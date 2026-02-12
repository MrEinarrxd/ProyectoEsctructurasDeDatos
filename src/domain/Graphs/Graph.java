package domain.Graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.List.EdgeList;
import domain.List.GraphNodeList;
import domain.List.StringList;

public class Graph {
    private GraphNodeList nodes;
    
    public Graph() {
        this.nodes = new GraphNodeList();
    }
    
    public void addNode(String name) {
        if (!hasNode(name)) {
            GraphNode newNode = new GraphNode(name);
            nodes.add(newNode);
        }
    }
    
    public void addEdge(String from, String to) {
        addEdge(from, to, 1);
    }
    
    public void addEdge(String from, String to, int weight) {
        addNode(from);
        addNode(to);
        
        GraphNode fromNode = getNode(from);
        fromNode.addEdge(to, weight);
        
        GraphNode toNode = getNode(to);
        toNode.addEdge(from, weight);
    }
    
    public GraphNode getNode(String name) {
        for (int i = 0; i < nodes.getSize(); i++) {
            GraphNode node = nodes.get(i);
            if (node != null && node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }
    
    public boolean hasNode(String name) {
        return getNode(name) != null;
    }
    
    public int getNodeCount() {
        return nodes.getSize();
    }

    public String bfs(String inicio) {
        String resultado = "=== EXPLORACIÓN BFS (Breadth-First Search) ===\n";
        resultado += "Inicio: " + inicio + "\n\n";

        if (!hasNode(inicio)) {
            resultado += "Error: Nodo no encontrado\n";
            return resultado;
        }

        StringList cola = new StringList();
        StringList visitado = new StringList();
        StringList recorrido = new StringList();

        cola.add(inicio);
        visitado.add(inicio);

        resultado += "Paso a paso:\n";
        int paso = 1;
        int colaIndex = 0;

        while (colaIndex < cola.getSize()) {
            String actual = cola.get(colaIndex);
            colaIndex++;
            recorrido.add(actual);

            resultado += paso++ + ". Visitando: " + actual + "\n";
            resultado += "   Vecinos: ";

            boolean tieneVecinos = false;
            GraphNode nodoActual = getNode(actual);
            if (nodoActual != null) {
                String[] vecinos = nodoActual.getNeighbors();
                for (String vecino : vecinos) {
                    if (vecino == null) continue;
                    tieneVecinos = true;
                    if (!visitado.contains(vecino)) {
                        cola.add(vecino);
                        visitado.add(vecino);
                        resultado += vecino + " (agregado) ";
                    } else {
                        resultado += vecino + " (ya visitado) ";
                    }
                }
            }

            if (!tieneVecinos) {
                resultado += "ninguno";
            }
            resultado += "\n\n";
        }

        resultado += "Orden de recorrido completo: ";
        for (int i = 0; i < recorrido.getSize(); i++) {
            resultado += recorrido.get(i);
            if (i < recorrido.getSize() - 1) resultado += " → ";
        }
        resultado += "\n";

        return resultado;
    }
    
    public Path dijkstra(String source, String target) {
        if (!hasNode(source) || !hasNode(target)) {
            return new Path(-1, new StringList());
        }
        if (source.equals(target)) {
            StringList path = new StringList();
            path.add(source);
            return new Path(0, path);
        }
        
        int total = nodes.getSize();
        int[] distance = new int[total];
        boolean[] visited = new boolean[total];
        String[] previous = new String[total];
        
        for (int i = 0; i < total; i++) {
            distance[i] = Integer.MAX_VALUE;
            visited[i] = false;
            previous[i] = null;
        }
        
        int sourceIndex = getNodeIndex(source);
        distance[sourceIndex] = 0;
        
        for (int count = 0; count < total - 1; count++) {
            int minDistance = Integer.MAX_VALUE;
            int minIndex = -1;
            
            for (int i = 0; i < total; i++) {
                if (!visited[i] && distance[i] < minDistance) {
                    minDistance = distance[i];
                    minIndex = i;
                }
            }
            
            if (minIndex == -1) break;
            
            visited[minIndex] = true;
            String currentName = getNode(minIndex).getName();
            
            GraphNode currentNode = getNode(currentName);
            EdgeList edges = currentNode.getEdges();
            
            for (int i = 0; i < edges.getSize(); i++) {
                Edge edge = (Edge) edges.get(i);
                if (edge != null) {
                    String neighborName = edge.getTo();
                    int neighborIndex = getNodeIndex(neighborName);
                    int weight = edge.getWeight();
                    
                    if (!visited[neighborIndex] && distance[minIndex] != Integer.MAX_VALUE && 
                        distance[minIndex] + weight < distance[neighborIndex]) {
                        distance[neighborIndex] = distance[minIndex] + weight;
                        previous[neighborIndex] = currentName;
                    }
                }
            }
        }
        
        int targetIndex = getNodeIndex(target);
        if (distance[targetIndex] == Integer.MAX_VALUE) {
            return new Path(-1, new StringList());
        }
        
        StringList path = new StringList();
        String current = target;
        while (current != null) {
            path.addFirst(current);
            if (current.equals(source)) break;
            current = previous[getNodeIndex(current)];
        }
        
        return new Path(distance[targetIndex], path);
    }

    public Map<String, List<Edge>> getEdgeMap() {
        Map<String, List<Edge>> mapa = new HashMap<>();
        for (int i = 0; i < nodes.getSize(); i++) {
            GraphNode node = nodes.get(i);
            if (node == null) continue;

            List<Edge> edges = new ArrayList<>();
            EdgeList edgeList = node.getEdges();
            for (int j = 0; j < edgeList.getSize(); j++) {
                Edge edge = (Edge) edgeList.get(j);
                if (edge != null) {
                    edges.add(new Edge(edge.getTo(), edge.getWeight()));
                }
            }
            mapa.put(node.getName(), edges);
        }
        return mapa;
    }
    
    public GraphNode getNode(int index) {
        if (index >= 0 && index < nodes.getSize()) {
            return nodes.get(index);
        }
        return null;
    }
    
    private int getNodeIndex(String name) {
        for (int i = 0; i < nodes.getSize(); i++) {
            GraphNode node = nodes.get(i);
            if (node != null && node.getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public String toString() {
        String result = "Graph{\n";
        for (int i = 0; i < nodes.getSize(); i++) {
            GraphNode node = nodes.get(i);
            if (node != null) {
                result += "  " + node.getName() + " -> ";
                String[] neighbors = node.getNeighbors();
                for (int j = 0; j < neighbors.length; j++) {
                    if (neighbors[j] != null) {
                        result += neighbors[j] + " ";
                    }
                }
                result += "\n";
            }
        }
        result += "}";
        return result;
    }
}
