package ProyectoLinda.cliente;

import ProyectoLinda.comun.MensajeRed;
import ProyectoLinda.comun.TuplaLinda;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente de consola con la logica de interfaz mejorada.
 */
public class AplicacionCliente {
    /**
     * Pre: El proxy debe estar escuchando en el puerto 8000.
     * Post: Ejecuta el bucle de interaccion con el usuario.
     */
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8000);
             ObjectOutputStream flujoSalida = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream flujoEntrada = new ObjectInputStream(socket.getInputStream());
             Scanner teclado = new Scanner(System.in)) {
            flujoSalida.writeObject(new MensajeRed(MensajeRed.TipoOperacion.CONECTAR, (TuplaLinda)null));
            flujoEntrada.readObject();
            System.out.println("--- Conectado a LINDA ---");
            boolean salir = false;
            while (!salir) {
                System.out.println("1.Post 2.Remove 3.Read 4.Salir");
                int opcion = Integer.parseInt(teclado.nextLine());
                if (opcion == 4) {
                    flujoSalida.writeObject(new MensajeRed(MensajeRed.TipoOperacion.DESCONECTAR, (TuplaLinda)null));
                    salir = true;
                } else {
                    System.out.println("Introduzca los datos separados por comas (ej: coche,rojo):");
                    String[] datosRaw = teclado.nextLine().split(",");
                    MensajeRed.TipoOperacion tipo = MensajeRed.TipoOperacion.POST_NOTE;
                    if (opcion == 2) tipo = MensajeRed.TipoOperacion.REMOVE_NOTE;
                    if (opcion == 3) tipo = MensajeRed.TipoOperacion.READ_NOTE;
                    flujoSalida.writeObject(new MensajeRed(tipo, new TuplaLinda(datosRaw)));
                    System.out.println("Procesando peticion...");
                    MensajeRed respuesta = (MensajeRed) flujoEntrada.readObject();
                    if (respuesta.obtenerTipo() == MensajeRed.TipoOperacion.RESPUESTA_OK) {
                        if (respuesta.obtenerTupla() == null) {
                            System.out.println("Exito: La tupla ha sido guardada en el sistema.");
                        } else {
                            System.out.println("Exito: Datos recuperados -> " + respuesta.obtenerTupla());
                        }
                    } else {
                        System.out.println("Error: La operacion ha fallado o el servidor no responde.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}