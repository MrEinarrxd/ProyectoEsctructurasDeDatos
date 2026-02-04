package domain.List;

import domain.Service;

public class ServiceList extends BaseLinkedList {
    
    public void add(Service data) {
        super.add(data);
    }
    
    public Service get(int index) {
        Object obj = super.get(index);
        return (Service) obj;
    }
    
    public java.util.List<Service> getAll() {
        java.util.List<Service> list = new java.util.ArrayList<>();
        Node current = head;
        while (current != null) {
            list.add((Service) current.data);
            current = current.next;
        }
        return list;
    }
}
