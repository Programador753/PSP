
package clienteServidor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Clase que representa un cliente en el sistema cliente-servidor.
 * El cliente se conecta a un servidor específico y permite enviar mensajes
 * que serán procesados por el servidor para contar las vocales que contienen.
 *
 * @author Sistema PSP
 * @version 1.0
 */
public class Cliente extends Conexion {

    /**
     * Constructor del cliente que establece la conexión con el servidor.
     *
     * @param host Dirección IP o nombre del host del servidor
     * @param puerto Puerto en el que el servidor está escuchando
     * @throws IOException Si ocurre un error al establecer la conexión
     */
    public Cliente(String host, int puerto) throws IOException {
        super("cliente", host, puerto);
    }

    /**
     * Inicia el cliente y gestiona la comunicación con el servidor.
     * Permite al usuario enviar múltiples mensajes hasta que escriba "END OF SERVICE".
     * Por cada mensaje, el servidor responde con el número de vocales que contiene.
     *
     * Protocolo de comunicación:
     * 1. Recibe mensaje de bienvenida del servidor
     * 2. Lee mensaje del usuario desde teclado
     * 3. Envía el mensaje al servidor
     * 4. Recibe y muestra la respuesta del servidor
     * 5. Repite desde el paso 2 hasta que el usuario escriba "END OF SERVICE"
     *
     * @see Servidor#startServer()
     */
    public void startClient() {
        try {
            // Canal para recibir mensajes (entrada)
            DataInputStream in = new DataInputStream(cs.getInputStream());
            // Canal para enviar mensajes (salida)
            DataOutputStream out = new DataOutputStream(cs.getOutputStream());
            // Lector de teclado
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

            // Recibir mensaje inicial del servidor
            String mensajeServidor = in.readUTF();
            System.out.println("Servidor: " + mensajeServidor + "\n");

            // Bucle para enviar múltiples mensajes
            while (true) {
                System.out.print("Introduce una frase (END OF SERVICE para salir): ");
                String mensaje = teclado.readLine();

                // Enviar mensaje al servidor
                out.writeUTF(mensaje);
                out.flush();

                // Recibir respuesta del servidor
                String respuesta = in.readUTF();
                System.out.println("Servidor: " + respuesta + "\n");

                // Si fue END OF SERVICE, salir del bucle
                if (mensaje.equalsIgnoreCase("END OF SERVICE")) {
                    break;
                }
            }

            // Cerrar recursos
            out.close();
            in.close();
            cs.close();
            System.out.println("Conexión cerrada");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}