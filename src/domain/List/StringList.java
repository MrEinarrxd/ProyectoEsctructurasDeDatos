package domain.List;



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
    
    public StringList getAll() {
        StringList list = new StringList();
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
        
        String result = "[";
        Node current = head;
        while (current != null) {
            result += (String) current.data;
            if (current.next != null) {
                result += ", ";
            }
            current = current.next;
        }
        result += "]";
        return result;
    }
}

