package domain.List;

import domain.Vehicle;
import java.util.ArrayList;
import java.util.List;

public class VehicleList {
    private NodeVehicle head;
    private int size;
    
    public VehicleList() {
        this.head = null;
        this.size = 0;
    }
    
    public void add(Vehicle vehiculo) {
        NodeVehicle newNode = new NodeVehicle(vehiculo);
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
    
    public void agregar(Vehicle vehiculo) {
        add(vehiculo);
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
    
    public boolean contains(Vehicle vehiculo) {
        NodeVehicle current = head;
        while (current != null) {
            if (current.getData().equals(vehiculo)) {
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
    
    public List<Vehicle> obtenerTodos() {
        List<Vehicle> lista = new ArrayList<>();
        NodeVehicle current = head;
        while (current != null) {
            lista.add(current.getData());
            current = current.getNext();
        }
        return lista;
    }
    
    public Vehicle buscarDisponible(String zona) {
        NodeVehicle current = head;
        while (current != null) {
            Vehicle v = current.getData();
            if (v.isAvailable() && v.getCurrentZone().equals(zona)) {
                return v;
            }
            current = current.getNext();
        }
        return null;
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
