package domain.List;

import domain.Request;

public class NodeRequest {
    private Request data;
    private NodeRequest next;
    
    public NodeRequest(Request data) {
        this.data = data;
        this.next = null;
    }
    
    public Request getData() {
        return data;
    }
    
    public void setData(Request data) {
        this.data = data;
    }
    
    public NodeRequest getNext() {
        return next;
    }
    
    public void setNext(NodeRequest next) {
        this.next = next;
    }
}
