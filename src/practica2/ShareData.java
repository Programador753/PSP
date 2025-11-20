package practica2;

import java.util.concurrent.Semaphore;

/**
 * Clase para almacenar todos los datos compartidos entre threads
 * con sus respectivos getters y setters para acceso sincronizado
 *
 * @author Sistema PSP
 * @version 1.0
 */
public class ShareData {
    // Dimensiones del problema
    private static final int N = 512;
    private static final int NUM_THREADS = 16;
    private static final int FILAS_POR_THREAD = N / NUM_THREADS; // 32 filas

    // Datos compartidos (ahora como variables de instancia)
    private float[][] matriz = new float[N][N];
    private float[] vector = new float[N];
    private float[] resultado = new float[N];

    // Variables de sincronización
    private volatile int threadsFinalizados = 0;

    // Variables para rastrear el thread más lento
    private long tMax = 0; // Tiempo máximo en nanosegundos
    private int idMasLento = -1; // ID del thread más lento
    private Semaphore semaforoTiempo = new Semaphore(1); // Para exclusión mutua

    // Getters para constantes (siguen siendo estáticos)
    public static int getN() {
        return N;
    }

    public static int getNumThreads() {
        return NUM_THREADS;
    }

    public static int getFilasPorThread() {
        return FILAS_POR_THREAD;
    }

    // Getters y Setters para la matriz (ahora de instancia)
    public float[][] getMatriz() {
        return matriz;
    }

    public void setMatriz(float[][] matriz) {
        this.matriz = matriz;
    }

    public float getMatrizElement(int i, int j) {
        return matriz[i][j];
    }

    public void setMatrizElement(int i, int j, float value) {
        matriz[i][j] = value;
    }

    // Getters y Setters para el vector (ahora de instancia)
    public float[] getVector() {
        return vector;
    }

    public void setVector(float[] vector) {
        this.vector = vector;
    }

    public float getVectorElement(int i) {
        return vector[i];
    }

    public void setVectorElement(int i, float value) {
        vector[i] = value;
    }

    // Getters y Setters para el resultado (ahora de instancia)
    public float[] getResultado() {
        return resultado;
    }

    public void setResultado(float[] resultado) {
        this.resultado = resultado;
    }

    public float getResultadoElement(int i) {
        return resultado[i];
    }

    public void setResultadoElement(int i, float value) {
        resultado[i] = value;
    }

    // Getters y Setters para threads finalizados (ahora de instancia)
    public int getThreadsFinalizados() {
        return threadsFinalizados;
    }

    public void setThreadsFinalizados(int threadsFinalizados) {
        this.threadsFinalizados = threadsFinalizados;
    }

    public synchronized void incrementarThreadsFinalizados() {
        threadsFinalizados++;
    }

    // Getters y Setters para variables del thread más lento (ahora de instancia)
    public long getTMax() {
        return tMax;
    }

    public void setTMax(long tMax) {
        this.tMax = tMax;
    }

    public int getIdMasLento() {
        return idMasLento;
    }

    public void setIdMasLento(int idMasLento) {
        this.idMasLento = idMasLento;
    }

    // Getter para el semáforo (ahora de instancia)
    public Semaphore getSemaforoTiempo() {
        return semaforoTiempo;
    }

    /**
     * Pre: tiempoEjecucion >= 0, idThread >= 0
     * Post: Actualiza tMax e idMasLento si este thread ha sido más lento
     * usando exclusión mutua con semáforo
     */
    public void actualizarThreadMasLento(long tiempoEjecucion, int idThread) {
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
    public void inicializarDatos() {
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
    public double calcularModulo() {
        System.out.println("Calculando módulo del vector resultado...");
        double sumaCuadrados = 0.0;

        for (int i = 0; i < N; i++) {
            sumaCuadrados += resultado[i] * resultado[i];
        }

        return Math.sqrt(sumaCuadrados);
    }
}