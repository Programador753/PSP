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
        System.out.println("==============================================");
        System.out.println("SERVIDOR PRINCIPAL INICIANDO RECUPERACIÓN");
        System.out.println("==============================================");

        // Bloquear operaciones durante la sincronización
        almacen.iniciarSincronizacion();

        try (Socket s = new Socket("localhost", PUERTO_REPLICA);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {

            System.out.println("Conectado a réplica en puerto " + PUERTO_REPLICA);
            System.out.println("Solicitando datos...");

            out.writeObject(new MensajeRed(MensajeRed.TipoOperacion.SINCRONIZAR, (TuplaLinda)null));
            MensajeRed resp = (MensajeRed) in.readObject();

            if (resp.obtenerTipo() == MensajeRed.TipoOperacion.RESPUESTA_SYNC) {
                System.out.println("Transferencia iniciada desde réplica...");
                almacen.sincronizarDesdeReplica(resp.obtenerLista());
                System.out.println("==============================================");
                System.out.println("RECUPERACIÓN EXITOSA");
                System.out.println("==============================================");
            }
        } catch (Exception e) {
            System.out.println("==============================================");
            System.out.println("Réplica no disponible. Iniciando vacío.");
            System.out.println("==============================================");
        } finally {
            // Desbloquear operaciones
            almacen.finalizarSincronizacion();
        }
    }
}