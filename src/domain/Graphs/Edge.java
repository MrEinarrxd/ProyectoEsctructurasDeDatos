package domain.Graphs;

public class Edge {
    private String to;
    private int weight;
    
    public Edge(String to) {
        this.to = to;
        this.weight = 1;
    }
    
    public Edge(String to, int weight) {
        this.to = to;
        this.weight = weight;
    }
    
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    @Override
    public String toString() {
        return to + "(" + weight + ")";
    }
}
