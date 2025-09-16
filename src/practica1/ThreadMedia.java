package practica1;

public class ThreadMedia extends Thread {
    private int[] vector;
    private double media;

    public ThreadMedia(int[] vector) {
        this.vector = vector;
    }

    /**
     * Pre: ---
     * Post: calcula la media aritmética del vector
     */
    public void run() {
        long suma = 0;
        for (int valor : vector) {
            suma += valor;
        }
        media = (double) suma / vector.length;
        System.out.println("Hilo Media: Cálculo completado");
    }

    public double getMedia() {
        return media;
    }
}