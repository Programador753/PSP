package ProyectoLinda.server;

import ProyectoLinda.comun.MensajeRed;
import ProyectoLinda.comun.TuplaLinda;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Main del nodo con recuperacion automatica.
 */
public class ServidorNodo {
    private static final int PUERTO_PRINCIPAL = 8001;
    private static final int PUERTO_REPLICA = 8004;
    /**
     * Pre: Puerto en args.
     * Post: Inicio y recuperacion si es necesario.
     */
    public static void main(String[] args) {
        if (args.length < 1) return;
        int puerto = Integer.parseInt(args[0]);
        AlmacenDatos almacen = new AlmacenDatos();
        if (puerto == PUERTO_PRINCIPAL) {
            recuperarDeReplica(almacen);
        }
        try (ServerSocket server = new ServerSocket(puerto)) {
            System.out.println("Nodo activo en " + puerto);
            while (true) {
                new HiloTrabajadorNodo(server.accept(), almacen).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Pre: Almacen valido.
     * Post: Copia datos de replica a principal.
     */
    private static void recuperarDeReplica(AlmacenDatos almacen) {
        System.out.println("Intentando recuperar datos...");
        try (Socket s = new Socket("localhost", PUERTO_REPLICA);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {
            out.writeObject(new MensajeRed(MensajeRed.TipoOperacion.SINCRONIZAR, (TuplaLinda)null));
            MensajeRed resp = (MensajeRed) in.readObject();
            if (resp.obtenerTipo() == MensajeRed.TipoOperacion.RESPUESTA_SYNC) {
                almacen.cargarDatosMasivos(resp.obtenerLista());
                System.out.println("Datos recuperados exitosamente.");
            }
        } catch (Exception e) {
            System.out.println("Replica no disponible. Iniciando vacio.");
        }
    }
}