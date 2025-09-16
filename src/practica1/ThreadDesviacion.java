package practica1;

public class ThreadDesviacion extends Thread {
    private int[] vector;
    private double media;
    private double desviacion;

    public ThreadDesviacion(int[] vector, double media) {
        this.vector = vector;
        this.media = media;
    }

    /**
     * Pre: ---
     * Post: calcula la desviación típica del vector
     */
    public void run() {
        double sumaCuadrados = 0;
        for (int valor : vector) {
            sumaCuadrados += Math.pow(valor - media, 2);
        }
        desviacion = Math.sqrt(sumaCuadrados / vector.length);
        System.out.println("Hilo Desviación: Cálculo completado");
    }

    public double getDesviacion() {
        return desviacion;
    }
}