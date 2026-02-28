package Blockchain;

import java.security.KeyPair;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Representa un nodo en la red P2P de blockchain.
 * Cada nodo tiene su propia blockchain, mempool, par de claves y lista de peers conocidos.
 * Actúa simultáneamente como cliente y servidor.
 */
public class Node {

    // Nombre del nodo (contacto)
    private String nombre;

    // Puerto donde escucha este nodo
    private int puerto;

    // Cada nodo tiene su propia copia de la blockchain
    private Blockchain blockchain;

    // Cada nodo tiene su propio mempool de transacciones pendientes
    private Mempool mempool;

    // Lista de direcciones de nodos conocidos (peers) en formato "ip:puerto" - thread-safe
    private CopyOnWriteArrayList<String> peersConocidos;

    // Par de claves del nodo (identidad criptográfica)
    private KeyPair parClaves;

    // Clave pública en Base64 para mostrar y usar en transacciones
    private String clavePublicaBase64;

    // Gestor de red para comunicación P2P
    private NetworkManager networkManager;

    // Directorio de contactos: nombre -> clave pública Base64
    private ConcurrentHashMap<String, String> contactos;

    // Set de hashes de bloques ya procesados (evita re-propagación infinita)
    private Set<String> bloquesVistos;

    // Set de direcciones de NEW_NODE ya procesados (deduplicación robusta)
    private Set<String> nodoNuevosVistos;

    // Referencia al minero actual para poder detenerlo si otro nodo mina primero
    private volatile Miner minerActual;

    public Node(String nombre, int puerto) {
        this.nombre = nombre;
        this.puerto = puerto;
        this.blockchain = new Blockchain();
        this.mempool = new Mempool();
        this.peersConocidos = new CopyOnWriteArrayList<>();
        this.contactos = new ConcurrentHashMap<>();
        this.bloquesVistos = Collections.synchronizedSet(new HashSet<>());
        this.nodoNuevosVistos = Collections.synchronizedSet(new HashSet<>());

        // Genera el par de claves criptográficas del nodo
        this.parClaves = KeyPairUtil.generarParClaves();
        this.clavePublicaBase64 = Transaction.clavePublicaABase64(parClaves.getPublic());

        // Registrarse a sí mismo en el directorio de contactos
        this.contactos.put(nombre, clavePublicaBase64);

        // Inicializa la cadena con el bloque génesis
        this.blockchain.inicializar();

        // Crea el gestor de red para este nodo
        this.networkManager = new NetworkManager(this);
    }

    /**
     * Método que añade una dirección (ip:puerto) a la lista de peers conocidos si no existe ya.
     * Pre: La dirección remota debe estar en formato "ip:puerto" válido.
     * Post: Si la dirección no es la propia y no estaba registrada, se añade a la lista de peers conocidos.
     */
    public void agregarPeer(String direccionRemota) {
        String miDireccion = "localhost:" + this.puerto;
        if (!direccionRemota.equals(miDireccion) && !peersConocidos.contains(direccionRemota)) {
            peersConocidos.add(direccionRemota);
            System.out.println("[" + nombre + "] Nuevo peer descubierto: " + direccionRemota);
        }
    }

    /**
     * Método que recibe una transacción, verifica su firma digital y la añade al mempool si es válida.
     * Si es una transacción nueva, la re-propaga a todos los peers conocidos mediante flooding.
     * Pre: La transacción debe estar instanciada y contener todos los campos necesarios.
     * Post: Si la firma es válida y el monto positivo, la transacción se añade al mempool y se propaga.
     *       Si la transacción ya existía, se ignora sin re-propagar para evitar bucles infinitos.
     */
    public void recibirTransaccion(Transaction tx) {
        // 1. Verificar que la transacción está firmada correctamente
        if (!tx.verificarFirma()) {
            System.out.println("[" + nombre + "] Transacción RECHAZADA (firma inválida): " + tx.getId());
            return;
        }

        // 2. Verificar que el monto es positivo
        if (tx.getMonto() <= 0) {
            System.out.println("[" + nombre + "] Transacción RECHAZADA (monto inválido): " + tx.getId());
            return;
        }

        // 3. Añade al mempool (el mempool ya controla duplicados y usa semáforo)
        boolean esNueva = mempool.agregarTransaccion(tx);

        if (esNueva) {
            // Resolver nombres del emisor y receptor para el log
            String emisorNombre = buscarNombrePorClave(tx.getClavePublicaEmisor());
            String receptorNombre = buscarNombrePorClave(tx.getClavePublicaReceptor());
            String emisorStr = emisorNombre != null ? emisorNombre : "desconocido";
            String receptorStr = receptorNombre != null ? receptorNombre : "desconocido";

            System.out.println("[" + nombre + "] ✓ Transacción verificada y aceptada: " + tx.getId()
                    + " | " + emisorStr + " -> " + receptorStr + " | " + tx.getMonto() + " BTC");

            // 4. Re-propagar a todos los peers (flooding) para que toda la red la reciba
            networkManager.propagarTransaccion(tx);
        }
        // Si no es nueva, ya la teníamos → no re-propagar (evita bucles infinitos)
    }

