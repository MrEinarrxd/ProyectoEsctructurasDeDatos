package domain.List;

import domain.Request;

public class RequestPriorityQueue {
    private Node head;
    private int size;

    private class Node {
        Request data;
        int priority;
        Node next;

        Node(Request data, int priority) {
            this.data = data;
            this.priority = priority;
            this.next = null;
        }
    }

    public RequestPriorityQueue() {
        this.head = null;
        this.size = 0;
    }

    public void enqueue(Request data, int priority) {
        Node newNode = new Node(data, priority);

        if (head == null || priority > head.priority) {
            newNode.next = head;
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null && current.next.priority >= priority) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    public Request dequeue() {
        if (head == null) {
            return null;
        }
        Request data = head.data;
        head = head.next;
        size--;
        return data;
    }

    public Request peek() {
        if (head == null) {
            return null;
        }
        return head.data;
    }

    public boolean isEmpty() {
        return head == null;
    }

    public int getSize() {
        return size;
    }

    public RequestQueue getAll() {
        RequestQueue queue = new RequestQueue();
        Node current = head;
        while (current != null) {
            queue.enqueue(current.data);
            current = current.next;
        }
        return queue;
    }

    public void clear() {
        head = null;
        size = 0;
    }
}
