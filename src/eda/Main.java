package eda;

import java.util.ArrayList;

public class Main {
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
