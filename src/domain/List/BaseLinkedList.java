package domain.List;

public class BaseLinkedList {
    protected class Node {
        protected Object data;
        protected Node next;
        
        protected Node(Object data) {
            this.data = data;
            this.next = null;
        }
    }
    
    protected Node head;
    protected int size;
    
    public BaseLinkedList() {
        this.head = null;
        this.size = 0;
    }
    
    public void add(Object data) {
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
    
    public Object get(int index) {
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
    
    public void clear() {
        head = null;
        size = 0;
    }
    
    public boolean contains(Object data) {
        Node current = head;
        while (current != null) {
            if (current.data.equals(data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    public void addFirst(Object data) {
        Node newNode = new Node(data);
        newNode.next = head;
        head = newNode;
        size++;
    }
}
