package domain.List;

import domain.Request;

public class RequestNode extends Node {
    public RequestNode(Request request) {
        super(request);
    }

    public Request getData() {
        return (Request) data;
    }

    public Request getRequest() {
        return (Request) data;
    }

    @Override
    public RequestNode getNext() {
        return (RequestNode) next;
    }

    public void setNext(RequestNode next) {
        this.next = next;
    }
}
