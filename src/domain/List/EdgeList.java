package domain.List;

import domain.Graphs.Edge;

public class EdgeList {
    private class Node {
        Edge data;
        Node next;
        
        Node(Edge data) {
            this.data = data;
            this.next = null;
        }
    }
    
    private Node head;
    private int size;
    
    public EdgeList() {
        this.head = null;
        this.size = 0;
    }
    
    public void add(Edge edge) {
        Node newNode = new Node(edge);
        if (head == null) {
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null) {
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }
    
    public Edge get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        
        Node current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
    
    public int getSize() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
}
