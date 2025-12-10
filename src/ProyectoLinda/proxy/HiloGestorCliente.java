package ProyectoLinda.proxy;

import ProyectoLinda.comun.MensajeRed;
import ProyectoLinda.comun.TuplaLinda;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
/**
 * Gestiona clientes y balanceo de carga.
 */
public class HiloGestorCliente extends Thread {
    private Socket cliente;
    private static final int P_A = 8001;
    private static final int P_B = 8002;
    private static final int P_C = 8003;
    private static final int P_REP = 8004;
    /**
     * Pre: Socket valido.
     * Post: Constructor.
     */
    public HiloGestorCliente(Socket cliente) {
        this.cliente = cliente;
    }
    /**
     * Pre: Run.
     * Post: Distribucion y tolerancia a fallos.
     */
    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(cliente.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(cliente.getInputStream())) {
            while (true) {
                MensajeRed m = (MensajeRed) in.readObject();
                if (m.obtenerTipo() == MensajeRed.TipoOperacion.DESCONECTAR) break;
                if (m.obtenerTipo() == MensajeRed.TipoOperacion.CONECTAR) {
                    out.writeObject(new MensajeRed(MensajeRed.TipoOperacion.RESPUESTA_OK, (TuplaLinda)null));
                    continue;
                }
                int puerto = calcularPuerto(m.obtenerTupla());
                MensajeRed resp = enviarANodo(puerto, m);
                if (puerto == P_A) {
                    if (m.obtenerTipo() == MensajeRed.TipoOperacion.POST_NOTE) enviarANodo(P_REP, m);
                    if (resp.obtenerTipo() == MensajeRed.TipoOperacion.ERROR) {
                        System.out.println("Fallo Nodo A. Usando Replica.");
                        resp = enviarANodo(P_REP, m);
                    }
                }
                out.writeObject(resp);
            }
        } catch (Exception e) {
            System.out.println("Cliente desconectado");
        }
    }
    /**
     * Pre: Tupla valida.
     * Post: Puerto destino.
     */
    private int calcularPuerto(TuplaLinda t) {
        if (t.obtenerTamano() <= 3) return P_A;
        if (t.obtenerTamano() <= 5) return P_B;
        return P_C;
    }
    /**
     * Pre: Puerto y mensaje.
     * Post: Respuesta del nodo.
     */
    private MensajeRed enviarANodo(int puerto, MensajeRed m) {
        try (Socket s = new Socket("localhost", puerto);
             ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(s.getInputStream())) {
            out.writeObject(m);
            return (MensajeRed) in.readObject();
        } catch (Exception e) {
            return new MensajeRed(MensajeRed.TipoOperacion.ERROR, (TuplaLinda)null);
        }
    }
}