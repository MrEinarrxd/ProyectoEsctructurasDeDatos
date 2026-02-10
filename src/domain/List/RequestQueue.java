package domain.List;

import domain.Request;

public class RequestQueue {
    private RequestNode front;
    private RequestNode rear;
    private int size;
    
    public RequestQueue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }
    
    public void enqueue(Request request) {
        RequestNode newNode = new RequestNode(request);
        
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
    
    public Request get(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        RequestNode current = front;
        for (int i = 0; i < index; i++) {
            current = current.getNext();
        }
        return current.getData();
    }
    
    public RequestQueue getAll() {
        RequestQueue queue = new RequestQueue();
        RequestNode current = front;
        while (current != null) {
            queue.enqueue(current.getData());
            current = current.getNext();
        }
        return queue;
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
        
        String result = "Queue: [";
        RequestNode current = front;
        
        while (current != null) {
            result += current.getData();
            if (current.getNext() != null) {
                result += " -> ";
            }
            current = current.getNext();
        }
        
        result += "]";
        return result;
    }
}
