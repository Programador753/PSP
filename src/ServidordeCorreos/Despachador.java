package ServidordeCorreos;

/**
 * Hilo que procesa y envía los mensajes de la bandeja.
 */
public class Despachador extends Thread {

    private BandejaSalida bandeja;

    /**
     * Constructor del despachador que recibe una bandeja de salida.
     * @param b Bandeja de salida.
     */
    public Despachador(BandejaSalida b) {
        this.bandeja = b;
    }

    /**
     * Pre: ---
     * Post: Procesa mensajes indefinidamente.
     */
    public void run() {
        while (true) {
            try {
                bandeja.extraerMensaje();
                // Simula tiempo de transmisión
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}