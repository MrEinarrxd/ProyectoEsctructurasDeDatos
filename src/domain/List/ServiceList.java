package domain.List;

import domain.Service;

public class ServiceList {
    private class Node {
        Service data;
        Node next;
        
        Node(Service data) {
            this.data = data;
            this.next = null;
        }
    }
    
    private Node head;
    private int size;
    
    public ServiceList() {
        this.head = null;
        this.size = 0;
    }
    
    public void add(Service data) {
        Node newNode = new Node(data);
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
    
    public Service get(int index) {
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
    
    public java.util.List<Service> getAll() {
        java.util.List<Service> list = new java.util.ArrayList<>();
        Node current = head;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }
    
    public void clear() {
        head = null;
        size = 0;
    }
}
