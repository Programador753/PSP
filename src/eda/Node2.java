
package eda;

/**
 * Esta clase representa un nodo para una lista enlazada doble.
 * Contiene referencias tanto al siguiente como al anterior nodo.
 *
 *
 */
public class Node2 {
    private int content;
    private Node2 next;
    private Node2 previous;

    /**
     * Constructor por defecto que inicializa un nodo vacío.
     */
    public Node2() {
        this.content = 0;
        this.next = null;
        this.previous = null;
    }

    /**
     * Constructor que inicializa un nodo con el contenido especificado.
     * @param content contiene el valor a almacenar en el nodo.
     */
    public Node2(int content) {
        this.content = content;
        this.next = null;
        this.previous = null;
    }

    /**
     * Este método devuelve el contenido almacenado en el nodo.
     * @return el valor entero almacenado en el nodo.
     */
    public int getContent() {
        return content;
    }

    /**
     * Este método permite establecer el contenido del nodo.
     * @param content contiene el nuevo valor a almacenar.
     */
    public void setContent(int content) {
        this.content = content;
    }

    /**
     * Este método devuelve la referencia al siguiente nodo.
     * @return el nodo siguiente en la lista, o null si no existe.
     */
    public Node2 getNext() {
        return next;
    }

    /**
     * Este método permite establecer la referencia al siguiente nodo.
     * @param next contiene la referencia al nodo siguiente.
     */
    public void setNext(Node2 next) {
        this.next = next;
    }

    /**
     * Este método devuelve la referencia al nodo anterior.
     * @return el nodo anterior en la lista, o null si no existe.
     */
    public Node2 getPrevious() {
        return previous;
    }

    /**
     * Este método permite establecer la referencia al nodo anterior.
     * @param previous contiene la referencia al nodo anterior.
     */
    public void setPrevious(Node2 previous) {
        this.previous = previous;
    }

    /**
     * Este método devuelve una representación en cadena del nodo.
     * @return una cadena con el formato "Content = [valor]".
     */
    @Override
    public String toString() {
        return "Content = " + this.content;
    }

    /**
     * Este método verifica si el nodo tiene un siguiente.
     * @return devuelve [true] si existe un nodo siguiente, [false] en caso contrario.
     */
    public boolean hasNext() {
        return this.next != null;
    }

    /**
     * Este método verifica si el nodo tiene un anterior.
     * @return devuelve [true] si existe un nodo anterior, [false] en caso contrario.
     */
    public boolean hasPrevious() {
        return this.previous != null;
    }

    /**
     * Este método verifica si el nodo es el primero en la lista.
     * @return devuelve [true] si es el primer nodo, [false] en caso contrario.
     */
    public boolean isFirst() {
        return this.previous == null;
    }

    /**
     * Este método verifica si el nodo es el último en la lista.
     * @return devuelve [true] si es el último nodo, [false] en caso contrario.
     */
    public boolean isLast() {
        return this.next == null;
    }
}