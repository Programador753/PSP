package ejercicioBaseDatos;

/**
 * Hilo que simula un administrador modificando la base de datos.
 */
public class Escritor extends Thread {

    private BaseDatos bd;
    private String idEscritor;

    /**
     * Constructor del Escritor.
     * @param id Identificador del administrador escritor.
     * @param bd Referencia a la base de datos compartida.
     */
    public Escritor(String id, BaseDatos bd) {
        this.idEscritor = id;
        this.bd = bd;
    }

    /**
     * Pre: ---
     * Post: Realiza 3 escrituras simuladas con pausas aleatorias.
     */
    public void run() {
        for (int i = 0; i < 3; i++) {
            try {
                // Simular tiempo pensando antes de escribir
                Thread.sleep((long) (Math.random() * 1000));

                bd.entrarEscritor(idEscritor);

                // Simular tiempo de escritura (crítico)
                Thread.sleep(800);

                bd.salirEscritor(idEscritor);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}