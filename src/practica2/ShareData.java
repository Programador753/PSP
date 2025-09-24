package practica2;

import java.util.concurrent.Semaphore;

/**
 * Clase para almacenar todos los datos compartidos entre threads
 * con sus respectivos getters y setters para acceso sincronizado
 */
public class ShareData {
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

    // Getters y Setters para constantes
    public static int getN() {
        return N;
    }

    public static int getNumThreads() {
        return NUM_THREADS;
    }

    public static int getFilasPorThread() {
        return FILAS_POR_THREAD;
    }

    // Getters y Setters para la matriz
    public static float[][] getMatriz() {
        return matriz;
    }

    public static void setMatriz(float[][] matriz) {
        ShareData.matriz = matriz;
    }

    public static float getMatrizElement(int i, int j) {
        return matriz[i][j];
    }

    public static void setMatrizElement(int i, int j, float value) {
        matriz[i][j] = value;
    }

    // Getters y Setters para el vector
    public static float[] getVector() {
        return vector;
    }

    public static void setVector(float[] vector) {
        ShareData.vector = vector;
    }

    public static float getVectorElement(int i) {
        return vector[i];
    }

    public static void setVectorElement(int i, float value) {
        vector[i] = value;
    }

    // Getters y Setters para el resultado
    public static float[] getResultado() {
        return resultado;
    }

    public static void setResultado(float[] resultado) {
        ShareData.resultado = resultado;
    }

    public static float getResultadoElement(int i) {
        return resultado[i];
    }

    public static void setResultadoElement(int i, float value) {
        resultado[i] = value;
    }

    // Getters y Setters para threads finalizados
    public static int getThreadsFinalizados() {
        return threadsFinalizados;
    }

    public static void setThreadsFinalizados(int threadsFinalizados) {
        ShareData.threadsFinalizados = threadsFinalizados;
    }

    public static synchronized void incrementarThreadsFinalizados() {
        threadsFinalizados++;
    }

    // Getters y Setters para variables del thread más lento
    public static long getTMax() {
        return tMax;
    }

    public static void setTMax(long tMax) {
        ShareData.tMax = tMax;
    }

    public static int getIdMasLento() {
        return idMasLento;
    }

    public static void setIdMasLento(int idMasLento) {
        ShareData.idMasLento = idMasLento;
    }

    // Getter para el semáforo
    public static Semaphore getSemaforoTiempo() {
        return semaforoTiempo;
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

    /**
     * Pre: ---
     * Post: Inicializa la matriz y el vector con valores de prueba
     */
    public static void inicializarDatos() {
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
    public static double calcularModulo() {
        System.out.println("Calculando módulo del vector resultado...");
        double sumaCuadrados = 0.0;

        for (int i = 0; i < N; i++) {
            sumaCuadrados += resultado[i] * resultado[i];
        }

        return Math.sqrt(sumaCuadrados);
    }
}