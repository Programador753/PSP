package practica2;

public class ThreadCalculador2 extends Thread {
    private int id;
    private int filaInicio;
    private int filaFin;
    private float[][] matriz;
    private float[] vector;
    private float[] resultado;

    /**
     * Pre: id >= 0, matriz != null, vector != null, resultado != null
     * Post: Crea un thread calculador para procesar las filas correspondientes
     *       y medir su tiempo de ejecución
     */
    public ThreadCalculador2(int id, float[][] matriz, float[] vector, float[] resultado) {
        this.id = id;
        this.matriz = matriz;
        this.vector = vector;
        this.resultado = resultado;
        this.filaInicio = id * Main2.getFilasPorThread();
        this.filaFin = filaInicio + Main2.getFilasPorThread();
    }

    /**
     * Pre: ---
     * Post: Calcula el producto parcial de las filas asignadas a este thread
     *       y actualiza las variables compartidas tMax e idMasLento usando
     *       exclusión mutua si este thread es el más lento hasta el momento
     */
    @Override
    public void run() {
        long tiempoInicio = System.nanoTime();

        System.out.println("Thread " + id + " iniciado - procesando filas " +
                filaInicio + " a " + (filaFin - 1));

        // Calcular producto parcial para las filas asignadas
        for (int i = filaInicio; i < filaFin; i++) {
            float suma = 0.0f;
            for (int j = 0; j < Main2.getN(); j++) {
                suma += matriz[i][j] * vector[j];
            }
            resultado[i] = suma;
        }

        long tiempoFin = System.nanoTime();
        long tiempoEjecucion = tiempoFin - tiempoInicio;

        System.out.println("Thread " + id + " finalizado - filas " +
                filaInicio + " a " + (filaFin - 1) + " completadas en " +
                (tiempoEjecucion / 1_000_000.0) + " ms");

        // Actualizar el thread más lento usando exclusión mutua
        Main2.actualizarThreadMasLento(tiempoEjecucion, id);

        // Sincronización: notificar finalización al proceso principal
        Main2.incrementarThreadsFinalizados();
    }
}