
package eda;

import java.util.ArrayList;

/**
 * Clase principal para demostrar el uso de la lista doblemente enlazada.
 *
 * Esta clase contiene ejemplos de las operaciones básicas que se pueden
 * realizar con una DoubleLinkedList.
 *
 * @author Sistema EDA
 * @version 1.0
 * @see DoubleLinkedList
 * @see Node2
 */
public class Main2 {
    /**
     * Método principal que demuestra el uso de la lista doblemente enlazada.
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
        // Probando DoubleLinkedList
        DoubleLinkedList l = new DoubleLinkedList();

        // Crear nodos usando Node2 (para lista doblemente enlazada)
        Node2 n = new Node2(20);
        Node2 n2 = new Node2(-3);
        Node2 n3 = new Node2(50);
        l.add(n);
        l.add(n2);
        l.add(n3);
        l.add(new Node2(100), 2);
        l.show();
        l.delete(1);
        l.show();
    }
}