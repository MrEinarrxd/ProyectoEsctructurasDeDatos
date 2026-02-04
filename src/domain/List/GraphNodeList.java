package domain.List;

import domain.Graphs.GraphNode;

public class GraphNodeList extends BaseLinkedList {
    
    public void add(GraphNode node) {
        super.add(node);
    }
    
    public GraphNode get(int index) {
        Object obj = super.get(index);
        return (GraphNode) obj;
    }
}
