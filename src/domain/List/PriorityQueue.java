package domain.List;

import java.util.List;
import java.util.ArrayList;

public class PriorityQueue<T> {
    private Node head;
    private int size;
    
    private class Node {
        T data;
        int priority;
        Node next;
        
        Node(T data, int priority) {
            this.data = data;
            this.priority = priority;
            this.next = null;
        }
    }
    
    public PriorityQueue() {
        this.head = null;
        this.size = 0;
    }
    
    public void enqueue(T data, int priority) {
        Node newNode = new Node(data, priority);
        
        if (head == null || priority > head.priority) {
            newNode.next = head;
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null && current.next.priority >= priority) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }
    
    public T dequeue() {
        if (head == null) {
            return null;
        }
        T data = head.data;
        head = head.next;
        size--;
        return data;
    }
    
    public T peek() {
        if (head == null) {
            return null;
        }
        return head.data;
    }
    
    public boolean isEmpty() {
        return head == null;
    }
    
    public int getSize() {
        return size;
    }
    
    public List<T> getAll() {
        List<T> list = new ArrayList<>();
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
