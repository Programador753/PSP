package eda;

import java.util.ArrayList;

public class Main2 {
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