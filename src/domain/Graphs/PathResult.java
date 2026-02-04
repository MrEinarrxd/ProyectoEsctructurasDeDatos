package domain.Graphs;

import domain.List.StringList;

public class PathResult {
    public final int distance;
    public final StringList path;
    
    public PathResult(int distance, StringList path) {
        this.distance = distance;
        this.path = path;
    }
    
    @Override
    public String toString() {
        if (distance < 0) {
            return "No hay ruta disponible";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Distancia: ").append(distance).append(" | Ruta: ");
        
        for (int i = 0; i < path.getSize(); i++) {
            sb.append(path.get(i));
            if (i < path.getSize() - 1) {
                sb.append(" -> ");
            }
        }
        
        return sb.toString();
    }
}
