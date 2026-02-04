package presentation;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.*;

import domain.Graphs.Edge;
import domain.Graphs.Graph;

public class GraphPanel extends JPanel {
    private Graph grafo;
    private Map<String, Point> posiciones;

    public GraphPanel(Graph grafo) {
        this.grafo = grafo;
        this.posiciones = new HashMap<>();
        calcularPosiciones();
        setPreferredSize(new Dimension(900, 600));
    }

    private void calcularPosiciones() {
        Map<String, List<Edge>> mapa = grafo.obtenerMapaAristas();
        
        // Si no hay nodos, no hacer nada
        if (mapa.isEmpty()) {
            return;
        }
        
        // Obtener todos los nodos únicos
        Set<String> nodos = new HashSet<>();
        for (String nodo : mapa.keySet()) {
            nodos.add(nodo);
            for (Edge edge : mapa.get(nodo)) {
                nodos.add(edge.getTo());
            }
        }
        
        // Inicializar posiciones en círculo
        int centerX = 450;
        int centerY = 300;
        int radius = 350;
        
        int totalNodos = nodos.size();
        int index = 0;
        for (String nodo : nodos) {
            double angulo = (2 * Math.PI * index) / totalNodos;
            int x = centerX + (int) (radius * Math.cos(angulo));
            int y = centerY + (int) (radius * Math.sin(angulo));
            posiciones.put(nodo, new Point(x, y));
            index++;
        }
        
        // Aplicar algoritmo force-directed (spring layout) para mejorar la visualización
        aplicarForceDirectedLayout(mapa, nodos, 100);
    }
    
    private void aplicarForceDirectedLayout(Map<String, List<Edge>> mapa, Set<String> nodos, int iteraciones) {
        Map<String, double[]> velocidades = new HashMap<>();
        Map<String, double[]> fuerzas = new HashMap<>();
        
        // Inicializar velocidades en cero
        for (String nodo : nodos) {
            velocidades.put(nodo, new double[]{0, 0});
        }
        
        double k = 200.0;  // Constante de repulsión
        double c = 0.05;  // Factor de amortiguación
        
        for (int iter = 0; iter < iteraciones; iter++) {
            // Reinicializar fuerzas
            for (String nodo : nodos) {
                fuerzas.put(nodo, new double[]{0, 0});
            }
            
            // Repulsión entre nodos (todos se repelen)
            String[] nodosArray = nodos.toArray(new String[0]);
            for (int i = 0; i < nodosArray.length; i++) {
                for (int j = i + 1; j < nodosArray.length; j++) {
                    String n1 = nodosArray[i];
                    String n2 = nodosArray[j];
                    
                    Point p1 = posiciones.get(n1);
                    Point p2 = posiciones.get(n2);
                    
                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    
                    if (dist > 1) {
                        double force = k / dist;
                        double fx = (dx / dist) * force;
                        double fy = (dy / dist) * force;
                        
                        // Repulsión (inversa)
                        fuerzas.get(n1)[0] -= fx;
                        fuerzas.get(n1)[1] -= fy;
                        fuerzas.get(n2)[0] += fx;
                        fuerzas.get(n2)[1] += fy;
                    }
                }
            }
            
            // Atracción por aristas (muelles)
            for (String origen : mapa.keySet()) {
                for (Edge edge : mapa.get(origen)) {
                    String destino = edge.getTo();
                    if (posiciones.containsKey(destino)) {
                        Point p1 = posiciones.get(origen);
                        Point p2 = posiciones.get(destino);
                        
                        double dx = p2.x - p1.x;
                        double dy = p2.y - p1.y;
                        double dist = Math.sqrt(dx * dx + dy * dy);
                        
                        double restLength = 300;
                        if (dist > 0.1) {
                            double force = 0.01 * (dist - restLength);
                            double fx = (dx / dist) * force;
                            double fy = (dy / dist) * force;
                            
                            fuerzas.get(origen)[0] += fx;
                            fuerzas.get(origen)[1] += fy;
                            fuerzas.get(destino)[0] -= fx;
                            fuerzas.get(destino)[1] -= fy;
                        }
                    }
                }
            }
            
            // Actualizar posiciones
            for (String nodo : nodos) {
                double[] vel = velocidades.get(nodo);
                double[] fuerza = fuerzas.get(nodo);
                Point pos = posiciones.get(nodo);
                
                vel[0] = (vel[0] + fuerza[0]) * (1 - c);
                vel[1] = (vel[1] + fuerza[1]) * (1 - c);
                
                int newX = Math.max(50, Math.min(850, (int)(pos.x + vel[0])));
                int newY = Math.max(50, Math.min(550, (int)(pos.y + vel[1])));
                
                posiciones.put(nodo, new Point(newX, newY));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        Map<String, List<Edge>> mapa = grafo.obtenerMapaAristas();
        Set<String> aristasDibujadas = new HashSet<>();
        Map<String, Integer> distancias = new HashMap<>();
        int pesoMaximo = 1;

        // Encontrar el peso máximo para escalar colores
        for (String origen : mapa.keySet()) {
            for (Edge arista : mapa.get(origen)) {
                if (arista.getWeight() > pesoMaximo) {
                    pesoMaximo = arista.getWeight();
                }
            }
        }

        // Dibujar aristas con colores según peso
        for (String origen : mapa.keySet()) {
            Point p1 = posiciones.get(origen);
            if (p1 == null) continue;

            for (Edge arista : mapa.get(origen)) {
                Point p2 = posiciones.get(arista.getTo());
                if (p2 == null) continue;

                String clave = origen.compareTo(arista.getTo()) < 0 
                    ? origen + "||" + arista.getTo() 
                    : arista.getTo() + "||" + origen;
                
                if (aristasDibujadas.contains(clave)) continue;
                aristasDibujadas.add(clave);
                distancias.put(clave, arista.getWeight());

                // Calcular color según peso (gradiente: verde->amarillo->rojo)
                float ratio = (float) arista.getWeight() / pesoMaximo;
                Color color = obtenerColorPorPeso(ratio);
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2 + (int)(ratio * 3))); // Grosor aumenta con peso
                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
            }
        }

        // Dibujar etiquetas de distancias mejoradas
        for (Map.Entry<String, Integer> entry : distancias.entrySet()) {
            String clave = entry.getKey();
            String[] partes = clave.split("\\|\\|");
            if (partes.length == 2) {
                Point p1 = posiciones.get(partes[0]);
                Point p2 = posiciones.get(partes[1]);
                
                if (p1 != null && p2 != null) {
                    int midX = (p1.x + p2.x) / 2;
                    int midY = (p1.y + p2.y) / 2;
                    
                    int dx = p2.x - p1.x;
                    int dy = p2.y - p1.y;
                    int length = (int) Math.sqrt(dx * dx + dy * dy);
                    
                    int perpX = 0, perpY = 0;
                    if (length > 0) {
                        perpX = (-dy * 15) / length;
                        perpY = (dx * 15) / length;
                    }
                    
                    // Fondo blanco para el texto
                    g2d.setColor(new Color(255, 255, 255, 220));
                    String texto = String.valueOf(entry.getValue());
                    FontMetrics fm = g2d.getFontMetrics();
                    int textWidth = fm.stringWidth(texto);
                    int textHeight = fm.getAscent();
                    
                    g2d.fillRect(midX + perpX - textWidth/2 - 3, midY + perpY - textHeight - 2, 
                                textWidth + 6, textHeight + 4);
                    
                    // Texto con color de la arista
                    float ratio = (float) entry.getValue() / pesoMaximo;
                    Color colorArista = obtenerColorPorPeso(ratio);
                    g2d.setColor(colorArista);
                    g2d.setFont(new Font("Arial", Font.BOLD, 13));
                    g2d.drawString(texto, midX + perpX - textWidth/2, midY + perpY);
                }
            }
        }

        // Dibujar nodos
        for (Map.Entry<String, Point> entry : posiciones.entrySet()) {
            Point p = entry.getValue();
            int radius = 35;
            g2d.setColor(Color.BLUE);
            g2d.fillOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(p.x - radius, p.y - radius, radius * 2, radius * 2);
            
            String nombre = entry.getKey();
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(nombre);
            int textHeight = fm.getAscent();
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString(nombre, p.x - textWidth / 2, p.y + textHeight / 4);
        }
    }

