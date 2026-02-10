package domain.List;

import domain.Service;

public class ServiceNode extends Node {
    public ServiceNode(Service service) {
        super(service);
    }
    
    public Service getService() {
        return (Service) data;
    }
}
