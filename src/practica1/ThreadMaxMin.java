package practica1;

public class ThreadMaxMin extends Thread {
    private int[] vector;
    private int maximo;
    private int minimo;

    public ThreadMaxMin(int[] vector) {
        this.vector = vector;
    }

    /**
     * Pre: ---
     * Post: calcula el valor máximo y mínimo del vector
     */
    public void run() {
        maximo = vector[0];
        minimo = vector[0];

        for (int valor : vector) {
            if (valor > maximo) {
                maximo = valor;
            }
            if (valor < minimo) {
                minimo = valor;
            }
        }
        System.out.println("Hilo MaxMin: Cálculo completado");
    }

    public int getMaximo() {
        return maximo;
    }

    public int getMinimo() {
        return minimo;
    }
}