package practica2;

import java.util.concurrent.Semaphore;

public class Main2 {
    // Dimensiones del problema
    private static final int N = 512;
    private static final int NUM_THREADS = 16;
    private static final int FILAS_POR_THREAD = N / NUM_THREADS; // 32 filas

    // Datos compartidos
    private static float[][] matriz = new float[N][N];
    private static float[] vector = new float[N];
    private static float[] resultado = new float[N];

    // Variables de sincronización
    private static volatile int threadsFinalizados = 0;

    // Variables para rastrear el thread más lento
    private static long tMax = 0; // Tiempo máximo en nanosegundos
    private static int idMasLento = -1; // ID del thread más lento
    private static Semaphore semaforoTiempo = new Semaphore(1); // Para exclusión mutua

    /**
     * Pre: ---
     * Post: Ejecuta el cálculo del producto matriz-vector usando 16 threads
     *       e identifica el thread más lento usando exclusión mutua con semáforo
     */
    public static void main(String[] args) {
        System.out.println("Iniciando cálculo del producto matriz-vector con " + NUM_THREADS + " threads");
        System.out.println("Identificando el thread más lento...");

        // Inicializar datos
        inicializarDatos();

        // Crear e iniciar los threads calculadores
        ThreadCalculador2[] threads = new ThreadCalculador2[NUM_THREADS];
        for (int i = 0; i < NUM_THREADS; i++) {
            threads[i] = new ThreadCalculador2(i, matriz, vector, resultado);
            threads[i].start();
        }

        // Proceso informador: espera activa hasta que todos terminen
        System.out.println("Proceso informador esperando finalización de threads...");
        while (threadsFinalizados < NUM_THREADS) {
            // Espera activa - comprueba continuamente
            Thread.yield(); // Cede el procesador para eficiencia
        }

        // Calcular módulo del vector resultado
        double modulo = calcularModulo();

        // Mostrar resultado
        System.out.println("Todos los threads han finalizado.");
        System.out.println("Módulo del vector resultado: " + modulo);
        System.out.println("Thread más lento: Thread " + idMasLento +
                " con tiempo: " + (tMax / 1_000_000.0) + " ms");
    }

    /**
     * Pre: ---
     * Post: Inicializa la matriz y el vector con valores de prueba
     */
    private static void inicializarDatos() {
        System.out.println("Inicializando matriz " + N + "x" + N + " y vector...");

        // Inicializar matriz con valores 1.0
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                matriz[i][j] = 1.0f;
            }
        }

        // Inicializar vector con valores 1.0
        for (int i = 0; i < N; i++) {
            vector[i] = 1.0f;
        }

        // Inicializar vector resultado a cero
        for (int i = 0; i < N; i++) {
            resultado[i] = 0.0f;
        }

        System.out.println("Datos inicializados correctamente.");
    }

    /**
     * Pre: El vector resultado debe estar calculado
     * Post: Devuelve el módulo del vector resultado
     */
    private static double calcularModulo() {
        System.out.println("Calculando módulo del vector resultado...");
        double sumaCuadrados = 0.0;

        for (int i = 0; i < N; i++) {
            sumaCuadrados += resultado[i] * resultado[i];
        }

        return Math.sqrt(sumaCuadrados);
    }

    /**
     * Pre: ---
     * Post: Incrementa de forma sincronizada el contador de threads finalizados
     */
    public static synchronized void incrementarThreadsFinalizados() {
        threadsFinalizados++;
    }

    /**
     * Pre: tiempoEjecucion >= 0, idThread >= 0
     * Post: Actualiza tMax e idMasLento si este thread ha sido más lento
     *       usando exclusión mutua con semáforo
     */
    public static void actualizarThreadMasLento(long tiempoEjecucion, int idThread) {
        try {
            // Adquirir el semáforo (entrar en sección crítica)
            semaforoTiempo.acquire();

            // Sección crítica: actualizar variables compartidas
            if (tiempoEjecucion > tMax) {
                tMax = tiempoEjecucion;
                idMasLento = idThread;
                System.out.println("Nuevo thread más lento: Thread " + idThread +
                        " con tiempo: " + (tiempoEjecucion / 1_000_000.0) + " ms");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            // Liberar el semáforo (salir de sección crítica)
            semaforoTiempo.release();
        }
    }

    // Getters para constantes
    public static int getN() {
        return N;
    }

    public static int getFilasPorThread() {
        return FILAS_POR_THREAD;
    }
}
