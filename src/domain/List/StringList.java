package domain.List;

import java.util.ArrayList;
import java.util.List;

public class StringList extends BaseLinkedList {
    
    public void add(String data) {
        super.add(data);
    }
    
    public void addFirst(String data) {
        super.addFirst(data);
    }
    
    public String get(int index) {
        Object obj = super.get(index);
        return (String) obj;
    }
    
    public boolean contains(String data) {
        return super.contains(data);
    }
    
    public List<String> getAll() {
        List<String> list = new ArrayList<>();
        Node current = head;
        while (current != null) {
            list.add((String) current.data);
            current = current.next;
        }
        return list;
    }
    
    public String[] toArray() {
        String[] arr = new String[size];
        Node current = head;
        int i = 0;
        while (current != null) {
            arr[i++] = (String) current.data;
            current = current.next;
        }
        return arr;
    }
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder("[");
        Node current = head;
        while (current != null) {
            sb.append((String) current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}

