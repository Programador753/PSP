package eda;

/**
 * Clase que implementa una lista enlazada simple.
 *
 * Una lista enlazada simple es una estructura de datos lineal donde cada elemento
 * (nodo) contiene un valor y una referencia al siguiente elemento.
 *
 * Operaciones soportadas:
 * - Inserción al final de la lista
 * - Inserción en posición específica
 * - Eliminación por posición
 * - Consulta de elementos
 * - Verificación de lista vacía
 *
 * @author Alvaro Juan Ciriaco
 * @version 1.0
 * @see Node
 */
public class SimpleLinkedList {
    /** Número de elementos en la lista */
    private int size;

    /** Primer nodo de la lista */
    private Node first;

    /**
     * Constructor que crea una lista enlazada simple vacía.
     */
    public SimpleLinkedList() {
        this.size = 0;
        this.first = null;
    }

    /**
     * Obtiene el tamaño actual de la lista.
     *
     * @return Número de elementos en la lista
     */
    public int getSize() {
        return size;
    }

    /**
     * Establece el tamaño de la lista.
     *
     * @param size Nuevo tamaño de la lista
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Obtiene el primer nodo de la lista.
     *
     * @return Primer nodo, o null si la lista está vacía
     */
    public Node getFirst() {
        return first;
    }

    /**
     * Establece el primer nodo de la lista.
     *
     * @param first Nuevo primer nodo de la lista
     */
    public void setFirst(Node first) {
        this.first = first;
    }

    /**
     * Añade un nuevo nodo al final de la lista.
     *
     * @param node Nodo a insertar al final de la lista
     */
    public void add(Node node) {
        if(isEmpty()) this.first = node;
        else {
            Node p = this.first;
            while(p.getNext() != null) {
                p = p.getNext();
            } p.setNext(node);
        } size++;
    }

    /**
     * Inserta un nodo en una posición específica de la lista.
     *
     * Las posiciones comienzan en 0. Si la posición es 0, el nodo se inserta
     * al principio. Si la posición es mayor o igual al tamaño, se lanza una excepción.
     *
     * @param node Nodo a insertar
     * @param position Posición donde insertar el nodo (0-indexed)
     * @throws IndexOutOfBoundsException Si la posición es inválida
     */
    public void add(Node node, int position) {
        // Validar posición
        if (position < 0 || position > size) {
            throw new IndexOutOfBoundsException("Posición inválida: " + position);
        }

        // Si la posición es 0, insertar al inicio
        if (position == 0) {
            node.setNext(this.first);
            this.first = node;
        } else {
            // Buscar la posición anterior donde insertar
            Node p = this.first;
            for (int i = 0; i < position - 1; i++) {
                p = p.getNext();
            }
            // Insertar el nodo
            node.setNext(p.getNext());
            p.setNext(node);
        }
        size++;
    }

    /**
     * Elimina el nodo en la posición especificada.
     *
     * @param position Posición del nodo a eliminar (0-indexed)
     * @throws IndexOutOfBoundsException Si la posición es inválida
     */
    public void delete(int position) {
        // Validar posicion
        if(position < 0 || position >= size) {
            throw new IndexOutOfBoundsException("Posición inválida: " + position);
        }

        // Si la lista esta vacia.
        if(isEmpty()) {
            return;
        }

        // Si eliminamos el primer nodo.
        if(position == 0) {
            this.first = this.first.getNext();
        } else {
            // Buscar el nodo anterior a eliminar.
            Node p = this.first;
            for(int i = 0; i < position - 1; i++) {
                p = p.getNext();
            }
            // Eliminar el nodo conectando el anterior con el siguiente
            Node nodeToDelete = p.getNext();
            p.setNext(nodeToDelete.getNext());
        }
        size--;
    }

    /**
     * Obtiene el nodo en la posición especificada.
     *
     * @param position Posición del nodo a obtener (0-indexed)
     * @return Nodo en la posición especificada, o null si no está implementado
     * @deprecated Este método aún no está implementado
     */
    public Node get(int position) {
        return null;
    }

    /**
     * Verifica si la lista está vacía.
     *
     * @return true si la lista no contiene elementos, false en caso contrario
     */
    public boolean isEmpty() {
        if(this.size == 0) return true;
        return false;
    }

    /**
     * Muestra por pantalla todos los elementos de la lista.
     *
     * Los elementos se muestran desde el primero hasta el último,
     * cada uno en una línea separada.
     */
    public void show() {
        Node p = first;
        while(p != null) {
            System.out.println(p);
            p = p.getNext();
        }
    }
}