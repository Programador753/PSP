package clienteServidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Servidor extends Conexion { //Se hereda de conexión para hacer uso de los sockets y demás

    public Servidor(int puerto) throws IOException {
    	super("servidor", null, puerto);
    }

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
