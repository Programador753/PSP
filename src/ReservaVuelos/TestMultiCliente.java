package ReservaVuelos;

import java.io.IOException;

/**
 * Clase de prueba para simular la conexión de múltiples clientes concurrentes
 * al servidor de reservas de vuelos.
 */
public class TestMultiCliente {
    private static final Object CONSOLE_LOCK = new Object();

    /**
     * Pre: El servidor debe estar en ejecución.
     * Post: Lanza un número predefinido de clientes en hilos separados para
     *       probar la concurrencia del servidor.
     */
    public static void main(String[] args) {
        final int NUM_CLIENTES = 5;

        synchronized (CONSOLE_LOCK) {
            System.out.println("==============================================");
            System.out.println("  INICIANDO PRUEBA CON " + NUM_CLIENTES + " CLIENTES CONCURRENTES");
            System.out.println("==============================================");
        }

        for (int i = 1; i <= NUM_CLIENTES; i++) {
            final String nombreCliente = "ClienteDePrueba-" + i;

            Thread t = new Thread(() -> {
                try {
                    synchronized (CONSOLE_LOCK) {
                        System.out.println("Lanzando " + nombreCliente + "...");
                    }
                    Cliente cli = new Cliente(nombreCliente);
                    cli.startClient();
                } catch (IOException e) {
                    synchronized (CONSOLE_LOCK) {
                        System.err.println("Error al conectar " + nombreCliente + ": " + e.getMessage() + ". Asegúrese de que el servidor está activo.");
                    }
                } catch (Exception e) {
                    synchronized (CONSOLE_LOCK) {
                        System.err.println("Error inesperado en " + nombreCliente + ": " + e.getMessage());
                    }
                }
            });
            t.start();
        }

        synchronized (CONSOLE_LOCK) {
            System.out.println("");
            System.out.println("Todos los hilos de cliente han sido lanzados.");
            System.out.println("La ejecución de cada cliente continuará de forma asíncrona.");
            System.out.println("==========================================================");
            System.out.println("");
        }
    }
}

