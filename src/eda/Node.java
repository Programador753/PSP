package eda;

/**
 * Clase que representa un nodo para una lista enlazada simple.
 *
 * Cada nodo contiene un valor entero y una referencia al siguiente nodo
 * en la lista.
 *
 * @author Sistema EDA
 * @version 1.0
 * @see SimpleLinkedList
 */
public class Node {
    /** Contenido del nodo */
    private int content;

    /** Referencia al siguiente nodo en la lista */
    private Node next;

    /**
     * Constructor por defecto que crea un nodo vacío.
     */
    public Node() {}

    /**
     * Constructor que crea un nodo con el contenido especificado.
     *
     * @param content Valor entero a almacenar en el nodo
     */
    public Node(int content) {
        this.content = content;
        this.next = null;
    }

    /**
     * Obtiene el contenido almacenado en el nodo.
     *
     * @return Valor entero del contenido del nodo
     */
    public int getContent() {
        return content;
    }

    /**
     * Establece el contenido del nodo.
     *
     * @param content Nuevo valor entero a almacenar
     */
    public void setContent(int content) {
        this.content = content;
    }

    /**
     * Obtiene la referencia al siguiente nodo.
     *
     * @return Siguiente nodo en la lista, o null si es el último
     */
    public Node getNext() {
        return next;
    }

    /**
     * Establece la referencia al siguiente nodo.
     *
     * @param next Nodo que seguirá a este en la lista
     */
    public void setNext(Node next) {
        this.next = next;
    }

    /**
     * Devuelve una representación en cadena del nodo.
     *
     * @return Cadena con formato "Content = [valor]"
     */
    @Override
    public String toString() {
        return "Content = " + this.content;
    }
}