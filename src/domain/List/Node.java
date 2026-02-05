package domain.List;

public class Node {
    protected Object data;
    protected Node next;
    
    public Node(Object data) {
        this.data = data;
        this.next = null;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    public Node getNext() {
        return next;
    }
    
    public void setNext(Node next) {
        this.next = next;
    }
}
