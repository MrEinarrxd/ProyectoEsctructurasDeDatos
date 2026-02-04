package presentation;

import java.awt.*;
import java.util.List;
import java.util.Map;

import javax.swing.*;

import domain.Utils;

public class GraphPanel extends JPanel {
    private Utils.Grafo grafo;
    private Map<String, Point> posiciones;

    public GraphPanel(Utils.Grafo grafo) {
        this.grafo = grafo;
        this.posiciones = new java.util.HashMap<>();
        calcularPosiciones();
        setPreferredSize(new Dimension(900, 600));
    }

    private void calcularPosiciones() {
        // Distribución no-convexa (cóncava)
        // Noroeste     Norte
        //    \          |
        //     Oeste - Centro - Este
        //    /          |
        // Suroeste     Sur
        
        posiciones.put("Centro", new Point(450, 300));
        posiciones.put("Norte", new Point(450, 80));
        posiciones.put("Sur", new Point(450, 520));
        posiciones.put("Este", new Point(750, 300));
        posiciones.put("Oeste", new Point(150, 300));
        posiciones.put("Noroeste", new Point(100, 100));
        posiciones.put("Suroeste", new Point(100, 500));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.GRAY);
        Map<String, List<Utils.Grafo.Arista>> mapa = grafo.obtenerMapaAristas();
        java.util.Set<String> aristasDibujadas = new java.util.HashSet<>();
        java.util.Map<String, Integer> distancias = new java.util.HashMap<>();

        for (String origen : mapa.keySet()) {
            Point p1 = posiciones.get(origen);
            if (p1 == null) continue;

            for (Utils.Grafo.Arista arista : mapa.get(origen)) {
                Point p2 = posiciones.get(arista.destino);
                if (p2 == null) continue;

                // Evitar dibujar aristas duplicadas (bidireccionales)
                String clave = origen.compareTo(arista.destino) < 0 
                    ? origen + "||" + arista.destino 
                    : arista.destino + "||" + origen;
                
                if (aristasDibujadas.contains(clave)) continue;
                aristasDibujadas.add(clave);
                distancias.put(clave, arista.distancia);

                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        // Dibujar etiquetas de distancias con desplazamiento
        for (Map.Entry<String, Integer> entry : distancias.entrySet()) {
            String clave = entry.getKey();
            String[] partes = clave.split("\\|\\|");
            if (partes.length == 2) {
                Point p1 = posiciones.get(partes[0]);
                Point p2 = posiciones.get(partes[1]);
                
                if (p1 != null && p2 != null) {
                    int midX = (p1.x + p2.x) / 2;
                    int midY = (p1.y + p2.y) / 2;
                    
                    // Calcular vector perpendicular para desplazar la etiqueta
                    int dx = p2.x - p1.x;
                    int dy = p2.y - p1.y;
                    int length = (int) Math.sqrt(dx * dx + dy * dy);
                    
                    // Vector perpendicular normalizado
                    int perpX = 0, perpY = 0;
                    if (length > 0) {
                        perpX = (-dy * 10) / length;  // Desplazamiento perpendicular
                        perpY = (dx * 10) / length;
                    }
                    
                    g2d.setColor(Color.BLACK);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    g2d.drawString(String.valueOf(entry.getValue()), midX + perpX, midY + perpY);
                }
            }
        }

        // Dibujar nodos con nombres completos
        for (Map.Entry<String, Point> entry : posiciones.entrySet()) {
            Point p = entry.getValue();
            int radius = 35;
            g2d.setColor(Color.BLUE);
            g2d.fillOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
            g2d.setColor(Color.WHITE);
            
            String nombre = entry.getKey();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(nombre);
            int textHeight = fm.getAscent();
            
            // Ajustar posición del texto para centrarlo
            g2d.drawString(nombre, p.x - textWidth / 2, p.y + textHeight / 4);
        }
    }
}
