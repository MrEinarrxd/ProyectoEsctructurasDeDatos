package domain;

public class Vehicle {
    private String id;
    private String driverName;
    private String currentZone;
    private boolean available;
    private String vehicleType;
    private int serviceCount;
    
    public Vehicle(String id, String driverName, String currentZone, String vehicleType) {
        this.id = id;
        this.driverName = driverName;
        this.currentZone = currentZone;
        this.vehicleType = vehicleType;
        this.available = true;
        this.serviceCount = 0;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getDriverName() {
        return driverName;
    }
    
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    
    public String getCurrentZone() {
        return currentZone;
    }
    
    public void setCurrentZone(String currentZone) {
        this.currentZone = currentZone;
    }
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public String getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public int getServiceCount() {
        return serviceCount;
    }
    
    public void incrementServiceCount() {
        this.serviceCount++;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehicle vehicle = (Vehicle) obj;
        return id.equals(vehicle.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", driver='" + driverName + '\'' +
                ", zone='" + currentZone + '\'' +
                ", type='" + vehicleType + '\'' +
                ", available=" + available +
                ", services=" + serviceCount +
                '}';
    }
}
