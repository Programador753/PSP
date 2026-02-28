package Blockchain;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gestor de red P2P para un nodo de blockchain.
 * Maneja la comunicación entre nodos mediante TCP: servidor de escucha, envío de mensajes,
 * descubrimiento de peers, sincronización de la blockchain y del mempool.
 * Los peers se identifican con dirección "ip:puerto".
 */
public class NetworkManager {

    // El nodo local al que pertenece este gestor de red
    private Node nodoLocal;

    /**
     * Constructor de la clase NetworkManager.
     * Pre: El nodo local debe estar instanciado y correctamente inicializado.
     * Post: Se crea un gestor de red asociado al nodo proporcionado.
     */
    public NetworkManager(Node nodoLocal) {
        this.nodoLocal = nodoLocal;
    }

    /**
     * Método que extrae la parte de la IP de una dirección en formato "ip:puerto".
     * Pre: La dirección debe contener el carácter ':' separando IP y puerto.
     * Post: Retorna la cadena de texto con la IP (parte antes de ':').
     */
    private String extraerIP(String direccion) {
        return direccion.split(":")[0];
    }

    /**
     * Método que extrae el número de puerto de una dirección en formato "ip:puerto".
     * Pre: La dirección debe contener el carácter ':' seguido de un número de puerto válido.
     * Post: Retorna un entero con el número de puerto (parte después de ':').
     */
    private int extraerPuerto(String direccion) {
        return Integer.parseInt(direccion.split(":")[1]);
    }

    // ═══════════════════════════════════════════════════════════
    //  SERVIDOR
    // ═══════════════════════════════════════════════════════════

