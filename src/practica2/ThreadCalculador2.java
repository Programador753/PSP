package practica2;

public class ThreadCalculador2 extends Thread {
    private int id;
    private int filaInicio;
    private int filaFin;

    /**
     * Pre: id >= 0
     * Post: Crea un thread calculador para procesar las filas correspondientes
     *       y medir su tiempo de ejecución, accediendo a los datos compartidos
     *       a través de ShareData
     */
    public ThreadCalculador2(int id) {
        this.id = id;
        this.filaInicio = id * ShareData.getFilasPorThread();
        this.filaFin = filaInicio + ShareData.getFilasPorThread();
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
            for (int j = 0; j < ShareData.getN(); j++) {
                suma += ShareData.getMatrizElement(i, j) * ShareData.getVectorElement(j);
            }
            ShareData.setResultadoElement(i, suma);
        }

        long tiempoFin = System.nanoTime();
        long tiempoEjecucion = tiempoFin - tiempoInicio;

        System.out.println("Thread " + id + " finalizado - filas " +
                filaInicio + " a " + (filaFin - 1) + " completadas en " +
                (tiempoEjecucion / 1_000_000.0) + " ms");

        // Actualizar el thread más lento usando exclusión mutua
        ShareData.actualizarThreadMasLento(tiempoEjecucion, id);

        // Sincronización: notificar finalización al proceso principal
        ShareData.incrementarThreadsFinalizados();
    }
}