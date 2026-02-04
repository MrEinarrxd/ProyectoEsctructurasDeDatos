package domain;

import java.time.LocalDateTime;

public class Request {
    private String id;
    private String origin;
    private String destination;
    private String clientName;
    private int clientCategory; // 0=económico, 1=regular, 2=VIP, 3=emergencia
    private String status;
    private LocalDateTime timestamp;
    private String assignedVehicleId;
    
    public Request(String id, String origin, String destination, String clientName, int clientCategory) {
        this.id = id;
        this.origin = origin;
        this.destination = destination;
        this.clientName = clientName;
        this.clientCategory = clientCategory;
        this.status = "PENDING";
        this.timestamp = LocalDateTime.now();
        this.assignedVehicleId = null;
    }

    // Constructor compatible con versiones anteriores
    public Request(String id, String origin, String destination, String clientName, int priority, int clientCategory) {
        this(id, origin, destination, clientName, clientCategory);
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getOrigin() {
        return origin;
    }
    
    public void setOrigin(String origin) {
        this.origin = origin;
    }
    
    public String getDestination() {
        return destination;
    }
    
    public void setDestination(String destination) {
        this.destination = destination;
    }
    
    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    
    public int getClientCategory() {
        return clientCategory;
    }
    
    public void setClientCategory(int clientCategory) {
        this.clientCategory = clientCategory;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getAssignedVehicleId() {
        return assignedVehicleId;
    }
    
    public void setAssignedVehicleId(String assignedVehicleId) {
        this.assignedVehicleId = assignedVehicleId;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Request request = (Request) obj;
        return id.equals(request.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return "Request{" +
                "id='" + id + '\'' +
                ", client='" + clientName + '\'' +
                ", from='" + origin + '\'' +
                ", to='" + destination + '\'' +
                ", category=" + clientCategory +
                ", status='" + status + '\'' +
                ", vehicle='" + (assignedVehicleId != null ? assignedVehicleId : "none") + '\'' +
                '}';
    }
}