    /**
     * Método que recibe un bloque minado, lo verifica y lo añade a la cadena local.
     * Si es un bloque nuevo y válido, detiene el minero local y lo re-propaga a todos los peers.
     * Pre: El bloque debe estar instanciado con hash, hashPrevio, transacciones y nonce válidos.
     * Post: Si el bloque es válido y encadena correctamente, se añade a la cadena local,
     *       se limpian las transacciones confirmadas del mempool, se detiene el minero local
     *       si estaba activo y se propaga a todos los peers. Si el bloque es inválido, se rechaza.
     */
    public void recibirBloque(Block bloque) {
        // 1. Verificar si ya procesamos este bloque (evita re-propagación infinita)
        if (bloquesVistos.contains(bloque.getHash())) {
            return;
        }

        // 1.5. Validación especial para el bloque génesis
        if (bloque.getHashPrevio() == null) {
            // Si es un génesis, debe coincidir con el de la red
            if (!bloque.getHash().equals(Blockchain.GENESIS_HASH)) {
                System.out.println("[" + nombre + "] Bloque génesis rechazado: hash no coincide con el de la red");
                System.out.println("   Esperado: " + Blockchain.GENESIS_HASH);
                System.out.println("   Recibido: " + bloque.getHash());
                return;
            }
            // Si ya tenemos el génesis, no lo procesamos de nuevo
            if (blockchain.getSize() > 0) {
                return;
            }
        }

        // 2. Verificación externa: re-hashear el contenido del bloque con el nonce proporcionado
        String hashCalculado = HashUtil.calcularHash(bloque.toString());
        if (!hashCalculado.equals(bloque.getHash())) {
            System.out.println("[" + nombre + "] Bloque rechazado: hash inválido");
            return;
        }

        // 3. Verificar que cumple la condición de dificultad (empieza con N ceros)
        if (!bloque.getHash().startsWith(Blockchain.PREFIJO_DIFICULTAD)) {
            System.out.println("[" + nombre + "] Bloque rechazado: no cumple dificultad");
            return;
        }

        // 4. Verificar las firmas de todas las transacciones del bloque
        for (Transaction tx : bloque.getTransacciones()) {
            if (!tx.verificarFirma()) {
                System.out.println("[" + nombre + "] Bloque rechazado: contiene transacción con firma inválida");
                return;
            }
        }

        // 5. Marcar como visto para no procesarlo dos veces
        bloquesVistos.add(bloque.getHash());

        // 6. Detener el minero local si estaba minando (otro nodo encontró la solución primero)
        if (minerActual != null && minerActual.isMinando()) {
            minerActual.detener();
        }

        // 7. Intentar añadir el bloque de forma atómica:
        //    agregarBloque verifica hashPrevio DENTRO del semáforo para evitar que dos bloques
        //    con el mismo hashPrevio se añadan simultáneamente (condición de carrera).
        //    Solo el primer minero en adquirir el semáforo gana.
        boolean anadido = blockchain.agregarBloque(bloque);
        if (!anadido) {
            System.out.println("[" + nombre + "] Bloque rechazado: otro bloque fue aceptado antes en esa posición");
            // CORRECCIÓN: Limpiar mempool aunque el bloque sea rechazado
            mempool.eliminarTransacciones(bloque.getTransacciones());
            return;
        }

        // 8. Limpieza del mempool: eliminar las transacciones ya confirmadas en el bloque
        mempool.eliminarTransacciones(bloque.getTransacciones());

        System.out.println("[" + nombre + "] ✓ Bloque aceptado: " + bloque.getHash().substring(0, 20) + "..."
                + " | " + bloque.getTransacciones().size() + " transacciones");

        // 9. Propagación: enviar el bloque a toda la red (flooding)
        networkManager.propagarBloque(bloque);
    }

    /**
     * Método que arranca el servidor TCP del nodo para escuchar conexiones entrantes.
     * Pre: El NetworkManager debe estar inicializado y el puerto debe estar disponible.
     * Post: El servidor del nodo queda escuchando en el puerto configurado y se muestra la clave pública.
     */
    public void iniciar() {
        networkManager.iniciarServidor();
        System.out.println("[" + nombre + "] Nodo iniciado en puerto " + puerto);
        System.out.println("[" + nombre + "] Clave pública: " + clavePublicaBase64.substring(0, 20) + "...");
    }

    /**
     * Método que conecta este nodo a un peer remoto, intercambia lista de peers y sincroniza blockchain.
     * Pre: La dirección remota debe estar en formato "ip:puerto" y el peer remoto debe estar escuchando.
     * Post: Se intercambian peers, contactos, blockchain y mempool con el peer remoto y se notifica a la red.
     * @param direccionRemota Dirección del peer en formato "ip:puerto".
     */
    public void conectarAPeer(String direccionRemota) {
        networkManager.conectarAPeer(direccionRemota);
    }

