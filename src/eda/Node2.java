

package eda;

/**
 * Clase que representa un nodo para una lista doblemente enlazada.
 *
 * Cada nodo contiene un valor entero y referencias tanto al siguiente
 * como al anterior nodo en la lista.
 *
 * @author Sistema EDA
 * @version 1.0
 * @see DoubleLinkedList
 */
public class Node2 {
    /** Contenido del nodo */
    private int content;

    /** Referencia al siguiente nodo en la lista */
    private Node2 next;

    /** Referencia al nodo anterior en la lista */
    private Node2 previous;

    /**
     * Constructor por defecto que inicializa un nodo vacío.
     * El contenido se inicializa a 0 y las referencias a null.
     */
    public Node2() {
        this.content = 0;
        this.next = null;
        this.previous = null;
    }

    /**
     * Constructor que inicializa un nodo con el contenido especificado.
     * Las referencias next y previous se inicializan a null.
     *
     * @param content Valor entero a almacenar en el nodo
     */
    public Node2(int content) {
        this.content = content;
        this.next = null;
        this.previous = null;
    }

    /**
     * Obtiene el contenido almacenado en el nodo.
     *
     * @return Valor entero almacenado en el nodo
     */
    public int getContent() {
        return content;
    }

    /**
     * Establece el contenido del nodo.
     *
     * @param content Nuevo valor entero a almacenar en el nodo
     */
    public void setContent(int content) {
        this.content = content;
    }

    /**
     * Obtiene la referencia al siguiente nodo en la lista.
     *
     * @return Siguiente nodo en la lista, o null si no existe
     */
    public Node2 getNext() {
        return next;
    }

    /**
     * Establece la referencia al siguiente nodo en la lista.
     *
     * @param next Referencia al nodo que seguirá a este en la lista
     */
    public void setNext(Node2 next) {
        this.next = next;
    }

    /**
     * Obtiene la referencia al nodo anterior en la lista.
     *
     * @return Nodo anterior en la lista, o null si no existe
     */
    public Node2 getPrevious() {
        return previous;
    }

    /**
     * Establece la referencia al nodo anterior en la lista.
     *
     * @param previous Referencia al nodo que precederá a este en la lista
     */
    public void setPrevious(Node2 previous) {
        this.previous = previous;
    }

    /**
     * Devuelve una representación en cadena del nodo.
     *
     * @return Cadena con el formato "Content = [valor]"
     */
    @Override
    public String toString() {
        return "Content = " + this.content;
    }

    /**
     * Verifica si el nodo tiene un nodo siguiente.
     *
     * @return true si existe un nodo siguiente, false en caso contrario
     */
    public boolean hasNext() {
        return this.next != null;
    }

    /**
     * Verifica si el nodo tiene un nodo anterior.
     *
     * @return true si existe un nodo anterior, false en caso contrario
     */
    public boolean hasPrevious() {
        return this.previous != null;
    }

    /**
     * Verifica si el nodo es el primero en la lista.
     * Un nodo es el primero si no tiene nodo anterior.
     *
     * @return true si es el primer nodo, false en caso contrario
     */
    public boolean isFirst() {
        return this.previous == null;
    }

    /**
     * Verifica si el nodo es el último en la lista.
     * Un nodo es el último si no tiene nodo siguiente.
     *
     * @return true si es el último nodo, false en caso contrario
     */
    public boolean isLast() {
        return this.next == null;
    }
}