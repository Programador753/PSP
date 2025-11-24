package ejercicioPanaderia;

/**
 * Hilo que compra pan de la cesta.
 */
public class Cliente extends Thread {

    private Cesta cesta;
    private String nombre;
    private int cantidadAComprar;

    /**
     * Constructor del Cliente.
     * @param nombre Nombre del cliente.
     * @param c Referencia a la cesta compartida.
     * @param cantidad Número de barras que quiere comprar.
     */
    public Cliente(String nombre, Cesta c, int cantidad) {
        this.nombre = nombre;
        this.cesta = c;
        this.cantidadAComprar = cantidad;
    }

    /**
     * Pre: ---
     * Post: Intenta comprar la cantidad deseada de pan.
     */
    public void run() {
        for (int i = 1; i <= cantidadAComprar; i++) {
            try {
                cesta.cogerBarra();

                // Simular tiempo comiendo o caminando
                Thread.sleep((long) (Math.random() * 600));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("<<< " + nombre + " se va a casa con su pan.");
    }
}