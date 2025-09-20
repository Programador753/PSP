
package eda;

/**
 * Esta clase contiene los métodos para la creación de una
 * lista enlazada doble.
 *
 *
 */
public class DoubleLinkedList {
    private Node2 head; // Primer nodo de la lista
    private Node2 tail; // Último nodo de la lista
    private int size;  // Tamaño actual de la lista

    public DoubleLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Node2 getHead() {
        return head;
    }

    public void setHead(Node2 head) {
        this.head = head;
    }

    public Node2 getTail() {
        return tail;
    }

    public void setTail(Node2 tail) {
        this.tail = tail;
    }

    /**
     * Este método permite añadir un nuevo nodo a la lista
     * enlazada doble. Lo inserta siempre en la última posición.
     * @param node contiene el nodo a insertar.
     */
    public void add(Node2 node) {
        if (isEmpty()) {
            // Lista vacía: el nodo es tanto head como tail
            this.head = node;
            this.tail = node;
            node.setPrevious(null);
            node.setNext(null);
        } else {
            // Lista no vacía: añadir al final
            this.tail.setNext(node);
            node.setPrevious(this.tail);
            node.setNext(null);
            this.tail = node;
        }
        size++;
    }

    /**
     * Este método permite añadir un nuevo nodo a la lista
     * enlazada doble en una posición específica.
     * @param node contiene el nodo a insertar.
     * @param position posición donde insertar el nodo (empezando desde 0).
     */
    public void add(Node2 node, int position) {
        // Validar posición
        if (position < 0 || position > size) {
            throw new IndexOutOfBoundsException("Posición inválida: " + position);
        }

        // Si la posición es 0, insertar al inicio
        if (position == 0) {
            addAtBeginning(node);
        } else if (position == size) {
            // Si es la última posición, usar add normal
            add(node);
        } else {
            // Insertar en posición intermedia
            Node2 current = getNodeAt(position);
            Node2 previous = current.getPrevious();

            node.setNext(current);
            node.setPrevious(previous);
            current.setPrevious(node);
            previous.setNext(node);

            size++;
        }
    }

    /**
     * Este método permite añadir un nuevo nodo al principio
     * de la lista enlazada doble.
     * @param node contiene el nodo a insertar.
     */
    public void addAtBeginning(Node2 node) {
        if (isEmpty()) {
            // Lista vacía
            this.head = node;
            this.tail = node;
            node.setPrevious(null);
            node.setNext(null);
        } else {
            // Lista no vacía: añadir al principio
            node.setNext(this.head);
            node.setPrevious(null);
            this.head.setPrevious(node);
            this.head = node;
        }
        size++;
    }

    /**
     * Este método permite eliminar un nodo de la lista
     * en la posición con valor [position]
     * @param position contiene la posición del nodo a eliminar.
     */
    public void delete(int position) {
        // Validar posición
        if (position < 0 || position >= size) {
            throw new IndexOutOfBoundsException("Posición inválida: " + position);
        }

        // Si la lista está vacía
        if (isEmpty()) {
            return;
        }

        Node2 nodeToDelete = getNodeAt(position);
        deleteNode(nodeToDelete);
    }

    /**
     * Este método elimina un nodo específico de la lista enlazada doble.
     * @param node contiene el nodo a eliminar.
     */
    public void deleteNode(Node2 node) {
        if (node == null) return;

        // Si es el único nodo
        if (size == 1) {
            this.head = null;
            this.tail = null;
        }
        // Si es el primer nodo
        else if (node == this.head) {
            this.head = node.getNext();
            this.head.setPrevious(null);
        }
        // Si es el último nodo
        else if (node == this.tail) {
            this.tail = node.getPrevious();
            this.tail.setNext(null);
        }
        // Si es un nodo intermedio
        else {
            Node2 previous = node.getPrevious();
            Node2 next = node.getNext();
            previous.setNext(next);
            next.setPrevious(previous);
        }

        size--;
    }


    /**
     * Este método devuelve el nodo que se encuentra en la posición
     * con valor [position]. Optimizado para recorrer desde el extremo más cercano.
     * @param position contiene la posición del nodo a devolver
     * @return el nodo en la posición [position]
     */
    private Node2 getNodeAt(int position) {
        // Validar posición
        if (position < 0 || position >= size) {
            throw new IndexOutOfBoundsException("Posición inválida: " + position);
        }

        Node2 current;
        // Optimización: empezar desde el extremo más cercano
        if (position < size / 2) {
            // Recorrer desde el principio
            current = this.head;
            for (int i = 0; i < position; i++) {
                current = current.getNext();
            }
        } else {
            // Recorrer desde el final
            current = this.tail;
            for (int i = size - 1; i > position; i--) {
                current = current.getPrevious();
            }
        }
        return current;
    }

    /**
     * Este método devuelve el nodo que se encuentra en la posición
     * con valor [position]
     * @param position contiene la posición del nodo a devolver
     * @return el nodo en la posición [position]
     */
    public Node2 get(int position) {
        return getNodeAt(position);
    }

    /**
     * Este método devuelve el contenido del nodo que se encuentra
     * en la posición con valor [position]
     * @param position contiene la posición del contenido a devolver
     * @return el contenido del nodo en la posición [position]
     */
    public int getContent(int position) {
        return getNodeAt(position).getContent();
    }

    /**
     * Indica si la lista está vacía o no.
     * @return devuelve [true] si y solo si la lista
     * no contiene ningún nodo. Devuelve [false] en caso
     * contrario.
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Este método muestra por pantalla los nodos
     * que contiene la lista enlazada. Siempre desde la
     * primera posición hasta la última.
     */
    public void show() {
        System.out.print("Lista: ");
        Node2 current = this.head;
        while (current != null) {
            System.out.print(current.getContent());
            if (current.getNext() != null) {
                System.out.print(" <-> ");
            }
            current = current.getNext();
        }
        System.out.println();
    }

    /**
     * Este método muestra por pantalla los nodos
     * que contiene la lista enlazada. Desde la
     * última posición hasta la primera (en reversa).
     */
    public void showReverse() {
        System.out.print("Lista (reversa): ");
        Node2 current = this.tail;
        while (current != null) {
            System.out.print(current.getContent());
            if (current.getPrevious() != null) {
                System.out.print(" <-> ");
            }
            current = current.getPrevious();
        }
        System.out.println();
    }

    /**
     * Este método elimina todos los elementos de la lista
     * enlazada doble, dejándola vacía.
     */
    public void clear() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
}