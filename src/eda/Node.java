package eda;

public class Node {
    private int content;
    private Node next;

    public Node() {}

    public Node(int content) {
        this.content = content;
        this.next = null;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }

    public Node getNext() {
        return next;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    @Override
    public String toString() {
        return "Content = " + this.content;
    }
}
