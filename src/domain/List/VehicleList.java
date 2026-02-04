package domain.List;

import domain.Vehicle;
import java.util.ArrayList;
import java.util.List;

public class VehicleList extends BaseLinkedList {
    
    public void add(Vehicle vehiculo) {
        super.add(vehiculo);
    }
    
    public void agregar(Vehicle vehiculo) {
        add(vehiculo);
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
            lista.add((Vehicle) current.data);
            current = current.next;
        }
        return lista;
    }
    
    public Vehicle buscarDisponible(String zona) {
        Node current = head;
        while (current != null) {
            Vehicle v = (Vehicle) current.data;
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
        
        StringBuilder sb = new StringBuilder();
        Node current = head;
        while (current != null) {
            sb.append(((Vehicle) current.data).toString()).append("\n");
            current = current.next;
        }
        return sb.toString();
    }
}
