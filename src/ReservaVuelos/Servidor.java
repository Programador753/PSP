package ReservaVuelos;

import java.io.IOException;

/**
 * Servidor de reservas de vuelos.
 * Gestiona múltiples clientes de forma concurrente usando threads.
 *
 */
public class Servidor extends Conexion {
    private final DatosCompartidos datosCompartidos;

    /**
     * Pre: ninguna
     * Post: Se crea el servidor y se inicializan los datos compartidos con
     *       el avión y semáforos para cada plaza
     *
     */
    public Servidor() throws IOException {
        super("servidor");
        this.datosCompartidos = new DatosCompartidos();
    }

    /**
     * Pre: El servidor debe estar correctamente inicializado
     * Post: El servidor acepta conexiones indefinidamente y crea un thread
     *       independiente para cada cliente que se conecta
     */
    public void startServer() {
        try {
            System.out.println("==============================================");
            System.out.println("  SERVIDOR DE RESERVAS DE VUELOS INICIADO");
            System.out.println("==============================================");
            System.out.println("Puerto: " + ss.getLocalPort());
            System.out.println("Capacidad: 16 plazas (4 filas x 4 asientos)");
            System.out.println("Esperando clientes...\n");

            int numeroCliente = 0;

            /*
             * Bucle infinito que acepta clientes
             */
            while (true) {
                /*
                 * Esperar conexión de un nuevo cliente
                 */
                cs = ss.accept();
                numeroCliente++;

                System.out.println("Nuevo cliente conectado (#" + numeroCliente + ") - IP: " +
                                 cs.getInetAddress().getHostAddress());

                /*
                 * Crear y arrancar thread para atender al cliente
                 */
                ThreadServidor threadServidor = new ThreadServidor(cs, numeroCliente, datosCompartidos);
                threadServidor.start();
            }

        } catch (IOException e) {
            System.err.println("Error de E/S en el servidor: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado en el servidor: " + e.getMessage());
        } finally {
            /*
             * Cerrar el socket del servidor
             */
            cerrarServidor();
        }
    }

    /**
     * Pre: ninguna
     * Post: Cierra el ServerSocket si está abierto. Si ocurre un error,
     *       se informa al usuario.
     */
    private void cerrarServidor() {
        try {
            if (ss != null && !ss.isClosed()) {
                ss.close();
                System.out.println("Servidor detenido correctamente");
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el servidor: " + e.getMessage());
        }
    }
}
