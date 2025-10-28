package ReservaVuelos;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Thread que atiende a un cliente específico del sistema de reservas.
 * Gestiona el protocolo de comunicación para reservas de plazas de avión.
 *
 */
public class ThreadCliente extends Thread {
    private final Socket clienteSocket;
    private final int numeroCliente;
    private final Avion avion;

    /**
     * Pre: socket != null, numeroCliente > 0, avion != null
     * Post: Se crea un thread listo para atender al cliente con los
     *       parámetros especificados
     *
     * @param socket Socket de conexión con el cliente
     * @param numeroCliente Número identificador del cliente
     * @param avion Instancia compartida del avión para gestionar reservas
     */
    public ThreadCliente(Socket socket, int numeroCliente, Avion avion) {
        this.clienteSocket = socket;
        this.numeroCliente = numeroCliente;
        this.avion = avion;
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
            /*
             * Inicialización de los streams de entrada/salida
             */
            in = new DataInputStream(clienteSocket.getInputStream());
            out = new DataOutputStream(clienteSocket.getOutputStream());

            String mensaje;
            boolean clienteActivo = true;

            System.out.println("Cliente #" + numeroCliente + " conectado");

            /*
             * Bucle principal de atención al cliente
             */
            while (clienteActivo) {
                mensaje = in.readUTF();

                /*
                 * Procesamiento del protocolo INICIO COMPRA
                 */
                if (mensaje.startsWith("INICIO COMPRA:")) {
                    nombreCliente = mensaje.substring(14).trim();
                    System.out.println("Cliente identificado: " + nombreCliente);

                    out.writeUTF("BIENVENIDO AL SERVICIO");
                    out.flush();

                /*
                 * Procesamiento del protocolo RESERVAR
                 */
                } else if (mensaje.startsWith("RESERVAR:")) {
                    String plaza = mensaje.substring(9).trim();

                    if (!avion.hayPlazasLibres()) {
                        /*
                         * No quedan plazas disponibles
                         */
                        out.writeUTF("VUELO COMPLETO");
                        out.flush();
                        clienteActivo = false;
                    } else {
                        /*
                         * Intentar reservar la plaza solicitada
                         */
                        String respuesta = avion.reservarPlaza(plaza);
                        out.writeUTF(respuesta);
                        out.flush();

                        /*
                         * Notificar si se completó el avión con esta reserva
                         */
                        if (!avion.hayPlazasLibres() && respuesta.startsWith("RESERVADA:")) {
                            System.out.println("AVIÓN COMPLETO - Todas las plazas reservadas");
                        }
                    }
                } else {
                    /*
                     * Comando no reconocido
                     */
                    out.writeUTF("ERROR:Comando no reconocido");
                    out.flush();
                }
            }

        } catch (IOException e) {
            System.err.println("Error de comunicación con " + nombreCliente + ": " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado con " + nombreCliente + ": " + e.getMessage());
        } finally {
            /*
             * Cierre de recursos
             */
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

