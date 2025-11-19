
package eda;

import java.util.ArrayList;

/**
 * Clase principal para demostrar el uso de la lista enlazada simple.
 *
 * Esta clase contiene ejemplos de las operaciones básicas que se pueden
 * realizar con una SimpleLinkedList.
 *
 * @author Sistema EDA
 * @version 1.0
 * @see SimpleLinkedList
 * @see Node
 */
public class Main {
    /**
     * Método principal que demuestra el uso de la lista enlazada simple.
     *
     * Operaciones demostradas:
     * - Creación de nodos
     * - Inserción al final
     * - Inserción en posición específica
     * - Eliminación por posición
     * - Visualización de la lista
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        //ArrayList<Integer> l = new ArrayList<>();
        SimpleLinkedList l = new SimpleLinkedList();
        Node n = new Node(20);
        Node n2 = new Node(-3);
        Node n3 = new Node(50);
        l.add(n);
        l.add(n2);
        l.add(n3);
        l.add(new Node(100), 2);
        l.show();
        l.delete(1);
        l.show();
    }
}