package Blockchain;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Gestor de red P2P para un nodo de blockchain.
 * Maneja la comunicación entre nodos: servidor TCP, envío de mensajes,
 * descubrimiento de peers y sincronización de la blockchain.
 * Los peers se identifican con dirección "ip:puerto".
 */
public class NetworkManager {

    // El nodo local al que pertenece este gestor de red
    private Node nodoLocal;

    public NetworkManager(Node nodoLocal) {
        this.nodoLocal = nodoLocal;
    }

    // ═══════════════════════════════════════════════════════════
    //  UTILIDAD: extraer IP y puerto de una dirección "ip:puerto"
    // ═══════════════════════════════════════════════════════════

    private String extraerIP(String direccion) {
        return direccion.split(":")[0];
    }

    private int extraerPuerto(String direccion) {
        return Integer.parseInt(direccion.split(":")[1]);
    }

    // ═══════════════════════════════════════════════════════════
    //  SERVIDOR: escucha conexiones entrantes
    // ═══════════════════════════════════════════════════════════

    /**
     * Arranca el servidor TCP en un hilo daemon para escuchar conexiones entrantes.
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
     * Maneja una conexión entrante: lee el mensaje y lo procesa según su tipo.
     */
    private void manejarConexion(Socket socket) {
        try (ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())) {

            Object recibido = ois.readObject();

            if (recibido instanceof Mensaje mensaje) {
                procesarMensaje(mensaje, oos);
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
     * Procesa un mensaje recibido según su tipo.
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
                // Recibe una lista de direcciones (ip:puerto) de peers y los añade
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

                // También enviamos nuestros contactos automáticamente
                HashMap<String, String> nuestrosContactosPeer = new HashMap<>(nodoLocal.getContactos());
                oos.writeObject(new Mensaje(Mensaje.CONTACT_INFO, nuestrosContactosPeer));
                oos.flush();
                break;

            case Mensaje.SYNC_REQUEST:
                // Un nodo nuevo pide nuestra blockchain completa
                ArrayList<Block> cadena = nodoLocal.getBlockchain().getCadena();
                oos.writeObject(new Mensaje(Mensaje.SYNC_RESPONSE, cadena));
                oos.flush();
                System.out.println("[" + nodoLocal.getNombre() + "] Blockchain enviada a peer solicitante");
                break;

            case Mensaje.SYNC_RESPONSE:
                // Recibimos una blockchain completa (se procesa en conectarAPeer)
                break;

            case Mensaje.CONTACT_INFO:
                // Recibe un mapa de contactos (nombre -> clave pública)
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
                // Un nodo nuevo se ha unido a la red, recibimos su info: [dirección, nombre, clavePublica]
                @SuppressWarnings("unchecked")
                ArrayList<String> infoNuevoNodo = (ArrayList<String>) mensaje.getPayload();
                String direccionNuevo = infoNuevoNodo.get(0);
                String nombreNuevo = infoNuevoNodo.get(1);
                String claveNuevo = infoNuevoNodo.get(2);

                // Registrar como peer y contacto si no lo conocíamos
                boolean esPeerNuevo = !nodoLocal.getPeersConocidos().contains(direccionNuevo)
                        && !direccionNuevo.equals("localhost:" + nodoLocal.getPuerto());
                nodoLocal.agregarPeer(direccionNuevo);
                nodoLocal.registrarContacto(nombreNuevo, claveNuevo);

                // Re-propagar a nuestros peers si era realmente nuevo (flooding)
                if (esPeerNuevo) {
                    System.out.println("[" + nodoLocal.getNombre() + "] \uD83D\uDD14 Nodo nuevo en la red: "
                            + nombreNuevo + " (" + direccionNuevo + ")");
                    propagarMensaje(mensaje);
                }
                break;
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  CONEXIÓN A PEERS
    // ═══════════════════════════════════════════════════════════

    /**
     * Conecta este nodo a un peer remoto usando su dirección ip:puerto.
     * 1. Intercambia listas de peers y contactos
     * 2. Sincroniza la blockchain
     * 3. Envía nuestros contactos al peer
     * 4. Notifica a TODA la red que nos hemos unido
     */
    public void conectarAPeer(String direccionRemota) {
        String ip = extraerIP(direccionRemota);
        int puerto = extraerPuerto(direccionRemota);

        // Paso 1: Intercambiar lista de peers y contactos
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

            // Recibimos los contactos que el servidor nos envía automáticamente
            Object respContactos = ois.readObject();
            if (respContactos instanceof Mensaje msgContactos) {
                if (msgContactos.getTipo().equals(Mensaje.CONTACT_INFO)) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, String> contactosRemotos = (HashMap<String, String>) msgContactos.getPayload();
                    for (var entry : contactosRemotos.entrySet()) {
                        nodoLocal.registrarContacto(entry.getKey(), entry.getValue());
                    }
                }
            }

            // Añadimos el peer remoto
            nodoLocal.agregarPeer(direccionRemota);

        } catch (Exception e) {
            System.out.println("[Red] Error al intercambiar peers con " + direccionRemota + ": " + e.getMessage());
        }

        // Paso 2: Solicitar blockchain para sincronización
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
                        // Limpiar del mempool las transacciones que ya están confirmadas en la nueva cadena
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

        // Paso 3: Enviar nuestros contactos al peer remoto
        try (Socket socket = new Socket(ip, puerto);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            HashMap<String, String> nuestrosContactos = new HashMap<>(nodoLocal.getContactos());
            oos.writeObject(new Mensaje(Mensaje.CONTACT_INFO, nuestrosContactos));
            oos.flush();

            // Recibimos contactos actualizados del remoto
            Object respuesta = ois.readObject();
            if (respuesta instanceof Mensaje msg) {
                if (msg.getTipo().equals(Mensaje.CONTACT_INFO)) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, String> contactosRemotos = (HashMap<String, String>) msg.getPayload();
                    for (var entry : contactosRemotos.entrySet()) {
                        nodoLocal.registrarContacto(entry.getKey(), entry.getValue());
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("[Red] Error al intercambiar contactos con " + direccionRemota + ": " + e.getMessage());
        }

        // Paso 4: Notificar a TODA la red que nos hemos unido
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
     * Envía un mensaje a un peer remoto identificado por su dirección "ip:puerto".
     * Si el peer no responde (Connection refused), se elimina de la lista de peers conocidos.
     */
    public void enviarMensaje(Mensaje mensaje, String direccionDestino) {
        try {
            String ip = extraerIP(direccionDestino);
            int puerto = extraerPuerto(direccionDestino);
            try (Socket socket = new Socket(ip, puerto);
                 ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {
                oos.writeObject(mensaje);
                oos.flush();
            }
        } catch (ConnectException e) {
            // El peer no está disponible, eliminarlo de la lista
            nodoLocal.getPeersConocidos().remove(direccionDestino);
            System.out.println("[Red] Peer " + direccionDestino + " desconectado. Eliminado de la lista de peers.");
        } catch (IOException e) {
            System.out.println("[Red] Error al enviar mensaje a " + direccionDestino + ": " + e.getMessage());
        }
    }

    /**
     * Propaga un mensaje a todos los peers conocidos.
     */
    public void propagarMensaje(Mensaje mensaje) {
        for (String peer : nodoLocal.getPeersConocidos()) {
            enviarMensaje(mensaje, peer);
        }
    }

    /**
     * Propaga una transacción a todos los peers conocidos.
     */
    public void propagarTransaccion(Transaction tx) {
        propagarMensaje(new Mensaje(Mensaje.TRANSACTION, tx));
    }

    /**
     * Propaga un bloque minado a todos los peers conocidos.
     */
    public void propagarBloque(Block bloque) {
        propagarMensaje(new Mensaje(Mensaje.BLOCK, bloque));
    }
}