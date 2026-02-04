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

    public String bfs(String inicio) {
        StringBuilder resultado = new StringBuilder();
        resultado.append("=== EXPLORACIÓN BFS (Breadth-First Search) ===\n");
        resultado.append("Inicio: ").append(inicio).append("\n\n");

        if (!hasNode(inicio)) {
            resultado.append("Error: Nodo no encontrado\n");
            return resultado.toString();
        }

        StringList cola = new StringList();
        StringList visitado = new StringList();
        StringList recorrido = new StringList();

        cola.add(inicio);
        visitado.add(inicio);

        resultado.append("Paso a paso:\n");
        int paso = 1;
        int colaIndex = 0;

        while (colaIndex < cola.getSize()) {
            String actual = cola.get(colaIndex);
            colaIndex++;
            recorrido.add(actual);

            resultado.append(paso++).append(". Visitando: ").append(actual).append("\n");
            resultado.append("   Vecinos: ");

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
                        resultado.append(vecino).append(" (agregado) ");
                    } else {
                        resultado.append(vecino).append(" (ya visitado) ");
                    }
                }
            }

            if (!tieneVecinos) {
                resultado.append("ninguno");
            }
            resultado.append("\n\n");
        }

        resultado.append("Orden de recorrido completo: ");
        for (int i = 0; i < recorrido.getSize(); i++) {
            resultado.append(recorrido.get(i));
            if (i < recorrido.getSize() - 1) resultado.append(" → ");
        }
        resultado.append("\n");

        return resultado.toString();
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

    public PathResult calcularRutaDijkstra(String inicio, String fin) {
        PathResult resultado = new PathResult();
        StringBuilder detalle = new StringBuilder();

        if (!hasNode(inicio) || !hasNode(fin)) {
            resultado.detalleAlgoritmo = "Nodo no encontrado en el mapa";
            return resultado;
        }

        detalle.append("=== ALGORITMO DIJKSTRA ===\n");
        detalle.append("Inicio: ").append(inicio).append(", Destino: ").append(fin).append("\n\n");

        int total = nodes.getSize();
        int[] distancia = new int[total];
        boolean[] visitado = new boolean[total];
        String[] previos = new String[total];

        for (int i = 0; i < total; i++) {
            distancia[i] = Integer.MAX_VALUE;
            visitado[i] = false;
            previos[i] = null;
        }

        int inicioIndex = getNodeIndex(inicio);
        distancia[inicioIndex] = 0;

        detalle.append("1. Inicialización:\n");
        detalle.append("   - Distancia[").append(inicio).append("] = 0\n");
        detalle.append("   - Todos los demás nodos = INFINITO\n\n");

        int paso = 2;
        for (int count = 0; count < total; count++) {
            int minDist = Integer.MAX_VALUE;
            int minIndex = -1;

            for (int i = 0; i < total; i++) {
                if (!visitado[i] && distancia[i] < minDist) {
                    minDist = distancia[i];
                    minIndex = i;
                }
            }

            if (minIndex == -1) break;

            String nodoActual = getNode(minIndex).getName();
            visitado[minIndex] = true;

            detalle.append(paso++).append(". Procesando nodo '").append(nodoActual)
                   .append("' (distancia: ").append(distancia[minIndex]).append(")\n");

            if (nodoActual.equals(fin)) {
                detalle.append("   ¡Destino alcanzado!\n\n");
                break;
            }

            GraphNode actualNode = getNode(nodoActual);
            EdgeList edges = actualNode.getEdges();
            for (int i = 0; i < edges.getSize(); i++) {
                Edge edge = (Edge) edges.get(i);
                if (edge == null) continue;

                String vecino = edge.getTo();
                int vecinoIndex = getNodeIndex(vecino);
                if (vecinoIndex < 0 || visitado[vecinoIndex]) continue;

                int nuevaDist = distancia[minIndex] + edge.getWeight();
                int distActual = distancia[vecinoIndex];

                detalle.append("   - Vecino '").append(vecino)
                       .append("': distancia actual=")
                       .append(distActual == Integer.MAX_VALUE ? "INF" : distActual)
                       .append(", nueva=").append(nuevaDist);

                if (nuevaDist < distActual) {
                    distancia[vecinoIndex] = nuevaDist;
                    previos[vecinoIndex] = nodoActual;
                    detalle.append(" ✓ ACTUALIZADO\n");
                } else {
                    detalle.append(" - no mejora\n");
                }
            }
            detalle.append("\n");
        }

        detalle.append("Reconstruyendo camino óptimo:\n");
        int finIndex = getNodeIndex(fin);
        if (finIndex >= 0 && distancia[finIndex] != Integer.MAX_VALUE) {
            StringList camino = new StringList();
            String actual = fin;
            while (actual != null) {
                camino.addFirst(actual);
                actual = previos[getNodeIndex(actual)];
            }

            resultado.camino = camino;
            resultado.distanciaTotal = distancia[finIndex];

            detalle.append("Camino: ");
            for (int i = 0; i < camino.getSize(); i++) {
                detalle.append(camino.get(i));
                if (i < camino.getSize() - 1) detalle.append(" → ");
            }
            detalle.append("\nDistancia total: ").append(resultado.distanciaTotal).append("\n");
        } else {
            detalle.append("No se encontró un camino válido\n");
        }

        resultado.detalleAlgoritmo = detalle.toString();
        return resultado;
    }

    public Map<String, List<Edge>> obtenerMapaAristas() {
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
