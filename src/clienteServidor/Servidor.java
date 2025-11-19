package clienteServidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Clase que representa un servidor en el sistema cliente-servidor.
 *
 * El servidor escucha conexiones entrantes y crea un thread independiente
 * para atender a cada cliente, permitiendo la atención concurrente de múltiples
 * clientes simultáneamente (hasta 10 clientes).
 *
 * Cada cliente conectado es atendido por un ThreadCliente que procesa
 * sus mensajes de forma independiente.
 *
 * @author Sistema PSP
 * @version 1.0
 * @see ThreadCliente
 * @see Conexion
 */
public class Servidor extends Conexion {

    /**
     * Constructor del servidor que inicializa el socket en el puerto especificado.
     *
     * @param puerto Puerto en el que el servidor escuchará conexiones entrantes
     * @throws IOException Si ocurre un error al crear el ServerSocket
     */
    public Servidor(int puerto) throws IOException {
        super("servidor", null, puerto);
    }

    /**
     * Inicia el servidor y acepta conexiones de clientes.
     *
     * El servidor acepta hasta 10 clientes simultáneos. Por cada cliente conectado,
     * se crea un nuevo thread (ThreadCliente) que gestiona la comunicación
     * de forma independiente.
     *
     * El servidor permanece en ejecución hasta que:
     * - Se alcanza el límite de 10 clientes
     * - Ocurre una excepción no controlada
     *
     * @see ThreadCliente#run()
     */
    public void startServer() {
        try {
            System.out.println("Servidor iniciado. Esperando clientes...");
            System.out.println("(Máximo 10 clientes simultáneos)\n");

            int numeroCliente = 0;
            final int MAX_CLIENTES = 10;

            // Bucle infinito para aceptar múltiples clientes
            while (numeroCliente < MAX_CLIENTES) {
                // Esperar y aceptar nueva conexión
                cs = ss.accept();
                numeroCliente++;

                // Crear un thread para atender a este cliente
                ThreadCliente threadCliente = new ThreadCliente(cs, numeroCliente);
                threadCliente.start();

                System.out.println("Nuevo cliente aceptado. Total de clientes conectados: " + numeroCliente + "\n");
            }

            System.out.println("Límite de clientes alcanzado (10). No se aceptarán más conexiones.");
            ss.close();

        } catch (Exception e) {
            System.out.println("Error en servidor: " + e.getMessage());
        }
    }
}