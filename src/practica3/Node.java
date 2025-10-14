package practica3;

/**
 * Esta clase representa un nodo para la cola enlazada.
 * Contiene un mensaje de texto y la referencia al siguiente nodo.
 */
public class Node {
    private String contenido;
    private Node siguiente;

    /**
     * Pre: ---
     * Post: Constructor por defecto que inicializa un nodo vacío.
     */
    public Node() {
        this.contenido = null;
        this.siguiente = null;
    }

    /**
     * Pre: contenido != null
     * Post: Constructor que inicializa un nodo con el contenido especificado.
     */
    public Node(String contenido) {
        this.contenido = contenido;
        this.siguiente = null;
    }

    /**
     * Pre: ---
     * Post: Devuelve el contenido almacenado en el nodo.
     */
    public String getContenido() {
        return contenido;
    }

    /**
     * Pre: contenido != null
     * Post: Establece el contenido del nodo.
     */
    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    /**
     * Pre: ---
     * Post: Devuelve la referencia al siguiente nodo.
     */
    public Node getSiguiente() {
        return siguiente;
    }

    /**
     * Pre: ---
     * Post: Establece la referencia al siguiente nodo.
     */
    public void setSiguiente(Node siguiente) {
        this.siguiente = siguiente;
    }

    /**
     * Pre: ---
     * Post: Verifica si el nodo tiene un siguiente.
     */
    public boolean hasSiguiente() {
        return this.siguiente != null;
    }

    /**
     * Pre: ---
     * Post: Devuelve una representación en cadena del nodo.
     */
    @Override
    public String toString() {
        return "Contenido = " + this.contenido;
    }
}