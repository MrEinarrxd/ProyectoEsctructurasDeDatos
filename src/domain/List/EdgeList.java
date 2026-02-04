package domain.List;

import domain.Graphs.Edge;

public class EdgeList extends BaseLinkedList {
    
    public void add(Edge edge) {
        super.add(edge);
    }
    
    public Edge get(int index) {
        Object obj = super.get(index);
        return (Edge) obj;
    }
}
