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

    public Node(String nombre, int puerto) {
        this.nombre = nombre;
        this.puerto = puerto;
        this.blockchain = new Blockchain();
        this.mempool = new Mempool();
        this.peersConocidos = new CopyOnWriteArrayList<>();
        this.contactos = new ConcurrentHashMap<>();
        this.bloquesVistos = Collections.synchronizedSet(new HashSet<>());

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
     * Añade una dirección (ip:puerto) a la lista de peers conocidos si no existe ya.
     */
    public void agregarPeer(String direccionRemota) {
        String miDireccion = "localhost:" + this.puerto;
        if (!direccionRemota.equals(miDireccion) && !peersConocidos.contains(direccionRemota)) {
            peersConocidos.add(direccionRemota);
            System.out.println("[" + nombre + "] Nuevo peer descubierto: " + direccionRemota);
        }
    }

    /**
     * Recibe una transacción, verifica su firma digital y la añade al mempool si es válida.
     * Si es una transacción nueva, la re-propaga a todos los peers conocidos (flooding).
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
     * Recibe un bloque minado, lo verifica y lo añade a la cadena local.
     * Si es un bloque nuevo y válido, lo re-propaga a todos los peers (flooding).
     */
    public void recibirBloque(Block bloque) {
        // 0. Verificar si ya procesamos este bloque (evita re-propagación infinita)
        if (bloquesVistos.contains(bloque.getHash())) {
            return; // Ya lo vimos, ignorar silenciosamente
        }

        // 1. Verifica que el hash del bloque es correcto recalculándolo
        String hashCalculado = HashUtil.calcularHash(bloque.toString());
        if (!hashCalculado.equals(bloque.getHash())) {
            System.out.println("[" + nombre + "] Bloque rechazado: hash inválido");
            return;
        }

        // 2. Verifica que cumple la dificultad
        if (!bloque.getHash().startsWith(Blockchain.PREFIJO_DIFICULTAD)) {
            System.out.println("[" + nombre + "] Bloque rechazado: no cumple dificultad");
            return;
        }

        // 3. Verifica las firmas de todas las transacciones del bloque
        for (Transaction tx : bloque.getTransacciones()) {
            if (!tx.verificarFirma()) {
                System.out.println("[" + nombre + "] Bloque rechazado: contiene transacción con firma inválida");
                return;
            }
        }

        // 4. Marcar como visto
        bloquesVistos.add(bloque.getHash());

        // 5. Añade el bloque a su cadena local (protegido por semáforo)
        blockchain.agregarBloque(bloque);

        // 6. Limpia del mempool las transacciones ya confirmadas (protegido por semáforo)
        mempool.eliminarTransacciones(bloque.getTransacciones());

        System.out.println("[" + nombre + "] ✓ Bloque verificado y aceptado: " + bloque.getHash().substring(0, 20) + "..."
                + " | " + bloque.getTransacciones().size() + " transacciones");

        // 7. Re-propagar a todos los peers para que toda la red lo reciba
        networkManager.propagarBloque(bloque);
    }

    /**
     * Arranca el servidor del nodo para escuchar conexiones entrantes.
     */
    public void iniciar() {
        networkManager.iniciarServidor();
        System.out.println("[" + nombre + "] Nodo iniciado en puerto " + puerto);
        System.out.println("[" + nombre + "] Clave pública: " + clavePublicaBase64.substring(0, 20) + "...");
    }

    /**
     * Conecta este nodo a un peer remoto, intercambia lista de peers y sincroniza blockchain.
     * @param direccionRemota dirección del peer en formato "ip:puerto"
     */
    public void conectarAPeer(String direccionRemota) {
        networkManager.conectarAPeer(direccionRemota);
    }

    /**
     * Registra un contacto (nombre -> clave pública) en el directorio local.
     */
    public void registrarContacto(String nombreContacto, String clavePublicaBase64) {
        if (!contactos.containsKey(nombreContacto)) {
            contactos.put(nombreContacto, clavePublicaBase64);
            System.out.println("[" + nombre + "] Nuevo contacto registrado: " + nombreContacto);
        }
    }

    /**
     * Busca la clave pública de un contacto por su nombre.
     * @return La clave pública en Base64 o null si no se encuentra.
     */
    public String buscarContacto(String nombre) {
        return contactos.get(nombre);
    }

    /**
     * Busca el nombre de un contacto por su clave pública.
     * @return El nombre del contacto o null si no se encuentra.
     */
    public String buscarNombrePorClave(String clavePublica) {
        for (var entry : contactos.entrySet()) {
            if (entry.getValue().equals(clavePublica)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Getters
    public String getNombre() { return nombre; }
    public int getPuerto() { return puerto; }
    public Blockchain getBlockchain() { return blockchain; }
    public Mempool getMempool() { return mempool; }
    public CopyOnWriteArrayList<String> getPeersConocidos() { return peersConocidos; }
    public KeyPair getParClaves() { return parClaves; }
    public String getClavePublicaBase64() { return clavePublicaBase64; }
    public NetworkManager getNetworkManager() { return networkManager; }
    public ConcurrentHashMap<String, String> getContactos() { return contactos; }
}