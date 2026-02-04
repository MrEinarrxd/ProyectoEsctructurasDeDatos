package domain.Graphs;

import domain.Vehicle;
import domain.List.VehicleListGraph;
import domain.List.EdgeList;

public class GraphNode {
    private String name;
    private VehicleListGraph vehicles;
    private EdgeList edges;
    
    public GraphNode(String name) {
        this.name = name;
        this.vehicles = new VehicleListGraph();
        this.edges = new EdgeList();
    }
    
    public String getName() {
        return name;
    }
    
    public void addVehicle(Vehicle vehicle) {
        if (!vehicles.contains(vehicle)) {
            vehicles.add(vehicle);
        }
    }
    
    public int getVehicleCount() {
        return vehicles.getSize();
    }
    
    public Vehicle getVehicle(int index) {
        return vehicles.get(index);
    }
    
    public void addEdge(String to) {
        addEdge(to, 1);
    }
    
    public void addEdge(String to, int weight) {
        for (int i = 0; i < edges.getSize(); i++) {
            Edge edge = (Edge) edges.get(i);
            if (edge != null && edge.getTo().equals(to)) {
                return;
            }
        }
        edges.add(new Edge(to, weight));
    }
    
    public String[] getNeighbors() {
        String[] neighbors = new String[edges.getSize()];
        for (int i = 0; i < edges.getSize(); i++) {
            Edge edge = (Edge) edges.get(i);
            if (edge != null) {
                neighbors[i] = edge.getTo();
            }
        }
        return neighbors;
    }
    
    public EdgeList getEdges() {
        return edges;
    }
    
    @Override
    public String toString() {
        return "GraphNode{name='" + name + "', vehicles=" + vehicles.getSize() + ", edges=" + edges.getSize() + "}";
    }
}
