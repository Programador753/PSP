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
            System.out.println("Esperando cliente...");
            cs = ss.accept();
            System.out.println("Cliente conectado\n");

            DataInputStream in = new DataInputStream(cs.getInputStream());
            DataOutputStream out = new DataOutputStream(cs.getOutputStream());

            out.writeUTF("Petición recibida y aceptada");
            out.flush();

            // Bucle para leer múltiples mensajes del mismo cliente
            boolean serverActivo = true;
            while(serverActivo) {
                try {
                    String mensaje = in.readUTF();
                    System.out.println("Mensaje recibido del cliente -> " + mensaje);

                    if (mensaje.equalsIgnoreCase("END OF SERVICE")) {
                        out.writeUTF("Fin del servicio. Cerrando conexión...");
                        out.flush();
                        System.out.println("Fin del servicio solicitado. Cerrando servidor.\n");
                        serverActivo = false;
                    } else {
                        int contador = contarVocales(mensaje);
                        String respuesta = "El mensaje contiene " + contador + " vocales.";
                        out.writeUTF(respuesta);
                        out.flush();
                        System.out.println("Respuesta enviada: " + respuesta + "\n");
                    }
                } catch (IOException e) {
                    System.out.println("Error de comunicación: " + e.getMessage());
                    serverActivo = false;
                }
            }

            // Cerrar recursos
            in.close();
            out.close();
            cs.close();
            ss.close();
            System.out.println("Servidor cerrado correctamente");

        } catch (Exception e) {
            System.out.println("Error en servidor: " + e.getMessage());
        }
    }

    private int  contarVocales(String texto) {
        int contador = 0;
        texto = texto.toLowerCase();
        for (char c : texto.toCharArray()) {
            if ("aeiou".indexOf(c) != -1) contador++;
        }
        return contador;
    }
}
