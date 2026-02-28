package Blockchain;

import java.util.Scanner;
import java.util.UUID;

/**
 * Punto de entrada de la aplicación blockchain P2P.
 * Cada ejecución de Main representa un nodo independiente en la red.
 * El usuario elige el puerto local y opcionalmente se conecta a un peer existente.
 *
 * USO:
 *   - Al iniciar se pide nombre y puerto del nodo.
 *   - Luego se elige: ser nodo principal (esperar conexiones) o conectarse a un nodo existente.
 *   - Si se elige conectar, se pide la IP y el puerto del nodo destino por separado.
 *
 * COMANDOS:
 *   /enviar <nombre_destino> <monto>          - Crea y propaga una transacción firmada al contacto
 *   /minar                                    - Mina un bloque con las transacciones del mempool
 *   /estado                                   - Muestra la cadena de bloques local
 *   /mempool                                  - Muestra las transacciones pendientes
 *   /peers                                    - Muestra los peers conectados
 *   /contactos                                - Muestra el directorio de contactos conocidos
 *   /clave                                    - Muestra tu clave pública (para que otros te envíen BTC)
 *   /validar                                  - Valida la integridad de la cadena local
 *   /conectar <ip:puerto>                     - Conecta a un nuevo peer
 *   /ayuda                                    - Muestra los comandos disponibles
 *   /salir                                    - Cierra el nodo
 */
