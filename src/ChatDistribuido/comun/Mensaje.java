package ChatDistribuido.comun;
import java.io.Serializable;

/**
 * Clase que representa un mensaje intercambiado entre cliente y servidor.
 * Implementa Serializable para poder enviarse por sockets.
 */
public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;
    private String tipo; // TEXTO, COMANDO, SALIR, CLAVE_PUBLICA, ETC
    private String contenido;
    private String emisor;

    /**
     * Constructor de la clase Mensaje.
     * Pre: Los parámetros no deben ser nulos.
     * Post: Se crea una instancia del mensaje con los datos proporcionados.
     */
    public Mensaje(String tipo, String contenido, String emisor) {
        this.tipo = tipo;
        this.contenido = contenido;
        this.emisor = emisor;
    }

    /**
     * Obtiene el tipo de mensaje.
     * Pre: Ninguna.
     * Post: Devuelve el tipo de mensaje.
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Obtiene el contenido del mensaje.
     * Pre: Ninguna.
     * Post: Devuelve el contenido del mensaje.
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Obtiene el emisor del mensaje.
     * Pre: Ninguna.
     * Post: Devuelve el nombre del emisor.
     */
    public String getEmisor() {
        return emisor;
    }
}