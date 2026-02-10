package domain.List;

import java.util.List;
import java.util.ArrayList;
import domain.Request;

public class RequestQueue {
    private NodeRequest front;
    private NodeRequest rear;
    private int size;
    
    public RequestQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }
    
    public void enqueue(Request request) {
        NodeRequest newNode = new NodeRequest(request);
        
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.setNext(newNode);
            rear = newNode;
        }
        size++;
    }
    
    public Request dequeue() {
        if (isEmpty()) {
            return null;
        }
        
        Request data = front.getData();
        front = front.getNext();
        size--;
        
        if (isEmpty()) {
            rear = null;
        }
        
        return data;
    }
    
    public Request peek() {
        if (isEmpty()) {
            return null;
        }
        return front.getData();
    }
    
    public boolean isEmpty() {
        return front == null;
    }
    
    public int getSize() {
        return size;
    }
    
    public List<Request> getAll() {
        List<Request> list = new ArrayList<>();
        NodeRequest current = front;
        while (current != null) {
            list.add(current.getData());
            current = current.getNext();
        }
        return list;
    }
    
    public void clear() {
        front = null;
        rear = null;
        size = 0;
    }
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return "Queue: []";
        }
        
        StringBuilder sb = new StringBuilder("Queue: [");
        NodeRequest current = front;
        
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
