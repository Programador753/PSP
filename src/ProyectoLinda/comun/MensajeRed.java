package ProyectoLinda.comun;
import java.io.Serializable;
import java.util.List;
/**
 * Objeto de transferencia para las comunicaciones por red.
 */
public class MensajeRed implements Serializable {
    public enum TipoOperacion {
        CONECTAR, DESCONECTAR, POST_NOTE, REMOVE_NOTE, READ_NOTE,
        RESPUESTA_OK, ERROR, SINCRONIZAR, RESPUESTA_SYNC
    }
    private TipoOperacion tipo;
    private TuplaLinda tupla;
    private List<TuplaLinda> listaTuplas;
    /**
     * Pre: Tipo obligatorio.
     * Post: Constructor para operaciones normales.
     */
    public MensajeRed(TipoOperacion tipo, TuplaLinda tupla) {
        this.tipo = tipo;
        this.tupla = tupla;
    }
    /**
     * Pre: Lista valida.
     * Post: Constructor para sincronizacion de nodos.
     */
    public MensajeRed(TipoOperacion tipo, List<TuplaLinda> listaTuplas) {
        this.tipo = tipo;
        this.listaTuplas = listaTuplas;
    }
    /**
     * Pre: Tipo no nulo.
     * Post: Constructor para mensajes de control sin datos.
     */
    public MensajeRed(TipoOperacion tipo) {
        this.tipo = tipo;
        this.tupla = null;
        this.listaTuplas = null;
    }
    /**
     * Pre: Ninguna.
     * Post: Getters.
     */
    public TipoOperacion obtenerTipo() { return tipo; }
    public TuplaLinda obtenerTupla() { return tupla; }
    public List<TuplaLinda> obtenerLista() { return listaTuplas; }
}