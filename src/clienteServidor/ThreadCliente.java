
package clienteServidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Thread que gestiona la comunicación con un cliente individual en el servidor.
 *
 * Cada instancia de esta clase atiende a un cliente específico de forma concurrente,
 * permitiendo que el servidor maneje múltiples clientes simultáneamente.
 *
 * Funcionalidad principal:
 * - Recibe mensajes del cliente
 * - Cuenta el número de vocales en cada mensaje
 * - Envía la respuesta al cliente
 * - Cierra la conexión cuando el cliente envía "END OF SERVICE"
 *
 * @author Sistema PSP
 * @version 1.0
 * @see Servidor
 */
public class ThreadCliente extends Thread {
    /** Socket de comunicación con el cliente */
    private Socket clienteSocket;

    /** Número identificador del cliente */
    private int numeroCliente;

    /**
     * Constructor del thread que atiende a un cliente específico.
     *
     * @param socket Socket de comunicación establecido con el cliente
     * @param numeroCliente Número identificador asignado al cliente
     */
    public ThreadCliente(Socket socket, int numeroCliente) {
        this.clienteSocket = socket;
        this.numeroCliente = numeroCliente;
    }

    /**
     * Método principal del thread que gestiona toda la comunicación con el cliente.
     *
     * Protocolo de comunicación:
     * 1. Envía mensaje de bienvenida al cliente
     * 2. Recibe mensaje del cliente
     * 3. Si el mensaje es "END OF SERVICE", finaliza la comunicación
     * 4. En caso contrario, cuenta las vocales y envía el resultado
     * 5. Repite desde el paso 2
     *
     * Al finalizar o en caso de error, cierra todos los recursos de forma segura.
     */
    @Override
    public void run() {
        try {
            System.out.println("Cliente #" + numeroCliente + " conectado desde: " +
                    clienteSocket.getInetAddress().getHostAddress());

            DataInputStream in = new DataInputStream(clienteSocket.getInputStream());
            DataOutputStream out = new DataOutputStream(clienteSocket.getOutputStream());

            // Mensaje de bienvenida
            out.writeUTF("Petición recibida y aceptada. Eres el cliente #" + numeroCliente);
            out.flush();

            // Bucle para atender al cliente
            boolean clienteActivo = true;
            while (clienteActivo) {
                try {
                    String mensaje = in.readUTF();
                    System.out.println("Cliente #" + numeroCliente + " -> " + mensaje);

                    if (mensaje.equalsIgnoreCase("END OF SERVICE")) {
                        out.writeUTF("Fin del servicio. Cerrando conexión...");
                        out.flush();
                        System.out.println("Cliente #" + numeroCliente + " solicitó fin del servicio.\n");
                        clienteActivo = false;
                    } else {
                        int contador = contarVocales(mensaje);
                        String respuesta = "El mensaje contiene " + contador + " vocales.";
                        out.writeUTF(respuesta);
                        out.flush();
                        System.out.println("Respuesta enviada a cliente #" + numeroCliente + ": " + respuesta + "\n");
                    }
                } catch (IOException e) {
                    System.out.println("Error de comunicación con cliente #" + numeroCliente + ": " + e.getMessage());
                    clienteActivo = false;
                }
            }

            // Cerrar recursos del cliente
            in.close();
            out.close();
            clienteSocket.close();
            System.out.println("Conexión con cliente #" + numeroCliente + " cerrada correctamente\n");

        } catch (Exception e) {
            System.out.println("Error al atender cliente #" + numeroCliente + ": " + e.getMessage());
        }
    }

    /**
     * Cuenta el número de vocales (a, e, i, o, u) en un texto dado.
     *
     * La comparación se realiza de forma case-insensitive (no distingue mayúsculas/minúsculas).
     *
     * @param texto Cadena de texto en la que se contarán las vocales
     * @return Número total de vocales encontradas en el texto
     */
    private int contarVocales(String texto) {
        int contador = 0;
        texto = texto.toLowerCase();
        for (char c : texto.toCharArray()) {
            if ("aeiou".indexOf(c) != -1) contador++;
        }
        return contador;
    }
}