package clienteServidor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ThreadCliente extends Thread {
    private Socket clienteSocket;
    private int numeroCliente;

    public ThreadCliente(Socket socket, int numeroCliente) {
        this.clienteSocket = socket;
        this.numeroCliente = numeroCliente;
    }

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

    private int contarVocales(String texto) {
        int contador = 0;
        texto = texto.toLowerCase();
        for (char c : texto.toCharArray()) {
            if ("aeiou".indexOf(c) != -1) contador++;
        }
        return contador;
    }
}

