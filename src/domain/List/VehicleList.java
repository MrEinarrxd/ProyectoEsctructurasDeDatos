package domain.List;

import domain.Vehicle;
import java.util.ArrayList;
import java.util.List;

public class VehicleList extends BaseLinkedList {

    public void add(Vehicle vehiculo) {
        VehicleNode newNode = new VehicleNode(vehiculo);
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

    public Vehicle get(int index) {
        Object obj = super.get(index);
        return (Vehicle) obj;
    }

    public boolean contains(Vehicle vehiculo) {
        return super.contains(vehiculo);
    }

    public List<Vehicle> obtenerTodos() {
        List<Vehicle> lista = new ArrayList<>();
        Node current = head;
        while (current != null) {
            VehicleNode vehicleNode = (VehicleNode) current;
            lista.add(vehicleNode.getVehicle());
            current = current.next;
        }
        return lista;
    }
    
    public Vehicle buscarDisponible(String zona) {
        Node current = head;
        while (current != null) {
            VehicleNode vehicleNode = (VehicleNode) current;
            Vehicle v = vehicleNode.getVehicle();
            if (v.isAvailable() && v.getCurrentZone().equals(zona)) {
                return v;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Lista vacia";
        }
        
        String result = "";
        Node current = head;
        while (current != null) {
            VehicleNode vehicleNode = (VehicleNode) current;
            result += vehicleNode.getVehicle().toString() + "\n";
            current = current.next;
        }
        return result;
    }
}