    /**
     * Método que arranca el servidor TCP en un hilo daemon para escuchar conexiones entrantes.
     * Cada conexión aceptada se maneja en un hilo independiente.
     * Pre: El puerto del nodo local debe estar disponible y no ocupado por otro proceso.
     * Post: El servidor queda escuchando conexiones entrantes de forma indefinida en segundo plano.
     */
    public void iniciarServidor() {
        Thread hiloServidor = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(nodoLocal.getPuerto())) {
                System.out.println("[Servidor] Escuchando en puerto " + nodoLocal.getPuerto());
                while (true) {
                    Socket cliente = serverSocket.accept();
                    // Cada conexión entrante se maneja en su propio hilo
                    new Thread(() -> manejarConexion(cliente)).start();
                }
            } catch (IOException e) {
                System.out.println("[Servidor] Error en puerto " + nodoLocal.getPuerto() + ": " + e.getMessage());
            }
        });
        hiloServidor.setDaemon(true);
        hiloServidor.start();
    }

    /**
     * Método que maneja una conexión entrante: lee el mensaje y lo procesa según su tipo.
     * Se crea ObjectOutputStream antes que ObjectInputStream para evitar deadlocks,
     * ya que ambos lados (cliente y servidor) escriben su cabecera primero.
     * Pre: El socket debe estar conectado y abierto.
     * Post: El mensaje recibido es procesado según su tipo y los recursos del socket se liberan.
     */
    private void manejarConexion(Socket socket) {
        try (ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            Object recibido = ois.readObject();

            if (recibido instanceof Mensaje mensaje) {
                procesarMensaje(mensaje, oos);

                // Enviar ACK para mensajes fire-and-forget (TRANSACTION y BLOCK)
                // Esto confirma al emisor que el mensaje fue recibido y procesado
                if (mensaje.getTipo().equals(Mensaje.TRANSACTION) ||
                        mensaje.getTipo().equals(Mensaje.BLOCK)) {
                    oos.writeObject(new Mensaje(Mensaje.ACK, "OK"));
                    oos.flush();
                }
            }

        } catch (Exception e) {
            if (!(e instanceof EOFException)) {
                System.out.println("[Red] Error en conexión: " + e.getMessage());
            }
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  PROCESAMIENTO DE MENSAJES
    // ═══════════════════════════════════════════════════════════

    /**
     * Método que procesa un mensaje recibido según su tipo.
     * Gestiona los tipos: TRANSACTION, BLOCK, PEER_LIST, SYNC_REQUEST, SYNC_RESPONSE,
     * CONTACT_INFO, NEW_NODE, SYNC_MEMPOOL_REQUEST y SYNC_MEMPOOL_RESPONSE.
     * Pre: El mensaje debe ser una instancia válida de Mensaje con tipo y payload correctos.
     *      El ObjectOutputStream debe estar abierto para poder enviar respuestas.
     * Post: El mensaje se procesa según su tipo: se añaden transacciones, bloques, peers o contactos
     *       al nodo local, y se envían respuestas al remitente cuando el protocolo lo requiere.
     */
    private void procesarMensaje(Mensaje mensaje, ObjectOutputStream oos) throws IOException {
        switch (mensaje.getTipo()) {

            case Mensaje.TRANSACTION:
                // Recibe una transacción y la añade al mempool (con verificación de firma)
                Transaction tx = (Transaction) mensaje.getPayload();
                nodoLocal.recibirTransaccion(tx);
                break;

            case Mensaje.BLOCK:
                // Recibe un bloque minado y lo valida/añade a la cadena
                Block bloque = (Block) mensaje.getPayload();
                nodoLocal.recibirBloque(bloque);
                break;

            case Mensaje.PEER_LIST:
                // Recibe una lista de direcciones de peers y los registra
                @SuppressWarnings("unchecked")
                ArrayList<String> peersRemotos = (ArrayList<String>) mensaje.getPayload();
                for (String peer : peersRemotos) {
                    nodoLocal.agregarPeer(peer);
                }
                // Responde con nuestra propia lista de peers + nuestra dirección
                ArrayList<String> nuestraPeerList = new ArrayList<>(nodoLocal.getPeersConocidos());
                nuestraPeerList.add("localhost:" + nodoLocal.getPuerto());
                oos.writeObject(new Mensaje(Mensaje.PEER_LIST, nuestraPeerList));
                oos.flush();

                // También enviamos todos nuestros contactos automáticamente
                HashMap<String, String> nuestrosContactosPeer = new HashMap<>(nodoLocal.getContactos());
                oos.writeObject(new Mensaje(Mensaje.CONTACT_INFO, nuestrosContactosPeer));
                oos.flush();
                break;

            case Mensaje.SYNC_REQUEST:
                // Un nodo nuevo pide nuestra blockchain completa para sincronizarse
                ArrayList<Block> cadena = nodoLocal.getBlockchain().getCadena();
                oos.writeObject(new Mensaje(Mensaje.SYNC_RESPONSE, cadena));
                oos.flush();
                System.out.println("[" + nodoLocal.getNombre() + "] Blockchain enviada a peer solicitante");
                break;

            case Mensaje.SYNC_RESPONSE:
                // Se procesa en conectarAPeer, no aquí
                break;

            case Mensaje.CONTACT_INFO:
                // Recibe un mapa de contactos (nombre -> clave pública) y los registra
                @SuppressWarnings("unchecked")
                HashMap<String, String> contactosRemotos = (HashMap<String, String>) mensaje.getPayload();
                for (var entry : contactosRemotos.entrySet()) {
                    nodoLocal.registrarContacto(entry.getKey(), entry.getValue());
                }
                // Responde con nuestros contactos
                HashMap<String, String> nuestrosContactos = new HashMap<>(nodoLocal.getContactos());
                oos.writeObject(new Mensaje(Mensaje.CONTACT_INFO, nuestrosContactos));
                oos.flush();
                break;

            case Mensaje.NEW_NODE:
                // Un nodo nuevo se ha unido a la red. Payload: [dirección, nombre, clavePublica]
                @SuppressWarnings("unchecked")
                ArrayList<String> infoNuevoNodo = (ArrayList<String>) mensaje.getPayload();
                String direccionNuevo = infoNuevoNodo.get(0);
                String nombreNuevo = infoNuevoNodo.get(1);
                String claveNuevo = infoNuevoNodo.get(2);

                // Ignorar si somos nosotros mismos
                if (direccionNuevo.equals("localhost:" + nodoLocal.getPuerto())) {
                    break;
                }

                // Registrar como peer y contacto
                nodoLocal.agregarPeer(direccionNuevo);
                nodoLocal.registrarContacto(nombreNuevo, claveNuevo);

                // Deduplicación basada en dirección del nodo nuevo (no en estado de peers)
                // para garantizar que siempre se re-propague a nodos que aún no lo recibieron
                boolean yaProcesado = !nodoLocal.getNodoNuevosVistos().add(direccionNuevo);
                if (!yaProcesado) {
                    System.out.println("[" + nodoLocal.getNombre() + "] Nodo nuevo en la red: "
                            + nombreNuevo + " (" + direccionNuevo + ")");
                    // Re-propagar a todos los peers (flooding)
                    propagarMensaje(mensaje);

                    // Enviar nuestros contactos al nodo nuevo en hilo aparte para no bloquear
                    new Thread(() -> {
                        try {
                            String ipNuevo = extraerIP(direccionNuevo);
                            int puertoNuevo = extraerPuerto(direccionNuevo);
                            try (Socket socketNuevo = new Socket(ipNuevo, puertoNuevo);
                                 ObjectOutputStream oosNuevo = new ObjectOutputStream(socketNuevo.getOutputStream());
                                 ObjectInputStream oisNuevo = new ObjectInputStream(socketNuevo.getInputStream())) {

                                HashMap<String, String> todosContactos = new HashMap<>(nodoLocal.getContactos());
                                oosNuevo.writeObject(new Mensaje(Mensaje.CONTACT_INFO, todosContactos));
                                oosNuevo.flush();

                                // Leer respuesta: el nodo nuevo responderá con sus contactos
                                Object respNuevo = oisNuevo.readObject();
                                if (respNuevo instanceof Mensaje msgNuevo) {
                                    if (msgNuevo.getTipo().equals(Mensaje.CONTACT_INFO)) {
                                        @SuppressWarnings("unchecked")
                                        HashMap<String, String> contactosDelNuevo = (HashMap<String, String>) msgNuevo.getPayload();
                                        for (var e : contactosDelNuevo.entrySet()) {
                                            nodoLocal.registrarContacto(e.getKey(), e.getValue());
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // No es crítico si falla, el nodo ya tiene contactos del paso de conexión
                        }
                    }).start();
                }
                break;

            case Mensaje.SYNC_MEMPOOL_REQUEST:
                // Un nodo nuevo pide las transacciones pendientes del mempool
                int totalPendientes = nodoLocal.getMempool().size();
                ArrayList<Transaction> txsPendientes = new ArrayList<>(nodoLocal.getMempool().obtenerPrimeras(totalPendientes));
                oos.writeObject(new Mensaje(Mensaje.SYNC_MEMPOOL_RESPONSE, txsPendientes));
                oos.flush();
                System.out.println("[" + nodoLocal.getNombre() + "] Mempool enviado a peer solicitante (" + txsPendientes.size() + " transacciones)");
                break;

            case Mensaje.SYNC_MEMPOOL_RESPONSE:
                // Se procesa en conectarAPeer, no aquí
                break;
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  CONEXIÓN A PEERS
    // ═══════════════════════════════════════════════════════════

    /**
     * Método que conecta este nodo a un peer remoto usando su dirección ip:puerto.
     * Realiza 5 pasos: intercambio de peers y contactos, sincronización de blockchain,
     * intercambio de contactos con todos los peers descubiertos, sincronización del mempool
     * desde todos los peers, y notificación a toda la red de nuestra incorporación.
     * Pre: La dirección remota debe estar en formato "ip:puerto" válido y el peer debe estar escuchando.
     * Post: El nodo queda conectado a la red con peers, contactos, blockchain y mempool sincronizados.
     *       Todos los nodos de la red son notificados de la nueva incorporación.
     */
    public void conectarAPeer(String direccionRemota) {
        String ip = extraerIP(direccionRemota);
        int puerto = extraerPuerto(direccionRemota);

        // Paso 1: Intercambiar lista de peers y contactos con el nodo inicial
        try (Socket socket = new Socket(ip, puerto);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            // Enviamos nuestra lista de peers + nuestra dirección
            ArrayList<String> nuestraPeerList = new ArrayList<>(nodoLocal.getPeersConocidos());
            nuestraPeerList.add("localhost:" + nodoLocal.getPuerto());
            oos.writeObject(new Mensaje(Mensaje.PEER_LIST, nuestraPeerList));
            oos.flush();

            // Recibimos la lista de peers del remoto
            Object respuesta = ois.readObject();
            if (respuesta instanceof Mensaje msg) {
                if (msg.getTipo().equals(Mensaje.PEER_LIST)) {
                    @SuppressWarnings("unchecked")
                    ArrayList<String> peersRemotos = (ArrayList<String>) msg.getPayload();
                    for (String peer : peersRemotos) {
                        nodoLocal.agregarPeer(peer);
                    }
                }
            }

            // Recibimos los contactos que el servidor envía automáticamente tras el PEER_LIST
            Object respContactos = ois.readObject();
            if (respContactos instanceof Mensaje msgContactos) {
                if (msgContactos.getTipo().equals(Mensaje.CONTACT_INFO)) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, String> contactosRemotos2 = (HashMap<String, String>) msgContactos.getPayload();
                    for (var entry : contactosRemotos2.entrySet()) {
                        nodoLocal.registrarContacto(entry.getKey(), entry.getValue());
                    }
                }
            }

            // Añadimos el peer remoto a nuestra lista
            nodoLocal.agregarPeer(direccionRemota);

        } catch (Exception e) {
            System.out.println("[Red] Error al intercambiar peers con " + direccionRemota + ": " + e.getMessage());
        }

        // Paso 2: Solicitar blockchain completa para sincronización
        try (Socket socket = new Socket(ip, puerto);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            oos.writeObject(new Mensaje(Mensaje.SYNC_REQUEST, null));
            oos.flush();

            Object respuesta = ois.readObject();
            if (respuesta instanceof Mensaje msg) {
                if (msg.getTipo().equals(Mensaje.SYNC_RESPONSE)) {
                    @SuppressWarnings("unchecked")
                    ArrayList<Block> cadenaRemota = (ArrayList<Block>) msg.getPayload();
                    boolean reemplazada = nodoLocal.getBlockchain().reemplazarCadena(cadenaRemota);
                    if (reemplazada) {
                        // Limpiar del mempool las transacciones ya confirmadas en la nueva cadena
                        ArrayList<Transaction> confirmadas = nodoLocal.getBlockchain().getTransaccionesConfirmadas();
                        nodoLocal.getMempool().eliminarTransacciones(confirmadas);
                        System.out.println("[" + nodoLocal.getNombre() + "] Mempool limpiado: eliminadas "
                                + confirmadas.size() + " transacciones ya confirmadas en la cadena");
                    } else {
                        System.out.println("[" + nodoLocal.getNombre() + "] Cadena local es igual o más larga, no se reemplaza");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("[Red] Error al sincronizar blockchain con " + direccionRemota + ": " + e.getMessage());
        }

        // Paso 3: Intercambiar contactos con TODOS los peers descubiertos (no solo el inicial)
        for (String peer : new ArrayList<>(nodoLocal.getPeersConocidos())) {
            try (Socket socket = new Socket(extraerIP(peer), extraerPuerto(peer));
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                HashMap<String, String> nuestrosContactos = new HashMap<>(nodoLocal.getContactos());
                oos.writeObject(new Mensaje(Mensaje.CONTACT_INFO, nuestrosContactos));
                oos.flush();

                // Recibimos contactos del peer
                Object respuesta = ois.readObject();
                if (respuesta instanceof Mensaje msg) {
                    if (msg.getTipo().equals(Mensaje.CONTACT_INFO)) {
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> contactosRemotos3 = (HashMap<String, String>) msg.getPayload();
                        for (var entry : contactosRemotos3.entrySet()) {
                            nodoLocal.registrarContacto(entry.getKey(), entry.getValue());
                        }
                    }
                }

            } catch (ConnectException e) {
                nodoLocal.getPeersConocidos().remove(peer);
                System.out.println("[Red] Peer " + peer + " no disponible, eliminado de la lista.");
            } catch (Exception e) {
                System.out.println("[Red] Error al intercambiar contactos con " + peer + ": " + e.getMessage());
            }
        }

        // Paso 4: Sincronizar mempool desde TODOS los peers (no solo el inicial)
        for (String peer : new ArrayList<>(nodoLocal.getPeersConocidos())) {
            try (Socket socket = new Socket(extraerIP(peer), extraerPuerto(peer));
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                oos.writeObject(new Mensaje(Mensaje.SYNC_MEMPOOL_REQUEST, null));
                oos.flush();

                Object respuesta = ois.readObject();
                if (respuesta instanceof Mensaje msg) {
                    if (msg.getTipo().equals(Mensaje.SYNC_MEMPOOL_RESPONSE)) {
                        @SuppressWarnings("unchecked")
                        ArrayList<Transaction> txsPendientes = (ArrayList<Transaction>) msg.getPayload();
                        int nuevas = 0;
                        for (Transaction txPend : txsPendientes) {
                            if (txPend.verificarFirma()) {
                                boolean esNueva = nodoLocal.getMempool().agregarTransaccion(txPend);
                                if (esNueva) nuevas++;
                            }
                        }
                        if (nuevas > 0) {
                            System.out.println("[" + nodoLocal.getNombre() + "] Mempool sincronizado desde " + peer + ": " + nuevas + " transacciones nuevas");
                        }
                    }
                }

            } catch (ConnectException e) {
                nodoLocal.getPeersConocidos().remove(peer);
            } catch (Exception e) {
                System.out.println("[Red] Error al sincronizar mempool con " + peer + ": " + e.getMessage());
            }
        }

        // Paso 5: Notificar a TODA la red que nos hemos unido
        // Payload: [dirección, nombre, clavePublica]
        ArrayList<String> miInfo = new ArrayList<>();
        miInfo.add("localhost:" + nodoLocal.getPuerto());
        miInfo.add(nodoLocal.getNombre());
        miInfo.add(nodoLocal.getClavePublicaBase64());
        Mensaje notificacion = new Mensaje(Mensaje.NEW_NODE, miInfo);
        propagarMensaje(notificacion);

        System.out.println("[" + nodoLocal.getNombre() + "] Conectado a la red. Todos los nodos han sido notificados.");
    }

    // ═══════════════════════════════════════════════════════════
    //  ENVÍO Y PROPAGACIÓN DE MENSAJES
    // ═══════════════════════════════════════════════════════════

    /**
     * Método que envía un mensaje a un peer remoto identificado por su dirección.
     * Espera un ACK de confirmación para asegurar que el mensaje fue recibido correctamente.
     * Pre: El mensaje debe ser una instancia válida de Mensaje. La dirección debe estar en formato "ip:puerto".
     * Post: El mensaje se envía al peer. Si el peer no está disponible, se elimina de la lista de peers.
     */
    public void enviarMensaje(Mensaje mensaje, String direccionDestino) {
        try {
            String ip = extraerIP(direccionDestino);
            int puerto = extraerPuerto(direccionDestino);
            try (Socket socket = new Socket(ip, puerto);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

                // Enviar el mensaje
                oos.writeObject(mensaje);
                oos.flush();

                // Esperar ACK para mensajes fire-and-forget (TRANSACTION y BLOCK)
                if (mensaje.getTipo().equals(Mensaje.TRANSACTION) ||
                        mensaje.getTipo().equals(Mensaje.BLOCK)) {

                    socket.setSoTimeout(2000); // Timeout de 2 segundos
                    try {
                        Object respuesta = ois.readObject();
                        if (respuesta instanceof Mensaje ack && ack.getTipo().equals(Mensaje.ACK)) {
                            // Mensaje recibido y procesado correctamente
                        }
                    } catch (SocketTimeoutException e) {
                        // El peer no respondió a tiempo, pero probablemente procesó el mensaje
                        System.out.println("[Red] Timeout esperando ACK de " + direccionDestino);
                    }
                }

            }
        } catch (ConnectException e) {
            nodoLocal.getPeersConocidos().remove(direccionDestino);
            System.out.println("[Red] Peer " + direccionDestino + " desconectado. Eliminado de la lista de peers.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("[Red] Error al enviar mensaje a " + direccionDestino + ": " + e.getMessage());
        }
    }

    /**
     * Método que propaga un mensaje a todos los peers conocidos en hilos paralelos.
     * Cada envío se realiza en un hilo independiente para evitar que un peer lento
     * bloquee la propagación al resto de nodos de la red.
     * Pre: El mensaje debe ser una instancia válida de Mensaje. La lista de peers debe estar inicializada.
     * Post: Se lanza un hilo por cada peer conocido para enviar el mensaje de forma concurrente.
     */
    public void propagarMensaje(Mensaje mensaje) {
        for (String peer : new ArrayList<>(nodoLocal.getPeersConocidos())) {
            new Thread(() -> enviarMensaje(mensaje, peer)).start();
        }
    }


    /**
     * Método que propaga una transacción a todos los peers conocidos mediante flooding.
     * Pre: La transacción debe estar firmada y ser válida.
     * Post: Se envía un mensaje de tipo TRANSACTION a todos los peers en hilos paralelos.
     */
    public void propagarTransaccion(Transaction tx) {
        propagarMensaje(new Mensaje(Mensaje.TRANSACTION, tx));
    }

    /**
     * Método que propaga un bloque minado a todos los peers conocidos mediante flooding.
     * Pre: El bloque debe tener un hash válido que cumpla la dificultad requerida.
     * Post: Se envía un mensaje de tipo BLOCK a todos los peers en hilos paralelos.
     */
    public void propagarBloque(Block bloque) {
        propagarMensaje(new Mensaje(Mensaje.BLOCK, bloque));
    }
}
