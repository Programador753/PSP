package practica3;

public class Main {
    // Constantes del sistema
    private static final int CAPACIDAD_COLA = 10;
    private static final int NUM_ESCRITORES = 4;
    private static final int NUM_LECTORES = 5;
    private static final int MENSAJES_POR_ESCRITOR = 8;
    private static final int MENSAJES_POR_LECTOR = 6;

    /**
     * Pre: ---
     * Post: Crea y ejecuta un sistema concurrente formado por 4 procesos escritores
     *       y 5 procesos lectores que operan sobre una cola FIFO compartida.
     *       Los escritores insertan 8 mensajes cada uno y los lectores extraen 6 cada uno.
     *       La cola tiene capacidad máxima de 10 mensajes y usa semáforos para sincronización.
     */
    public static void main(String[] args) {
        /*
         * Creación de los datos compartidos con la cola concurrente
         */
        ShareData datosCompartidos = new ShareData(CAPACIDAD_COLA);

        /*
         * Creación de arrays para almacenar los hilos
         */
        ProcesoEscritor[] escritores = new ProcesoEscritor[NUM_ESCRITORES];
        ProcesoLector[] lectores = new ProcesoLector[NUM_LECTORES];

        /*
         * Información inicial del sistema
         */
        mostrarInformacionInicial(datosCompartidos);

        /*
         * Creación e inicialización de los procesos escritores
         */
        for (int i = 0; i < NUM_ESCRITORES; i++) {
            escritores[i] = new ProcesoEscritor(datosCompartidos, i + 1, MENSAJES_POR_ESCRITOR);
            escritores[i].start();
        }

        /*
         * Creación e inicialización de los procesos lectores
         */
        for (int i = 0; i < NUM_LECTORES; i++) {
            lectores[i] = new ProcesoLector(datosCompartidos, i + 1, MENSAJES_POR_LECTOR);
            lectores[i].start();
        }

        /*
         * Esperar a que todos los hilos terminen su ejecución
         */
        try {
            /*
             * Esperamos a que terminen todos los escritores
             */
            for (ProcesoEscritor escritor : escritores) {
                escritor.join();
            }
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Todos los escritores han terminado");
            System.out.println("=".repeat(50));

            /*
             * Esperamos a que terminen todos los lectores
             */
            for (ProcesoLector lector : lectores) {
                lector.join();
            }
            System.out.println("\n" + "=".repeat(50));
            System.out.println("Todos los lectores han terminado");
            System.out.println("=".repeat(50));

            /*
             * Mostrar estado final de la cola
             */
            mostrarEstadoFinal(datosCompartidos);

        } catch (InterruptedException e) {
            /*
             * Manejo de interrupción del hilo principal
             */
            Thread.currentThread().interrupt();
            System.err.println("✗ El hilo principal fue interrumpido");
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("Sistema concurrente finalizado correctamente");
        System.out.println("=".repeat(50));
    }

    /**
     * Pre: datosCompartidos != null
     * Post: Muestra la información inicial del sistema concurrente.
     */
    private static void mostrarInformacionInicial(ShareData datosCompartidos) {
        try {
            System.out.println("=".repeat(50));
            System.out.println("SISTEMA CONCURRENTE CON SEMÁFOROS");
            System.out.println("=".repeat(50));
            System.out.println("Capacidad de la cola: " + datosCompartidos.getCapacidad() + " mensajes");
            System.out.println("Número de escritores: " + NUM_ESCRITORES);
            System.out.println("Mensajes por escritor: " + MENSAJES_POR_ESCRITOR);
            System.out.println("Total mensajes a insertar: " + (NUM_ESCRITORES * MENSAJES_POR_ESCRITOR));
            System.out.println("-".repeat(50));
            System.out.println("Número de lectores: " + NUM_LECTORES);
            System.out.println("Mensajes por lector: " + MENSAJES_POR_LECTOR);
            System.out.println("Total mensajes a extraer: " + (NUM_LECTORES * MENSAJES_POR_LECTOR));
            System.out.println("-".repeat(50));
            System.out.println("Mensajes que quedarán: " +
                    ((NUM_ESCRITORES * MENSAJES_POR_ESCRITOR) - (NUM_LECTORES * MENSAJES_POR_LECTOR)));
            System.out.println("=".repeat(50) + "\n");
        } catch (Exception e) {
            System.err.println("Error al mostrar información inicial");
        }
    }

    /**
     * Pre: datosCompartidos != null
     * Post: Muestra el estado final de la cola y extrae los mensajes restantes.
     */
    private static void mostrarEstadoFinal(ShareData datosCompartidos) {
        try {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("ESTADO FINAL DE LA COLA");
            System.out.println("=".repeat(50));
            System.out.println("Mensajes restantes: " + datosCompartidos.getTamaño());
            System.out.println(datosCompartidos.mostrarCola());
            System.out.println("-".repeat(50));

            /*
             * Mostrar y extraer todos los mensajes restantes
             */
            int contador = 1;
            while (!datosCompartidos.estaVacia()) {
                String mensajeRestante = datosCompartidos.extraer();
                System.out.println("Mensaje restante " + contador + ": " + mensajeRestante);
                contador++;
            }
            System.out.println("=".repeat(50));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("✗ Interrupción al extraer mensajes restantes");
        }
    }
}