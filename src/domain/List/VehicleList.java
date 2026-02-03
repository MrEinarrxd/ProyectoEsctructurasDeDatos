package domain.List;

import domain.Vehicle;

public class VehicleList {
    private NodeVehicle head;
    private int size;
    
    public VehicleList() {
        this.head = null;
        this.size = 0;
    }
    
    public void add(Vehicle vehicle) {
        NodeVehicle newNode = new NodeVehicle(vehicle);
        if (head == null) {
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
    
    public int getSize() {
        return size;
    }
    
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "Lista vacia";
        }
        
        StringBuilder sb = new StringBuilder();
        NodeVehicle current = head;
        while (current != null) {
            sb.append(current.getData().toString()).append("\n");
            current = current.getNext();
        }
        return sb.toString();
    }
}
