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
    
    public ServiceList getAll() {
        ServiceList list = new ServiceList();
        Node current = head;
        while (current != null) {
            list.add((Service) current.data);
            current = current.next;
        }
        return list;
    }
}
