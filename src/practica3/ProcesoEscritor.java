package practica3;

public class ProcesoEscritor extends Thread {
    private ShareData datosCompartidos;
    private int idEscritor;
    private int numMensajes;

    /**
     * Pre: datosCompartidos != null, idEscritor > 0, numMensajes > 0
     * Post: Constructor del proceso escritor que inicializa los atributos
     *       necesarios para la inserción de mensajes en la cola compartida.
     */
    public ProcesoEscritor(ShareData datosCompartidos, int idEscritor, int numMensajes) {
        this.datosCompartidos = datosCompartidos;
        this.idEscritor = idEscritor;
        this.numMensajes = numMensajes;
    }

    /**
     * Pre: ---
     * Post: El método run() contiene el código a ejecutar por parte del hilo escritor.
     *       Inserta el número especificado de mensajes en la cola de forma concurrente.
     *       Cada mensaje incluye el identificador del escritor y el número de mensaje.
     */
    @Override
    public void run() {
        try {
            /*
             * Bucle para insertar todos los mensajes asignados
             */
            for (int i = 1; i <= numMensajes; i++) {
                String mensaje = "Mensaje " + i + " del Escritor " + idEscritor;
                datosCompartidos.insertar(mensaje);
                System.out.println("Escritor " + idEscritor + " insertó: " + mensaje);

                /*
                 * Pequeña pausa para simular trabajo de procesamiento
                 */
                Thread.sleep(50);
            }
            System.out.println("✓ Escritor " + idEscritor + " terminó su trabajo");
        } catch (InterruptedException e) {
            /*
             * Manejo de interrupción del hilo
             */
            Thread.currentThread().interrupt();
            System.err.println("✗ Escritor " + idEscritor + " fue interrumpido");
        }
    }
}