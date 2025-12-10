package ProyectoLinda.comun;
import java.io.Serializable;
import java.util.Arrays;
/**
 * Clase que representa una tupla de datos (Strings).
 */
public class TuplaLinda implements Serializable {
    private String[] contenido;
    /**
     * Pre: El contenido no debe ser nulo.
     * Post: Instancia la tupla.
     */
    public TuplaLinda(String[] contenido) {
        this.contenido = contenido;
    }
    /**
     * Pre: El patron no debe ser nulo.
     * Post: Devuelve true si coincide, soportando variables ?.
     */
    public boolean coincideConPatron(TuplaLinda patron) {
        if (patron.obtenerTamano() != this.obtenerTamano()) {
            return false;
        }
        for (int i = 0; i < this.obtenerTamano(); i++) {
            String valorPatron = patron.obtenerElemento(i);
            String valorPropio = this.contenido[i];
            if (!valorPatron.startsWith("?") && !valorPatron.equals(valorPropio)) {
                return false;
            }
        }
        return true;
    }
    /**
     * Pre: Indice valido.
     * Post: Devuelve el elemento en esa posicion.
     */
    public String obtenerElemento(int i) {
        return contenido[i];
    }
    /**
     * Pre: Ninguna.
     * Post: Devuelve el numero de elementos.
     */
    public int obtenerTamano() {
        return contenido.length;
    }
    /**
     * Pre: Ninguna.
     * Post: Representacion textual.
     */
    @Override
    public String toString() {
        return Arrays.toString(contenido);
    }
}