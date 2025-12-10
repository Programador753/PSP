package ProyectoLinda.server;

import ProyectoLinda.comun.MensajeRed;
import ProyectoLinda.comun.TuplaLinda;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
/**
 * Procesa peticiones individuales en el nodo.
 */
public class HiloTrabajadorNodo extends Thread {
    private Socket socketCliente;
    private AlmacenDatos almacenCompartido;
    /**
     * Pre: Validos.
     * Post: Constructor.
     */
    public HiloTrabajadorNodo(Socket socketCliente, AlmacenDatos almacenCompartido) {
        this.socketCliente = socketCliente;
        this.almacenCompartido = almacenCompartido;
    }
    /**
     * Pre: Start.
     * Post: Procesa peticiones y devuelve ERROR si no encuentra tupla.
     */
    @Override
    public void run() {
        try (ObjectInputStream entrada = new ObjectInputStream(socketCliente.getInputStream());
             ObjectOutputStream salida = new ObjectOutputStream(socketCliente.getOutputStream())) {
            MensajeRed peticion = (MensajeRed) entrada.readObject();
            MensajeRed respuesta = new MensajeRed(MensajeRed.TipoOperacion.ERROR, (TuplaLinda)null);
            switch (peticion.obtenerTipo()) {
                case POST_NOTE:
                    almacenCompartido.postNote(peticion.obtenerTupla());
                    respuesta = new MensajeRed(MensajeRed.TipoOperacion.RESPUESTA_OK, (TuplaLinda)null);
                    break;
                case REMOVE_NOTE:
                    TuplaLinda tRem = almacenCompartido.removeNote(peticion.obtenerTupla());
                    if (tRem != null) {
                        respuesta = new MensajeRed(MensajeRed.TipoOperacion.RESPUESTA_OK, tRem);
                    }
                    break;
                case READ_NOTE:
                    TuplaLinda tRead = almacenCompartido.readNote(peticion.obtenerTupla());
                    if (tRead != null) {
                        respuesta = new MensajeRed(MensajeRed.TipoOperacion.RESPUESTA_OK, tRead);
                    }
                    break;
                case SINCRONIZAR:
                    List<TuplaLinda> backup = almacenCompartido.obtenerCopiaSeguridad();
                    respuesta = new MensajeRed(MensajeRed.TipoOperacion.RESPUESTA_SYNC, backup);
                    break;
            }
            salida.writeObject(respuesta);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}