package domain.Graphs;

import domain.Vehicle;
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
    
    public void addVehicleToNode(String nodeName, Vehicle vehicle) {
        GraphNode node = getNode(nodeName);
        if (node != null) {
            node.addVehicle(vehicle);
        }
    }
    
    public int getNodeCount() {
        return nodes.getSize();
    }
    
    public PathResult shortestPath(String from, String to) {
        if (!hasNode(from) || !hasNode(to)) {
            return new PathResult(-1, new StringList());
        }
        if (from.equals(to)) {
            StringList path = new StringList();
            path.add(from);
            return new PathResult(0, path);
        }
        
        int total = nodes.getSize();
        StringList queue = new StringList();
        int headIndex = 0;
        boolean[] visited = new boolean[total];
        String[] previous = new String[total];
        int[] distance = new int[total];
        
        for (int i = 0; i < total; i++) {
            distance[i] = -1;
        }
        
        int fromIndex = getNodeIndex(from);
        visited[fromIndex] = true;
        distance[fromIndex] = 0;
        queue.add(from);
        
        while (headIndex < queue.getSize()) {
            String currentName = queue.get(headIndex);
            headIndex++;
            
            if (currentName.equals(to)) {
                StringList path = reconstructPath(previous, from, to);
                return new PathResult(distance[getNodeIndex(to)], path);
            }
            
            GraphNode currentNode = getNode(currentName);
            String[] neighbors = currentNode.getNeighbors();
            
            for (int i = 0; i < neighbors.length; i++) {
                String neighbor = neighbors[i];
                if (neighbor == null) continue;
                
                int neighborIndex = getNodeIndex(neighbor);
                if (!visited[neighborIndex]) {
                    visited[neighborIndex] = true;
                    previous[neighborIndex] = currentName;
                    distance[neighborIndex] = distance[getNodeIndex(currentName)] + 1;
                    queue.add(neighbor);
                }
            }
        }
        return new PathResult(-1, new StringList());
    }
    
    public PathResult dijkstra(String source, String target) {
        if (!hasNode(source) || !hasNode(target)) {
            return new PathResult(-1, new StringList());
        }
        if (source.equals(target)) {
            StringList path = new StringList();
            path.add(source);
            return new PathResult(0, path);
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
            return new PathResult(-1, new StringList());
        }
        
        StringList path = new StringList();
        String current = target;
        while (current != null) {
            path.addFirst(current);
            if (current.equals(source)) break;
            current = previous[getNodeIndex(current)];
        }
        
        return new PathResult(distance[targetIndex], path);
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
    
    private StringList reconstructPath(String[] previous, String from, String to) {
        StringList path = new StringList();
        String current = to;
        
        while (current != null) {
            path.addFirst(current);
            if (current.equals(from)) break;
            current = previous[getNodeIndex(current)];
        }
        
        return path;
    }
    
    // BFS - Recorrido en anchura
    public String bfsExploration(String inicio) {
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== EXPLORACIÓN BFS (Breadth-First Search) ===\n");
        resultado.append("Inicio: ").append(inicio).append("\n\n");
        
        if (!hasNode(inicio)) {
            resultado.append("Error: Nodo no encontrado\n");
            return resultado.toString();
        }
        
        int total = nodes.getSize();
        boolean[] visitado = new boolean[total];
        StringList cola = new StringList();
        StringList recorrido = new StringList();
        
        int inicioIndex = getNodeIndex(inicio);
        visitado[inicioIndex] = true;
        cola.add(inicio);
        
        resultado.append("Paso a paso:\n");
        int paso = 1;
        
        int headIndex = 0;
        while (headIndex < cola.getSize()) {
            String actual = cola.get(headIndex);
            headIndex++;
            recorrido.add(actual);
            
            resultado.append(paso++).append(". Visitando: ").append(actual).append("\n");
            
            GraphNode nodo = getNode(actual);
            String[] vecinos = nodo.getNeighbors();
            
            resultado.append("   Vecinos: ");
            boolean tieneVecinos = false;
            for (int i = 0; i < vecinos.length; i++) {
                if (vecinos[i] != null) {
                    tieneVecinos = true;
                    int vecinoIndex = getNodeIndex(vecinos[i]);
                    if (!visitado[vecinoIndex]) {
                        visitado[vecinoIndex] = true;
                        cola.add(vecinos[i]);
                        resultado.append(vecinos[i]).append(" (agregado) ");
                    } else {
                        resultado.append(vecinos[i]).append(" (ya visitado) ");
                    }
                }
            }
            if (!tieneVecinos) {
                resultado.append("ninguno");
            }
            resultado.append("\n\n");
        }
        
        resultado.append("Orden de recorrido: ");
        for (int i = 0; i < recorrido.getSize(); i++) {
            resultado.append(recorrido.get(i));
            if (i < recorrido.getSize() - 1) resultado.append(" → ");
        }
        resultado.append("\n");
        
        return resultado.toString();
    }
    
    public StringList getAllNodeNames() {
        StringList names = new StringList();
        for (int i = 0; i < nodes.getSize(); i++) {
            GraphNode node = nodes.get(i);
            if (node != null) {
                names.add(node.getName());
            }
        }
        return names;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Graph{\n");
        for (int i = 0; i < nodes.getSize(); i++) {
            GraphNode node = nodes.get(i);
            if (node != null) {
                sb.append("  ").append(node.getName()).append(" -> ");
                String[] neighbors = node.getNeighbors();
                for (int j = 0; j < neighbors.length; j++) {
                    if (neighbors[j] != null) {
                        sb.append(neighbors[j]).append(" ");
                    }
                }
                sb.append("\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
