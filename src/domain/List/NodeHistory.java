package domain.List;

public class NodeHistory {
    private String data;
    private NodeHistory next;
    
    public NodeHistory(String data) {
        this.data = data;
        this.next = null;
    }
    
    public String getData() {
        return data;
    }
    
    public void setData(String data) {
        this.data = data;
    }
    
    public NodeHistory getNext() {
        return next;
    }
    
    public void setNext(NodeHistory next) {
        this.next = next;
    }
}