package domain.Graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Búsqueda en Anchura (Breadth-First Search)
     * Recorre el grafo nivel por nivel desde un nodo inicial.
     * Usa una cola para procesar nodos en el orden que fueron descubiertos.
     * Complejidad: O(V + E) donde V = vértices, E = aristas
     */
    public String bfs(String start) {
        StringBuilder result = new StringBuilder();
        result.append("=== EXPLORACIÓN BFS (Breadth-First Search) ===\n");
        result.append("Inicio: ").append(start).append("\n\n");

        if (!hasNode(start)) {
            result.append("Error: Nodo no encontrado\n");
            return result.toString();
        }

        StringList queue = new StringList();
        StringList visited = new StringList();
        StringList traversal = new StringList();

        queue.add(start);
        visited.add(start);

        result.append("Paso a paso:\n");
        int step = 1;
        int queueIndex = 0;

        while (queueIndex < queue.getSize()) {
            String current = queue.get(queueIndex);
            queueIndex++;
            traversal.add(current);

            result.append(step++).append(". Visitando: ").append(current).append("\n");
            result.append("   Vecinos: ");

            boolean hasNeighbors = false;
            GraphNode currentNode = getNode(current);
            if (currentNode != null) {
                String[] neighbors = currentNode.getNeighbors();
                for (String neighbor : neighbors) {
                    if (neighbor == null) continue;
                    hasNeighbors = true;
                    if (!visited.contains(neighbor)) {
                        queue.add(neighbor);
                        visited.add(neighbor);
                        result.append(neighbor).append(" (agregado) ");
                    } else {
                        result.append(neighbor).append(" (ya visitado) ");
                    }
                }
            }

            if (!hasNeighbors) {
                result.append("ninguno");
            }
            result.append("\n\n");
        }

        result.append("Orden de recorrido completo: ");
        for (int i = 0; i < traversal.getSize(); i++) {
            result.append(traversal.get(i));
            if (i < traversal.getSize() - 1) result.append(" → ");
        }
        result.append("\n");

        return result.toString();
    }
    
    public Path shortestPath(String from, String to) {
        if (!hasNode(from) || !hasNode(to)) {
            return new Path(-1, new StringList());
        }
        if (from.equals(to)) {
            StringList path = new StringList();
            path.add(from);
            return new Path(0, path);
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
                return new Path(distance[getNodeIndex(to)], path);
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
        return new Path(-1, new StringList());
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

    /**
     * Algoritmo de Dijkstra para encontrar la ruta más corta entre dos nodos.
     * Usa una estrategia greedy: siempre explora el nodo no visitado con menor distancia.
     * Mantiene un registro de distancias acumuladas y nodos previos para reconstruir la ruta.
     * Complejidad: O(V²) donde V = cantidad de vértices (sin heap de prioridad)
     */
    public Path calculateDijkstraRoute(String start, String end) {
        Path result = new Path();
        StringBuilder detail = new StringBuilder();

        if (!hasNode(start) || !hasNode(end)) {
            result.algorithmDetail = "Node not found in map";
            return result;
        }

        detail.append("═══════════════════════════════════════\n");
        detail.append("     ALGORITMO DIJKSTRA - BÚSQUEDA DE RUTA ÓPTIMA\n");
        detail.append("═══════════════════════════════════════\n\n");
        detail.append("Origen: ").append(start).append("  →  Destino: ").append(end).append("\n\n");

        int total = nodes.getSize();
        int[] distance = new int[total];
        boolean[] visited = new boolean[total];
        String[] previous = new String[total];

        for (int i = 0; i < total; i++) {
            distance[i] = Integer.MAX_VALUE;
            visited[i] = false;
            previous[i] = null;
        }

        int startIndex = getNodeIndex(start);
        distance[startIndex] = 0;

        detail.append("PASO 1: Inicializar\n");
        detail.append("────────────────────\n");
        detail.append("  • Distancia[").append(start).append("] = 0\n");
        detail.append("  • Resto de nodos = ∞\n\n");

        detail.append("PASO 2: Procesar nodos\n");
        detail.append("────────────────────\n");
        
        int step = 1;
        for (int count = 0; count < total; count++) {
            int minDist = Integer.MAX_VALUE;
            int minIndex = -1;

            for (int i = 0; i < total; i++) {
                if (!visited[i] && distance[i] < minDist) {
                    minDist = distance[i];
                    minIndex = i;
                }
            }

            if (minIndex == -1) break;

            String currentNode = getNode(minIndex).getName();
            visited[minIndex] = true;

            detail.append("  ").append(step++).append(". Nodo: ").append(currentNode)
                   .append(" (distancia: ").append(distance[minIndex]).append(")\n");

            if (currentNode.equals(end)) {
                detail.append("     DESTINO ALCANZADO!\n\n");
                break;
            }

            GraphNode currentGraphNode = getNode(currentNode);
            EdgeList edges = currentGraphNode.getEdges();
            for (int i = 0; i < edges.getSize(); i++) {
                Edge edge = (Edge) edges.get(i);
                if (edge == null) continue;

                String neighbor = edge.getTo();
                int neighborIndex = getNodeIndex(neighbor);
                if (neighborIndex < 0 || visited[neighborIndex]) continue;

                int newDist = distance[minIndex] + edge.getWeight();
                int currentDist = distance[neighborIndex];

                if (newDist < currentDist) {
                    distance[neighborIndex] = newDist;
                    previous[neighborIndex] = currentNode;
                }
            }
        }

        detail.append("\nPASO 3: Reconstruir ruta\n");
        detail.append("────────────────────\n");
        int endIndex = getNodeIndex(end);
        if (endIndex >= 0 && distance[endIndex] != Integer.MAX_VALUE) {
            StringList pathList = new StringList();
            String current = end;
            while (current != null) {
                pathList.addFirst(current);
                current = previous[getNodeIndex(current)];
            }

            Path pathResult = new Path(distance[endIndex], pathList);

            detail.append("  Ruta óptima: ");
            for (int i = 0; i < pathList.getSize(); i++) {
                detail.append(pathList.get(i));
                if (i < pathList.getSize() - 1) detail.append(" → ");
            }
            detail.append("\n  Distancia total: ").append(distance[endIndex]).append(" unidades\n");
            detail.append("\n═══════════════════════════════════════\n");
            pathResult.algorithmDetail = detail.toString();
            return pathResult;
        } else {
            detail.append("  No se encontró un camino válido\n");
            detail.append("═══════════════════════════════════════\n");
            result.algorithmDetail = detail.toString();
            return result;
        }
    }

    public Map<String, List<Edge>> getEdgeMap() {
        Map<String, List<Edge>> map = new HashMap<>();
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
            map.put(node.getName(), edges);
        }
        return map;
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
