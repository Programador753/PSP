package ProyectoLinda.cliente;

import ProyectoLinda.comun.MensajeRed;
import ProyectoLinda.comun.TuplaLinda;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Cliente simple para escribir tuplas de prueba.
 */
public class ClientePrueba {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java ProyectoLinda.cliente.ClientePrueba <host> <puerto> <numMensajes>");
            return;
        }

        String host = args[0];
        int puerto = Integer.parseInt(args[1]);
        int numMensajes = Integer.parseInt(args[2]);

        System.out.println("========================================");
        System.out.println("CLIENTE DE PRUEBA");
        System.out.println("Conectando a: " + host + ":" + puerto);
        System.out.println("Mensajes a enviar: " + numMensajes);
        System.out.println("========================================");

        for (int i = 1; i <= numMensajes; i++) {
            try (Socket socket = new Socket(host, puerto);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                // Conectar al nodo
                out.writeObject(new MensajeRed(MensajeRed.TipoOperacion.CONECTAR));
                in.readObject();

                // Enviar tupla
                String[] datos = {"mensaje", String.valueOf(i), "Datos de prueba " + i};
                TuplaLinda tupla = new TuplaLinda(datos);

                MensajeRed msgPost = new MensajeRed(MensajeRed.TipoOperacion.POST_NOTE, tupla);
                out.writeObject(msgPost);

                MensajeRed respuesta = (MensajeRed) in.readObject();

                if (respuesta.obtenerTipo() == MensajeRed.TipoOperacion.RESPUESTA_OK) {
                    System.out.println("[✓] Mensaje " + i + " enviado correctamente");
                } else {
                    System.out.println("[✗] Mensaje " + i + " RECHAZADO (posible sincronización)");
                }

                // Desconectar
                out.writeObject(new MensajeRed(MensajeRed.TipoOperacion.DESCONECTAR));

                // Pequeña pausa entre mensajes
                Thread.sleep(100);

            } catch (Exception e) {
                System.out.println("[ERROR] No se pudo enviar mensaje " + i + ": " + e.getMessage());
            }
        }

        System.out.println("========================================");
        System.out.println("PRUEBA COMPLETADA");
        System.out.println("========================================");
    }
}

