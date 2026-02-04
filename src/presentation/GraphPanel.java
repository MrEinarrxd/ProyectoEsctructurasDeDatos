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
        setPreferredSize(new Dimension(600, 400));
    }

    private void calcularPosiciones() {
        posiciones.put("Centro", new Point(300, 200));
        posiciones.put("Norte", new Point(300, 50));
        posiciones.put("Sur", new Point(300, 350));
        posiciones.put("Este", new Point(500, 200));
        posiciones.put("Oeste", new Point(100, 200));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                           RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.GRAY);
        Map<String, List<Utils.Grafo.Arista>> mapa = grafo.obtenerMapaAristas();

        for (String origen : mapa.keySet()) {
            Point p1 = posiciones.get(origen);
            if (p1 == null) continue;

            for (Utils.Grafo.Arista arista : mapa.get(origen)) {
                Point p2 = posiciones.get(arista.destino);
                if (p2 == null) continue;

                g2d.drawLine(p1.x, p1.y, p2.x, p2.y);

                int midX = (p1.x + p2.x) / 2;
                int midY = (p1.y + p2.y) / 2;
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.valueOf(arista.distancia), midX, midY);
                g2d.setColor(Color.GRAY);
            }
        }

        for (Map.Entry<String, Point> entry : posiciones.entrySet()) {
            Point p = entry.getValue();
            g2d.setColor(Color.BLUE);
            g2d.fillOval(p.x - 20, p.y - 20, 40, 40);
            g2d.setColor(Color.WHITE);
            g2d.drawString(entry.getKey(), p.x - 15, p.y + 5);
        }
    }
}
