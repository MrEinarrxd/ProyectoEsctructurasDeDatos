package presentation;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.*;

import business.GuiController;
import domain.Graphs.Edge;

public class GraphPanel extends JPanel {
    // Constantes de layout
    private static final int PANEL_WIDTH = 900;
    private static final int PANEL_HEIGHT = 600;
    private static final int CENTER_X = PANEL_WIDTH / 2;
    private static final int CENTER_Y = PANEL_HEIGHT / 2;
    private static final int LAYOUT_RADIUS = 350;
    private static final int FORCE_ITERATIONS = 100;
    private static final double REPULSION_CONSTANT = 200.0;
    private static final double DAMPING_FACTOR = 0.05;
    private static final int NODE_RADIUS = 35;
    private static final int EDGE_LABEL_OFFSET = 15;
    private static final int MIN_POSITION = 50;
    private static final int MAX_POSITION_X = 850;
    private static final int MAX_POSITION_Y = 550;

    private Map<String, Point> posiciones;
    private Map<String, List<Edge>> mapaAristas;
    @SuppressWarnings("unused")
    private GuiController controller;

    public GraphPanel(GuiController controller) {
        this.controller = controller;
        this.posiciones = new HashMap<>();
        this.mapaAristas = controller.getMap().getEdgeMap();
        calcularPosiciones();
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        
    }

    private void calcularPosiciones() {
        // Si no hay nodos, no hacer nada
        if (mapaAristas.isEmpty()) {
            return;
        }
        
        // Obtener todos los nodos únicos
        Set<String> nodos = new HashSet<>();
        for (String nodo : mapaAristas.keySet()) {
            nodos.add(nodo);
            for (Edge edge : mapaAristas.get(nodo)) {
                nodos.add(edge.getTo());
            }
        }
        
        // Inicializar posiciones en círculo
        int totalNodos = nodos.size();
        int index = 0;
        for (String nodo : nodos) {
            double angulo = (2 * Math.PI * index) / totalNodos;
            int x = CENTER_X + (int) (LAYOUT_RADIUS * Math.cos(angulo));
            int y = CENTER_Y + (int) (LAYOUT_RADIUS * Math.sin(angulo));
            posiciones.put(nodo, new Point(x, y));
            index++;
        }
        
        // Aplicar algoritmo force-directed (spring layout)
        aplicarForceDirectedLayout(nodos);
    }
    
    private void aplicarForceDirectedLayout(Set<String> nodos) {
        Map<String, double[]> velocidades = new HashMap<>();
        Map<String, double[]> fuerzas = new HashMap<>();
        
        // Inicializar velocidades en cero
        for (String nodo : nodos) {
            velocidades.put(nodo, new double[]{0, 0});
        }
        
        for (int iter = 0; iter < FORCE_ITERATIONS; iter++) {
            // Reinicializar fuerzas
            for (String nodo : nodos) {
                fuerzas.put(nodo, new double[]{0, 0});
            }
            
            // Repulsión entre nodos
            aplicarRepulsion(nodos, fuerzas);
            
            // Atracción por aristas (muelles)
            aplicarAtraccion(fuerzas);
            
            // Actualizar posiciones
            actualizarPosiciones(nodos, velocidades, fuerzas);
        }
    }

    private void aplicarRepulsion(Set<String> nodos, Map<String, double[]> fuerzas) {
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
                    double force = REPULSION_CONSTANT / dist;
                    double fx = (dx / dist) * force;
                    double fy = (dy / dist) * force;
                    
                    fuerzas.get(n1)[0] -= fx;
                    fuerzas.get(n1)[1] -= fy;
                    fuerzas.get(n2)[0] += fx;
                    fuerzas.get(n2)[1] += fy;
                }
            }
        }
    }

    private void aplicarAtraccion(Map<String, double[]> fuerzas) {
        double restLength = 300;
        for (String origen : mapaAristas.keySet()) {
            for (Edge edge : mapaAristas.get(origen)) {
                String destino = edge.getTo();
                if (posiciones.containsKey(destino)) {
                    Point p1 = posiciones.get(origen);
                    Point p2 = posiciones.get(destino);
                    
                    double dx = p2.x - p1.x;
                    double dy = p2.y - p1.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    
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
    }

    private void actualizarPosiciones(Set<String> nodos, Map<String, double[]> velocidades, Map<String, double[]> fuerzas) {
        for (String nodo : nodos) {
            double[] vel = velocidades.get(nodo);
            double[] fuerza = fuerzas.get(nodo);
            Point pos = posiciones.get(nodo);
            
            vel[0] = (vel[0] + fuerza[0]) * (1 - DAMPING_FACTOR);
            vel[1] = (vel[1] + fuerza[1]) * (1 - DAMPING_FACTOR);
            
            int newX = Math.max(MIN_POSITION, Math.min(MAX_POSITION_X, (int)(pos.x + vel[0])));
            int newY = Math.max(MIN_POSITION, Math.min(MAX_POSITION_Y, (int)(pos.y + vel[1])));
            
            posiciones.put(nodo, new Point(newX, newY));
        }
    }

    private int obtenerPesoMaximo() {
        int max = 1;
        for (List<Edge> edges : mapaAristas.values()) {
            for (Edge edge : edges) {
                max = Math.max(max, edge.getWeight());
            }
        }
        return max;
    }

    private String crearClaveArista(String n1, String n2) {
        return n1.compareTo(n2) < 0 ? n1 + "||" + n2 : n2 + "||" + n1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        Set<String> aristasDibujadas = new HashSet<>();
        Map<String, Integer> distancias = new HashMap<>();
        int pesoMaximo = obtenerPesoMaximo();

        // Dibujar aristas con colores según peso
        for (String origen : mapaAristas.keySet()) {
            Point p1 = posiciones.get(origen);
            if (p1 == null) continue;

            for (Edge arista : mapaAristas.get(origen)) {
                Point p2 = posiciones.get(arista.getTo());
                if (p2 == null) continue;

                String clave = crearClaveArista(origen, arista.getTo());
                
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
                        perpX = (-dy * EDGE_LABEL_OFFSET) / length;
                        perpY = (dx * EDGE_LABEL_OFFSET) / length;
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
            g2d.setColor(Color.BLUE);
            g2d.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            g2d.setColor(Color.WHITE);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            
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
