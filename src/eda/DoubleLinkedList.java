
package eda;

/**
 * Clase que implementa una lista doblemente enlazada.
 *
 * Una lista doblemente enlazada es una estructura de datos lineal donde cada elemento
 * (nodo) contiene un valor y referencias tanto al siguiente como al anterior elemento.
 *
 * Operaciones soportadas:
 * - Inserción al final de la lista
 * - Inserción al principio de la lista
 * - Inserción en posición específica
 * - Eliminación por posición
 * - Eliminación de un nodo específico
 * - Consulta de elementos
 * - Recorrido normal y en reversa
 * - Verificación de lista vacía
 *
 * @author Sistema EDA
 * @version 1.0
 * @see Node2
 */
public class DoubleLinkedList {
    /** Primer nodo de la lista */
    private Node2 head;

    /** Último nodo de la lista */
    private Node2 tail;

    /** Tamaño actual de la lista */
    private int size;

    /**
     * Constructor que crea una lista doblemente enlazada vacía.
     */
    public DoubleLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
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
     * @return Primer nodo (head), o null si la lista está vacía
     */
    public Node2 getHead() {
        return head;
    }

    /**
     * Establece el primer nodo de la lista.
     *
     * @param head Nuevo primer nodo de la lista
     */
    public void setHead(Node2 head) {
        this.head = head;
    }

    /**
     * Obtiene el último nodo de la lista.
     *
     * @return Último nodo (tail), o null si la lista está vacía
     */
    public Node2 getTail() {
        return tail;
    }

    /**
     * Establece el último nodo de la lista.
     *
     * @param tail Nuevo último nodo de la lista
     */
    public void setTail(Node2 tail) {
        this.tail = tail;
    }

    /**
     * Añade un nuevo nodo al final de la lista.
     *
     * Si la lista está vacía, el nodo se convierte tanto en head como en tail.
     * Si la lista no está vacía, el nodo se añade después del tail actual.
     *
     * @param node Nodo a insertar al final de la lista
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
     * Inserta un nodo en una posición específica de la lista.
     *
     * Las posiciones comienzan en 0. Si la posición es 0, el nodo se inserta
     * al principio. Si la posición es igual al tamaño, se inserta al final.
     *
     * @param node Nodo a insertar
     * @param position Posición donde insertar el nodo (0-indexed)
     * @throws IndexOutOfBoundsException Si la posición es inválida (menor que 0 o mayor que size)
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
     * Añade un nuevo nodo al principio de la lista.
     *
     * Si la lista está vacía, el nodo se convierte tanto en head como en tail.
     * Si la lista no está vacía, el nodo se convierte en el nuevo head.
     *
     * @param node Nodo a insertar al principio de la lista
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
     * Elimina el nodo en la posición especificada.
     *
     * @param position Posición del nodo a eliminar (0-indexed)
     * @throws IndexOutOfBoundsException Si la posición es inválida (menor que 0 o mayor o igual que size)
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
     * Elimina un nodo específico de la lista enlazada doble.
     *
     * Maneja los casos especiales:
     * - Si es el único nodo en la lista
     * - Si es el primer nodo (head)
     * - Si es el último nodo (tail)
     * - Si es un nodo intermedio
     *
     * @param node Nodo a eliminar de la lista
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
     * Obtiene el nodo que se encuentra en la posición especificada.
     *
     * Método privado optimizado que recorre la lista desde el extremo más cercano
     * (principio o final) para minimizar el número de iteraciones.
     *
     * @param position Posición del nodo a obtener (0-indexed)
     * @return Nodo en la posición especificada
     * @throws IndexOutOfBoundsException Si la posición es inválida
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
     * Obtiene el nodo que se encuentra en la posición especificada.
     *
     * @param position Posición del nodo a obtener (0-indexed)
     * @return Nodo en la posición especificada
     * @throws IndexOutOfBoundsException Si la posición es inválida
     */
    public Node2 get(int position) {
        return getNodeAt(position);
    }

    /**
     * Obtiene el contenido del nodo que se encuentra en la posición especificada.
     *
     * @param position Posición del contenido a obtener (0-indexed)
     * @return Valor entero almacenado en el nodo de la posición especificada
     * @throws IndexOutOfBoundsException Si la posición es inválida
     */
    public int getContent(int position) {
        return getNodeAt(position).getContent();
    }

    /**
     * Verifica si la lista está vacía.
     *
     * @return true si la lista no contiene ningún nodo, false en caso contrario
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Muestra por pantalla todos los nodos de la lista.
     *
     * Los nodos se muestran desde el primero (head) hasta el último (tail),
     * con el formato: "Lista: valor1 <-> valor2 <-> valor3"
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
     * Muestra por pantalla todos los nodos de la lista en orden inverso.
     *
     * Los nodos se muestran desde el último (tail) hasta el primero (head),
     * con el formato: "Lista (reversa): valor3 <-> valor2 <-> valor1"
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
     * Elimina todos los elementos de la lista, dejándola vacía.
     *
     * Resetea head y tail a null, y el tamaño a 0.
     */
    public void clear() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
}