public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║       BLOCKCHAIN P2P - Nodo Interactivo     ║");
        System.out.println("╚══════════════════════════════════════════════╝");
        System.out.println();

        // 1. Pedir nombre del nodo
        System.out.print("Nombre de este nodo (ej. Alice, Bob): ");
        String nombreNodo = scanner.nextLine().trim();

        // 2. Pedir puerto local
        System.out.print("Puerto de este nodo (ej. 8001): ");
        int puertoLocal = Integer.parseInt(scanner.nextLine().trim());

        // 3. Crear y arrancar el nodo
        Node nodo = new Node(nombreNodo, puertoLocal);
        nodo.iniciar();

        try { Thread.sleep(500); } catch (InterruptedException ignored) {} // Espera a que el servidor arranque

        // 4. Preguntar el rol del nodo
        System.out.println();
        System.out.println("¿Qué deseas hacer?");
        System.out.println("  1) Ser el nodo principal (esperar conexiones)");
        System.out.println("  2) Conectarme a un nodo existente");
        System.out.print("Opción (1/2): ");
        String opcion = scanner.nextLine().trim();

        if (opcion.equals("2")) {
            System.out.print("IP del nodo al que conectar (ej. localhost, 192.168.1.10): ");
            String ipRemota = scanner.nextLine().trim();
            System.out.print("Puerto del nodo al que conectar (ej. 8001): ");
            String puertoRemoto = scanner.nextLine().trim();

            String direccionRemota = ipRemota + ":" + puertoRemoto;
            nodo.conectarAPeer(direccionRemota);
            System.out.println("Conectado al peer en " + direccionRemota);
        } else {
            System.out.println("Nodo principal iniciado. Esperando conexiones de otros nodos...");
        }

        try { Thread.sleep(300); } catch (InterruptedException ignored) {}

        // 5. Mostrar información del nodo
        System.out.println();
        System.out.println("════════════════════════════════════════════════");
        System.out.println("  Nodo '" + nombreNodo + "' activo en puerto: " + puertoLocal);
        System.out.println("  Tu clave pública: " + nodo.getClavePublicaBase64().substring(0, 30) + "...");
        System.out.println("  Peers conocidos: " + nodo.getPeersConocidos());
        System.out.println("  Contactos: " + nodo.getContactos().keySet());
        System.out.println("  Bloques en cadena: " + nodo.getBlockchain().getSize());
        System.out.println("════════════════════════════════════════════════");
        System.out.println("  Escribe /ayuda para ver los comandos");
        System.out.println();

        // 5. Bucle de comandos interactivos
        while (true) {
            System.out.print("[" + nombreNodo + "@" + puertoLocal + "] > ");
            String linea = scanner.nextLine().trim();

            if (linea.isEmpty()) continue;

            String[] partes = linea.split("\\s+");
            String cmd = partes[0].toLowerCase();

            switch (cmd) {

                case "/enviar":
                    // /enviar <nombre_destino> <monto>
                    if (partes.length < 3) {
                        System.out.println("Uso: /enviar <nombre_destino> <monto>");
                        break;
                    }
                    try {
                        String nombreDestino = partes[1];
                        double monto = Double.parseDouble(partes[2]);

                        // Buscar la clave pública del destinatario por nombre
                        String destino = nodo.buscarContacto(nombreDestino);
                        if (destino == null) {
                            System.out.println("ERROR: Contacto '" + nombreDestino + "' no encontrado.");
                            System.out.println("Contactos disponibles: " + nodo.getContactos().keySet());
                            break;
                        }

                        // Verificar que no se envía a sí mismo
                        if (destino.equals(nodo.getClavePublicaBase64())) {
                            System.out.println("ERROR: No puedes enviarte BTC a ti mismo.");
                            break;
                        }

                        // Crear transacción con ID único
                        String txId = "tx-" + UUID.randomUUID().toString().substring(0, 8);
                        Transaction tx = new Transaction(
                                txId,
                                nodo.getClavePublicaBase64(),  // Emisor: nuestra clave pública en Base64
                                destino,                        // Receptor: clave pública del destino
                                monto
                        );

                        // Firmar la transacción con nuestra clave privada
                        tx.firmar(nodo.getParClaves().getPrivate());

                        // Verificar que la firma es correcta antes de enviar
                        if (!tx.verificarFirma()) {
                            System.out.println("ERROR: La firma generada no es válida.");
                            break;
                        }

                        // Añadir al mempool local (se verifica firma y se propaga automáticamente a toda la red)
                        nodo.recibirTransaccion(tx);


                        System.out.println("Transacción " + txId + " creada y propagada (" + monto + " BTC -> " + nombreDestino + ")");

                    } catch (NumberFormatException e) {
                        System.out.println("Error: El monto debe ser un número válido.");
                    }
                    break;

                case "/minar":
                    if (nodo.getMempool().estaVacio()) {
                        System.out.println("El mempool está vacío. No hay transacciones para minar.");
                        break;
                    }

                    // Verificar si ya hay un minado en curso
                    if (nodo.getMinerActual() != null && nodo.getMinerActual().isMinando()) {
                        System.out.println("Ya hay un proceso de minado en curso.");
                        break;
                    }

                    // Crear minero y registrarlo en el nodo para que pueda ser detenido
                    // si otro nodo mina el bloque primero
                    System.out.println("Iniciando proceso de minado en segundo plano...");
                    Miner miner = new Miner();
                    nodo.setMinerActual(miner);

                    // El minado se ejecuta en un hilo separado sin join() para no bloquear
                    // la consola, permitiendo que el nodo siga recibiendo comandos y bloques
                    new Thread(() -> {
                        String hashPrevio = nodo.getBlockchain().getUltimoBloque().getHash();
                        Block bloque = miner.minar(nodo.getMempool(), hashPrevio);

                        // Limpiar la referencia al minero activo
                        nodo.setMinerActual(null);

                        if (bloque != null) {
                            // El bloque se verifica, añade a la cadena y propaga a la red
                            nodo.recibirBloque(bloque);
                            System.out.println("Bloque minado y propagado a " + nodo.getPeersConocidos().size() + " peers");
                        }
                    }).start();
                    break;

                case "/estado":
                    System.out.println("═══ Cadena de Bloques (Nodo " + puertoLocal + ") ═══");
                    nodo.getBlockchain().imprimirCadena();
                    System.out.println("Total bloques: " + nodo.getBlockchain().getSize());
                    break;

                case "/mempool":
                    int pendientes = nodo.getMempool().size();
                    System.out.println("═══ Mempool ═══");
                    System.out.println("Transacciones pendientes: " + pendientes);
                    if (pendientes > 0) {
                        var txsPendientes = nodo.getMempool().obtenerPrimeras(pendientes);
                        for (Transaction t : txsPendientes) {
                            String emisorNombre = nodo.buscarNombrePorClave(t.getClavePublicaEmisor());
                            String receptorNombre = nodo.buscarNombrePorClave(t.getClavePublicaReceptor());
                            String emisorStr = emisorNombre != null ? emisorNombre : "desconocido";
                            String receptorStr = receptorNombre != null ? receptorNombre : "desconocido";
                            System.out.println("  TX " + t.getId() + " | " + emisorStr + " -> " + receptorStr + " | " + t.getMonto() + " BTC");
                        }
                    }
                    break;

                case "/contactos":
                    System.out.println("═══ Directorio de Contactos ═══");
                    if (nodo.getContactos().isEmpty()) {
                        System.out.println("  No hay contactos registrados.");
                    } else {
                        for (var entry : nodo.getContactos().entrySet()) {
                            String marca = entry.getKey().equals(nombreNodo) ? " (tú)" : "";
                            System.out.println("  " + entry.getKey() + marca + " -> " + entry.getValue().substring(0, 30) + "...");
                        }
                    }
                    break;

                case "/peers":
                    System.out.println("═══ Peers Conocidos ═══");
                    if (nodo.getPeersConocidos().isEmpty()) {
                        System.out.println("  No hay peers conectados.");
                    } else {
                        for (String peer : nodo.getPeersConocidos()) {
                            // Intentar resolver el nombre del peer por su puerto
                            System.out.println("  -> " + peer);
                        }
                    }
                    break;

                case "/clave":
                    System.out.println("Tu clave pública completa (compártela para recibir BTC):");
                    System.out.println(nodo.getClavePublicaBase64());
                    break;

                case "/validar":
                    boolean valida = nodo.getBlockchain().esValida();
                    System.out.println("Cadena válida: " + (valida ? "✓ SÍ" : "✗ NO"));
                    break;

                case "/conectar":
                    if (partes.length < 2) {
                        System.out.println("Uso: /conectar <ip:puerto> (ej. /conectar localhost:8001)");
                        break;
                    }
                    String direccion = partes[1];
                    if (!direccion.contains(":")) {
                        System.out.println("Formato inválido. Usa ip:puerto (ej. localhost:8001)");
                        break;
                    }
                    nodo.conectarAPeer(direccion);
                    break;

                case "/ayuda":
                    System.out.println("╔═══════════════════════════════════════════════════════════╗");
                    System.out.println("║  COMANDOS DISPONIBLES                                    ║");
                    System.out.println("╠═══════════════════════════════════════════════════════════╣");
                    System.out.println("║  /enviar <nombre_destino> <monto> Enviar BTC a contacto   ║");
                    System.out.println("║  /minar                          Minar un bloque          ║");
                    System.out.println("║  /estado                         Ver la cadena             ║");
                    System.out.println("║  /mempool                        Ver transacciones pend.   ║");
                    System.out.println("║  /peers                          Ver peers conectados      ║");
                    System.out.println("║  /contactos                      Ver directorio contactos  ║");
                    System.out.println("║  /clave                          Ver tu clave pública      ║");
                    System.out.println("║  /validar                        Validar la cadena         ║");
                    System.out.println("║  /conectar <ip:puerto>           Conectar a nuevo peer     ║");
                    System.out.println("║  /salir                          Cerrar el nodo            ║");
                    System.out.println("╚═══════════════════════════════════════════════════════════╝");
                    break;

                case "/salir":
                    System.out.println("Cerrando nodo...");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Comando desconocido. Escribe /ayuda para ver los comandos.");
                    break;
            }
        }
    }
}