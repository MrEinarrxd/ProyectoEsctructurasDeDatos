package domain.List;

import domain.Vehicle;

public class NodeVehicle {
    private Vehicle data;
    private NodeVehicle next;
    
    public NodeVehicle(Vehicle data) {
        this.data = data;
        this.next = null;
    }
    
    public Vehicle getData() {
        return data;
    }
    
    public void setData(Vehicle data) {
        this.data = data;
    }
    
    public NodeVehicle getNext() {
        return next;
    }
    
    public void setNext(NodeVehicle next) {
        this.next = next;
    }
}