package practica2;

public class ThreadCalculador extends Thread {
    private int id;
    private int filaInicio;
    private int filaFin;
    private float[][] matriz;
    private float[] vector;
    private float[] resultado;

    /**
     * Pre: id >= 0, matriz != null, vector != null, resultado != null
     * Post: Crea un thread calculador para procesar las filas correspondientes
     */
    public ThreadCalculador(int id, float[][] matriz, float[] vector, float[] resultado) {
        this.id = id;
        this.matriz = matriz;
        this.vector = vector;
        this.resultado = resultado;
        this.filaInicio = id * Main1.getFilasPorThread();
        this.filaFin = filaInicio + Main1.getFilasPorThread();
    }

    /**
     * Pre: ---
     * Post: Calcula el producto parcial de las filas asignadas a este thread.
     *       Cada thread procesa exactamente 32 filas de la matriz sin
     *       interferencias con otros threads.
     */
    @Override
    public void run() {
        System.out.println("Thread " + id + " iniciado - procesando filas " +
                filaInicio + " a " + (filaFin - 1));

        // Calcular producto parcial para las filas asignadas
        for (int i = filaInicio; i < filaFin; i++) {
            float suma = 0.0f;
            for (int j = 0; j < Main1.getN(); j++) {
                suma += matriz[i][j] * vector[j];
            }
            resultado[i] = suma;
        }

        System.out.println("Thread " + id + " finalizado - filas " +
                filaInicio + " a " + (filaFin - 1) + " completadas");

        // Sincronización: notificar finalización al proceso principal
        Main1.incrementarThreadsFinalizados();
    }
}