package ReservaVuelos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Thread del servidor que atiende a un cliente específico del sistema de reservas.
 * Gestiona el protocolo de comunicación para reservas de plazas de avión.
 * Permite que el servidor continúe aceptando nuevas conexiones mientras atiende clientes.
 */
public class ThreadServidor extends Thread {
    private final Socket clienteSocket;
    private final int numeroCliente;
    private final DatosCompartidos datosCompartidos;

    /**
     * Pre: socket != null, numeroCliente > 0, datosCompartidos != null
     * Post: Se crea un thread listo para atender al cliente con los
     *       parámetros especificados
     *
     * @param socket Socket de conexión con el cliente
     * @param numeroCliente Número identificador del cliente
     * @param datosCompartidos Instancia compartida de datos con avión y semáforos
     */
    public ThreadServidor(Socket socket, int numeroCliente, DatosCompartidos datosCompartidos) {
        this.clienteSocket = socket;
        this.numeroCliente = numeroCliente;
        this.datosCompartidos = datosCompartidos;
    }

    /**
     * Pre: Conexión establecida con el cliente a través del socket
     * Post: Se procesan todas las peticiones del cliente según el protocolo
     *       establecido hasta que el cliente finaliza o el vuelo está completo.
     *       Los recursos se cierran correctamente.
     */
    @Override
    public void run() {
        DataInputStream in = null;
        DataOutputStream out = null;
        String nombreCliente = "Cliente #" + numeroCliente;

        try {
            in = new DataInputStream(clienteSocket.getInputStream());
            out = new DataOutputStream(clienteSocket.getOutputStream());

            String mensaje;
            boolean clienteActivo = true;

            System.out.println("Cliente #" + numeroCliente + " conectado");

            while (clienteActivo) {
                mensaje = in.readUTF();

                if (mensaje.startsWith("INICIO COMPRA:")) {
                    nombreCliente = mensaje.substring(14).trim();
                    System.out.println("Cliente identificado: " + nombreCliente);

                    out.writeUTF("BIENVENIDO AL SERVICIO");
                    out.flush();

                } else if (mensaje.startsWith("RESERVAR:")) {
                    String plaza = mensaje.substring(9).trim();

                    if (!datosCompartidos.hayPlazasLibres()) {
                        out.writeUTF("VUELO COMPLETO");
                        out.flush();
                        clienteActivo = false;
                    } else {
                        String respuesta = datosCompartidos.reservarPlaza(plaza);
                        out.writeUTF(respuesta);
                        out.flush();

                        if (!datosCompartidos.hayPlazasLibres() && respuesta.startsWith("RESERVADA:")) {
                            System.out.println("AVIÓN COMPLETO - Todas las plazas reservadas");
                        }
                    }
                } else {
                    out.writeUTF("ERROR:Comando no reconocido");
                    out.flush();
                }
            }

        } catch (IOException e) {
            System.err.println("Error de comunicación con " + nombreCliente + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado con " + nombreCliente + ": " + e.getMessage());
        } finally {
            cerrarRecursos(in, out);
            System.out.println("Desconectado: " + nombreCliente);
        }
    }

    /**
     * Pre: Los recursos pueden ser null o estar abiertos
     * Post: Todos los recursos se cierran de forma segura. Si ocurre un error
     *       durante el cierre, se informa al usuario.
     *
     * @param in Stream de entrada a cerrar
     * @param out Stream de salida a cerrar
     */
    private void cerrarRecursos(DataInputStream in, DataOutputStream out) {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (clienteSocket != null && !clienteSocket.isClosed()) {
                clienteSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar conexión con cliente #" + numeroCliente + ": " + e.getMessage());
        }
    }
}

