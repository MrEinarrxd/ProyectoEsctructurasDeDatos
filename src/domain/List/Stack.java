package domain.List;

public class Stack {
    private NodeHistory top;
    private int size;
    
    public Stack() {
        this.top = null;
        this.size = 0;
    }
    
    public void push(String action) {
        NodeHistory newNode = new NodeHistory(action);
        newNode.setNext(top);
        top = newNode;
        size++;
    }
    
    public String pop() {
        if (isEmpty()) {
            return null;
        }
        
        String data = top.getData();
        top = top.getNext();
        size--;
        
        return data;
    }
    
    public String peek() {
        if (isEmpty()) {
            return null;
        }
        return top.getData();
    }
    
    public boolean isEmpty() {
        return top == null;
    }
    
    public int getSize() {
        return size;
    }
    
    public void clear() {
        top = null;
        size = 0;
    }
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "Stack: []";
        }
        
        StringBuilder sb = new StringBuilder("Stack (top -> bottom): [");
        NodeHistory current = top;
        
        while (current != null) {
            sb.append(current.getData());
            if (current.getNext() != null) {
                sb.append(" -> ");
            }
            current = current.getNext();
        }
        
        sb.append("]");
        return sb.toString();
    }
}
