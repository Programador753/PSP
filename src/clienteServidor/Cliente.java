package clienteServidor;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Cliente extends Conexion {
    public Cliente(String host, int puerto) throws IOException {
    	super("cliente", host, puerto);
    } //Se usa el constructor para cliente de Conexion

    public void startClient() {//Método para iniciar el cliente
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

