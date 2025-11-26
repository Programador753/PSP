package ejercicioBaseDatos;

/**
 * Hilo que simula un usuario consultando la base de datos.
 */
public class Lector extends Thread {

    private BaseDatos bd;
    private String idLector;

    /**
     * Constructor del Lector.
     * @param id Identificador del usuario lector.
     * @param bd Referencia a la base de datos compartida.
     */
    public Lector(String id, BaseDatos bd) {
        this.idLector = id;
        this.bd = bd;
    }

    /**
     * Pre: ---
     * Post: Realiza 3 lecturas simuladas con pausas aleatorias.
     */
    public void run() {
        for (int i = 0; i < 3; i++) {
            try {
                bd.entrarLector(idLector);

                // Simular tiempo de lectura
                Thread.sleep((long) (Math.random() * 500));

                bd.salirLector(idLector);

                // Tiempo de descanso fuera de la BD
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}