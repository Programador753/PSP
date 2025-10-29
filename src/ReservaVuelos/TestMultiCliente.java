package ReservaVuelos;

import java.io.IOException;

/**
 * Clase de prueba para simular la conexión de múltiples clientes concurrentes
 * al servidor de reservas de vuelos.
 */
public class TestMultiCliente {

    /**
     * Pre: El servidor debe estar en ejecución.
     * Post: Lanza un número predefinido de clientes en hilos separados para
     *       probar la concurrencia del servidor.
     *
     *
     */
    public static void main(String[] args) {
        // Número de clientes concurrentes a simular
        final int NUM_CLIENTES = 5;

        System.out.println("==============================================");
        System.out.println("  INICIANDO PRUEBA CON " + NUM_CLIENTES + " CLIENTES CONCURRENTES");
        System.out.println("==============================================");

        // Crear y lanzar un hilo para cada cliente
        for (int i = 1; i <= NUM_CLIENTES; i++) {
            final String nombreCliente = "ClienteDePrueba-" + i;

            Thread t = new Thread(() -> {
                try {
                    System.out.println("Lanzando " + nombreCliente + "...");
                    // Cada hilo crea su propia instancia de Cliente
                    Cliente cli = new Cliente(nombreCliente);
                    cli.startClient();
                } catch (IOException e) {
                    System.err.println("Error al conectar " + nombreCliente + ": " + e.getMessage() + ". Asegúrese de que el servidor está activo.");
                } catch (Exception e) {
                    System.err.println("Error inesperado en " + nombreCliente + ": " + e.getMessage());
                }
            });
            t.start();
        }

        System.out.println("\nTodos los hilos de cliente han sido lanzados.");
        System.out.println("La ejecución de cada cliente continuará de forma asíncrona.");
        System.out.println("==========================================================\n");
    }
}

