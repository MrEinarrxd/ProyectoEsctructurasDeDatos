package domain.List;

import domain.Graphs.GraphNode;

public class GraphNodeList {
    private class Node {
        GraphNode data;
        Node next;
        
        Node(GraphNode data) {
            this.data = data;
            this.next = null;
        }
    }
    
    private Node head;
    private int size;
    
    public GraphNodeList() {
        this.head = null;
        this.size = 0;
    }
    
    public void add(GraphNode node) {
        Node newNode = new Node(node);
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
    
    public GraphNode get(int index) {
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
