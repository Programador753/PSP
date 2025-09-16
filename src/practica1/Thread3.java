package practica1;

public class Thread3 extends Thread {
    private String mensaje;
    private int retardo;
    private int veces;

    public Thread3(String mensaje, int retardo, int veces) {
        this.mensaje = mensaje;
        this.retardo = retardo;
        this.veces = veces;
    }

    /**
     * Pre: ---
     * Post: el metodo run() contiene el codigo a ejecutar por
     * 		parte del hilo.
     */
    public void run() {
        for(int i = 1; i <= veces; i++) {
            System.out.println(mensaje);
            try {
                /*
                 * Paraliza le ejecucion del hilo
                 * [retardo] milisegundos
                 */
                Thread.sleep(retardo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
