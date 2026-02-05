package domain.Graphs;

import domain.List.StringList;

public class Path {
    public final int distance;
    public final StringList path;
    
    // información adicional para resultados detallados
    public StringList camino;
    public int distanciaTotal;
    public String detalleAlgoritmo;
    
    // Constructor original
    public Path(int distance, StringList path) {
        this.distance = distance;
        this.path = path;
        this.camino = new StringList();
        this.distanciaTotal = distance;
        this.detalleAlgoritmo = "";
        
        // Copiar el camino original al nuevo campo para detalles
        for (int i = 0; i < path.getSize(); i++) {
            this.camino.add(path.get(i));
        }
    }
    
    // Constructor vacío
    public Path() {
        this.distance = 0;
        this.path = new StringList();
        this.camino = new StringList();
        this.distanciaTotal = 0;
        this.detalleAlgoritmo = "";
    }
    
    @Override
    public String toString() {
        if (distance < 0) {
            return "No hay ruta disponible";
        }
        
        String result = "Distancia: " + distance + " | Ruta: ";
        
        for (int i = 0; i < path.getSize(); i++) {
            result += path.get(i);
            if (i < path.getSize() - 1) {
                result += " -> ";
            }
        }
        
        return result;
    }
}
