package ejercicioPanaderia;

/**
 * Hilo que hornea pan y lo coloca en la cesta.
 */
public class Panadero extends Thread {

    private Cesta cesta;
    private String nombre;
    private int cantidadAProducir;

    /**
     * Constructor del Panadero.
     * @param nombre Nombre del panadero.
     * @param c Referencia a la cesta compartida.
     * @param cantidad Número total de barras que va a hornear.
     */
    public Panadero(String nombre, Cesta c, int cantidad) {
        this.nombre = nombre;
        this.cesta = c;
        this.cantidadAProducir = cantidad;
    }

    /**
     * Pre: ---
     * Post: Produce la cantidad asignada de pan con pausas de horneado.
     */
    public void run() {
        for (int i = 1; i <= cantidadAProducir; i++) {
            try {
                // Simular tiempo de horneado
                Thread.sleep((long) (Math.random() * 400));

                String barra = "Barra_" + i + "_de_" + nombre;
                cesta.ponerBarra(barra);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(">>> " + nombre + " ha terminado su turno.");
    }
}