package domain.List;

import domain.Vehicle;

public class List {
    private NodeVehicle head;
    private int size;
    
    public List() {
        this.head = null;
        this.size = 0;
    }
    
    public void add(Vehicle vehicle) {
        NodeVehicle newNode = new NodeVehicle(vehicle);
        
        if (isEmpty()) {
            head = newNode;
        } else {
            NodeVehicle current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newNode);
        }
        size++;
    }
    
    public void addFirst(Vehicle vehicle) {
        NodeVehicle newNode = new NodeVehicle(vehicle);
        newNode.setNext(head);
        head = newNode;
        size++;
    }
    
    public boolean remove(Vehicle vehicle) {
        if (isEmpty()) {
            return false;
        }
        
        if (head.getData().equals(vehicle)) {
            head = head.getNext();
            size--;
            return true;
        }
        
        NodeVehicle current = head;
        while (current.getNext() != null) {
            if (current.getNext().getData().equals(vehicle)) {
                current.setNext(current.getNext().getNext());
                size--;
                return true;
            }
            current = current.getNext();
        }
        
        return false;
    }
    
    public Vehicle get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        
        NodeVehicle current = head;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        
        return current.getData();
    }
    
    public boolean contains(Vehicle vehicle) {
        NodeVehicle current = head;
        while (current != null) {
            if (current.getData().equals(vehicle)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }
    
    public boolean isEmpty() {
        return head == null;
    }
    
    public int getSize() {
        return size;
    }
    
    public void clear() {
        head = null;
        size = 0;
    }
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "List: []";
        }
        
        StringBuilder sb = new StringBuilder("List: [");
        NodeVehicle current = head;
        
        while (current != null) {
            sb.append(current.getData());
            if (current.getNext() != null) {
                sb.append(", ");
            }
            current = current.getNext();
        }
        
        sb.append("]");
        return sb.toString();
    }
}