    private Color obtenerColorPorPeso(float ratio) {
        // Paleta de colores estética: Azul profundo -> Cian -> Verde -> Amarillo -> Naranja -> Rojo
        if (ratio < 0.2f) {
            // Azul profundo a Cian
            float localRatio = ratio / 0.2f;
            int r = (int) (0 + 0 * localRatio);
            int g = (int) (102 + 153 * localRatio);
            int b = (int) (204 + 51 * localRatio);
            return new Color(r, g, b);
        } else if (ratio < 0.4f) {
            // Cian a Verde
            float localRatio = (ratio - 0.2f) / 0.2f;
            int r = (int) (0 + 0 * localRatio);
            int g = (int) (255 - 55 * localRatio);
            int b = (int) (255 - 155 * localRatio);
            return new Color(r, g, b);
        } else if (ratio < 0.6f) {
            // Verde a Amarillo
            float localRatio = (ratio - 0.4f) / 0.2f;
            int r = (int) (0 + 255 * localRatio);
            int g = (int) (200 + 55 * localRatio);
            int b = (int) (100 - 100 * localRatio);
            return new Color(r, g, b);
        } else if (ratio < 0.8f) {
            // Amarillo a Naranja
            float localRatio = (ratio - 0.6f) / 0.2f;
            int r = (int) (255 + 0 * localRatio);
            int g = (int) (255 - 155 * localRatio);
            int b = (int) (0);
            return new Color(r, g, b);
        } else {
            // Naranja a Rojo oscuro
            float localRatio = (ratio - 0.8f) / 0.2f;
            int r = (int) (255 - 55 * localRatio);
            int g = (int) (100 - 100 * localRatio);
            int b = (int) (0);
            return new Color(r, g, b);
        }
    }
}
