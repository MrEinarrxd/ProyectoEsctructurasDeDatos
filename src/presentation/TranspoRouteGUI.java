package presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import business.GuiController;
import domain.Servicio;
import domain.Solicitud;
import domain.Vehiculo;

public class TranspoRouteGUI extends JFrame {
    private final GuiController controller;
    private GraphPanel graphPanel;
    private JTextArea logArea;

    public TranspoRouteGUI(GuiController controller) {
        this.controller = controller;

        setTitle("TranspoRoute - Sistema de Transporte");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Nueva Solicitud", crearPanelSolicitud());
        tabbedPane.addTab("Procesar", crearPanelProcesar());

        graphPanel = new GraphPanel(controller.getMapa());
        tabbedPane.addTab("Mapa", graphPanel);

        tabbedPane.addTab("Reportes", crearPanelReportes());
        tabbedPane.addTab("Historial", crearPanelHistorial());

        logArea = new JTextArea(10, 60);
        logArea.setEditable(false);
        JScrollPane scrollLog = new JScrollPane(logArea);

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
        add(scrollLog, BorderLayout.SOUTH);
    }

    private JPanel crearPanelSolicitud() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField clienteField = new JTextField();
        JTextField origenField = new JTextField();
        JTextField destinoField = new JTextField();
        JComboBox<String> prioridadCombo = new JComboBox<>(new String[] {
                "1 - Baja", "2 - Media", "3 - Alta", "4 - Emergencia" });

        JButton registrarBtn = new JButton("Registrar Solicitud");

        panel.add(new JLabel("Cliente:"));
        panel.add(clienteField);
        panel.add(new JLabel("Origen:"));
        panel.add(origenField);
        panel.add(new JLabel("Destino:"));
        panel.add(destinoField);
        panel.add(new JLabel("Prioridad:"));
        panel.add(prioridadCombo);
        panel.add(new JLabel(""));
        panel.add(registrarBtn);

        registrarBtn.addActionListener(e -> {
            String cliente = clienteField.getText();
            String origen = origenField.getText();
            String destino = destinoField.getText();
            int prioridad = prioridadCombo.getSelectedIndex() + 1;

            if (cliente.isEmpty() || origen.isEmpty() || destino.isEmpty()) {
                logArea.append("Error: Complete todos los campos\n");
                return;
            }

            Solicitud solicitud = controller.registrarSolicitud(cliente, origen, destino, prioridad);
            logArea.append("Solicitud #" + solicitud.id + " registrada\n");

            clienteField.setText("");
            origenField.setText("");
            destinoField.setText("");
        });

        return panel;
    }

    private JPanel crearPanelProcesar() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton procesarBtn = new JButton("Procesar Siguiente Solicitud");
        JTextArea resultadoArea = new JTextArea(30, 70);
        resultadoArea.setEditable(false);
        resultadoArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));

        panel.add(procesarBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultadoArea), BorderLayout.CENTER);

        procesarBtn.addActionListener(e -> {
            Servicio servicio = controller.procesarSiguienteServicio();
            if (servicio == null) {
                resultadoArea.append("No hay solicitudes pendientes\n");
            } else {
                resultadoArea.setText(""); // Limpiar área
                resultadoArea.append("╔════════════════════════════════════════════════════════════════╗\n");
                resultadoArea.append("║  SERVICIO #" + servicio.id + " COMPLETADO\n");
                resultadoArea.append("╚════════════════════════════════════════════════════════════════╝\n\n");
                
                resultadoArea.append("📋 INFORMACIÓN GENERAL:\n");
                resultadoArea.append("   Cliente: " + servicio.solicitud.cliente + "\n");
                resultadoArea.append("   Vehículo: " + servicio.vehiculo.id + " (Zona: " + servicio.vehiculo.zona + ")\n");
                resultadoArea.append("   Origen: " + servicio.solicitud.origen + "\n");
                resultadoArea.append("   Destino: " + servicio.solicitud.destino + "\n");
                resultadoArea.append("   Costo Total: $" + String.format("%.2f", servicio.costo) + "\n\n");
                
                resultadoArea.append("🚗 RUTA DEL VEHÍCULO AL CLIENTE:\n");
                resultadoArea.append("   " + (servicio.rutaVehiculoCliente != null && !servicio.rutaVehiculoCliente.isEmpty() 
                    ? servicio.rutaVehiculoCliente 
                    : "Vehículo ya en ubicación del cliente") + "\n\n");
                
                resultadoArea.append("👤 RUTA DEL CLIENTE AL DESTINO:\n");
                resultadoArea.append("   " + (servicio.rutaClienteDestino != null && !servicio.rutaClienteDestino.isEmpty() 
                    ? servicio.rutaClienteDestino 
                    : "No disponible") + "\n\n");
                
                if (servicio.algoritmoDetalle != null && !servicio.algoritmoDetalle.isEmpty()) {
                    resultadoArea.append("═══════════════════════════════════════════════════════════════\n");
                    resultadoArea.append("🔍 DETALLE DEL ALGORITMO DE ENRUTAMIENTO\n");
                    resultadoArea.append("═══════════════════════════════════════════════════════════════\n\n");
                    resultadoArea.append(servicio.algoritmoDetalle);
                }
                
                // Posicionar el scroll al inicio
                resultadoArea.setCaretPosition(0);
            }
        });

        return panel;
    }

    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea reporteArea = new JTextArea(20, 60);
        reporteArea.setEditable(false);

        JPanel botonesPanel = new JPanel();
        JButton reporte1Btn = new JButton("Orden Burbuja (servicios)");
        JButton reporte2Btn = new JButton("Orden QuickSort (servicios)");

        botonesPanel.add(reporte1Btn);
        botonesPanel.add(reporte2Btn);

        reporte1Btn.addActionListener(e -> {
            List<Vehiculo> vehiculos = controller.obtenerVehiculosOrdenadosBurbuja();
            reporteArea.setText("=== VEHICULOS ORDENADOS (BURBUJA) ===\n");
            for (Vehiculo v : vehiculos) {
                reporteArea.append(v + "\n");
            }
        });

        reporte2Btn.addActionListener(e -> {
            List<Vehiculo> vehiculos = controller.obtenerVehiculosOrdenadosQuickSort();
            reporteArea.setText("=== VEHICULOS ORDENADOS (QUICKSORT) ===\n");
            for (Vehiculo v : vehiculos) {
                reporteArea.append(v + "\n");
            }
        });

        panel.add(botonesPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(reporteArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea historialArea = new JTextArea(20, 60);
        historialArea.setEditable(false);

        JButton mostrarBtn = new JButton("Mostrar Historial");

        mostrarBtn.addActionListener(e -> {
            historialArea.setText("");
            historialArea.append("=== HISTORIAL DE ACCIONES ===\n");
            historialArea.append("(Historial disponible en consola)\n");
        });

        panel.add(mostrarBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(historialArea), BorderLayout.CENTER);

        return panel;
    }
}
