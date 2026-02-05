package domain.List;

import domain.Vehicle;

public class VehicleNode extends Node {
    public VehicleNode(Vehicle vehicle) {
        super(vehicle);
    }
    
    public Vehicle getVehicle() {
        return (Vehicle) data;
    }
}
