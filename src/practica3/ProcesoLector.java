package practica3;

public class ProcesoLector extends Thread {
    private ShareData datosCompartidos;
    private int idLector;
    private int numMensajes;

    /**
     * Pre: datosCompartidos != null, idLector > 0, numMensajes > 0
     * Post: Constructor del proceso lector que inicializa los atributos
     *       necesarios para la extracción de mensajes de la cola compartida.
     */
    public ProcesoLector(ShareData datosCompartidos, int idLector, int numMensajes) {
        this.datosCompartidos = datosCompartidos;
        this.idLector = idLector;
        this.numMensajes = numMensajes;
    }

    /**
     * Pre: ---
     * Post: El método run() contiene el código a ejecutar por parte del hilo lector.
     *       Extrae el número especificado de mensajes de la cola de forma concurrente.
     *       Muestra por salida estándar su identificador y el contenido extraído.
     */
    @Override
    public void run() {
        try {
            /*
             * Bucle para extraer todos los mensajes asignados
             */
            for (int i = 1; i <= numMensajes; i++) {
                String mensaje = datosCompartidos.extraer();
                System.out.println("Lector " + idLector + " extrajo: " + mensaje);

                /*
                 * Pequeña pausa para simular procesamiento del mensaje
                 */
                Thread.sleep(100);
            }
            System.out.println("✓ Lector " + idLector + " terminó su trabajo");
        } catch (InterruptedException e) {
            /*
             * Manejo de interrupción del hilo
             */
            Thread.currentThread().interrupt();
            System.err.println("✗ Lector " + idLector + " fue interrumpido");
        }
    }
}