    /**
     * Método que registra un contacto (nombre -> clave pública) en el directorio local.
     * Pre: El nombre del contacto y la clave pública en Base64 no deben ser nulos.
     * Post: Si el contacto no existía, se añade al directorio. Si ya existía, no se modifica.
     */
    public void registrarContacto(String nombreContacto, String clavePublicaBase64) {
        if (!contactos.containsKey(nombreContacto)) {
            contactos.put(nombreContacto, clavePublicaBase64);
            System.out.println("[" + nombre + "] Nuevo contacto registrado: " + nombreContacto);
        }
    }

    /**
     * Método que busca la clave pública de un contacto por su nombre.
     * Pre: El nombre no debe ser nulo.
     * Post: Retorna la clave pública en Base64 del contacto o null si no se encuentra.
     */
    public String buscarContacto(String nombre) {
        return contactos.get(nombre);
    }

    /**
     * Método que busca el nombre de un contacto por su clave pública.
     * Pre: La clave pública en Base64 no debe ser nula.
     * Post: Retorna el nombre del contacto asociado a esa clave o null si no se encuentra.
     */
    public String buscarNombrePorClave(String clavePublica) {
        for (var entry : contactos.entrySet()) {
            if (entry.getValue().equals(clavePublica)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Método que devuelve el nombre del nodo.
     * Pre: El objeto Node debe estar instanciado.
     * Post: Retorna una cadena de texto con el nombre identificador del nodo.
     */
    public String getNombre() { return nombre; }

    /**
     * Método que devuelve el puerto en el que escucha el nodo.
     * Pre: El objeto Node debe estar instanciado.
     * Post: Retorna un entero con el número de puerto del servidor TCP del nodo.
     */
    public int getPuerto() { return puerto; }

    /**
     * Método que devuelve la blockchain local del nodo.
     * Pre: El objeto Node debe estar instanciado y la blockchain inicializada.
     * Post: Retorna la referencia a la cadena de bloques local del nodo.
     */
    public Blockchain getBlockchain() { return blockchain; }

    /**
     * Método que devuelve el mempool del nodo.
     * Pre: El objeto Node debe estar instanciado.
     * Post: Retorna la referencia al mempool de transacciones pendientes del nodo.
     */
    public Mempool getMempool() { return mempool; }

    /**
     * Método que devuelve la lista de peers conocidos.
     * Pre: El objeto Node debe estar instanciado.
     * Post: Retorna la lista thread-safe de direcciones "ip:puerto" de peers conocidos.
     */
    public CopyOnWriteArrayList<String> getPeersConocidos() { return peersConocidos; }

    /**
     * Método que devuelve el par de claves criptográficas del nodo.
     * Pre: El objeto Node debe estar instanciado.
     * Post: Retorna el KeyPair con la clave pública y privada del nodo.
     */
    public KeyPair getParClaves() { return parClaves; }

    /**
     * Método que devuelve la clave pública del nodo en formato Base64.
     * Pre: El objeto Node debe estar instanciado.
     * Post: Retorna una cadena Base64 que representa la clave pública del nodo.
     */
    public String getClavePublicaBase64() { return clavePublicaBase64; }

    /**
     * Método que devuelve el gestor de red del nodo.
     * Pre: El objeto Node debe estar instanciado.
     * Post: Retorna la referencia al NetworkManager encargado de la comunicación P2P.
     */
    public NetworkManager getNetworkManager() { return networkManager; }

    /**
     * Método que devuelve el directorio de contactos del nodo.
     * Pre: El objeto Node debe estar instanciado.
     * Post: Retorna el mapa concurrente nombre -> clave pública Base64 de los contactos conocidos.
     */
    public ConcurrentHashMap<String, String> getContactos() { return contactos; }

    /**
     * Método que devuelve el conjunto de direcciones de nodos nuevos ya procesados.
     * Pre: El objeto Node debe estar instanciado.
     * Post: Retorna el set sincronizado de direcciones cuyo mensaje NEW_NODE ya fue procesado.
     */
    public Set<String> getNodoNuevosVistos() { return nodoNuevosVistos; }

    /**
     * Método que devuelve el minero actualmente en ejecución.
     * Pre: El objeto Node debe estar instanciado.
     * Post: Retorna la referencia al Miner activo, o null si no se está minando.
     */
    public Miner getMinerActual() { return minerActual; }

    /**
     * Método que establece el minero activo del nodo.
     * Pre: El parámetro miner puede ser un objeto Miner o null para indicar que no hay minado activo.
     * Post: La referencia al minero activo queda actualizada con el valor proporcionado.
     */
    public void setMinerActual(Miner miner) { this.minerActual = miner; }
}