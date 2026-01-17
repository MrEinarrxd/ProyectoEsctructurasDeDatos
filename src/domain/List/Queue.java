package domain.List;

import domain.Request;

public class Queue {
    private NodeRequest front;
    private NodeRequest rear;
    private int size;
    
    public Queue() {
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
