package ServidordeCorreos;

/**
 * Hilo que simula un usuario enviando emails.
 */
public class Usuario extends Thread {

    private BandejaSalida bandeja;
    private String idUsuario;

    /**
     * Constructor del usuario que recibe un id y una bandeja de salida.
     * @param id Identificador del usuario.
     * @param b Bandeja de salida.
     */
    public Usuario(String id, BandejaSalida b) {
        this.idUsuario = id;
        this.bandeja = b;
    }

    /**
     * Pre: ---
     * Post: Genera y envía 5 mensajes a la bandeja.
     */
    public void run() {
        for (int i = 1; i <= 5; i++) {
            try {
                Thread.sleep((long) (Math.random() * 500));
                String msg = "Email_" + i + "_de_" + idUsuario;
                bandeja.depositarMensaje(msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}