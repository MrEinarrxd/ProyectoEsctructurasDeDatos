package presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Date;
import domain.List.StringList;
import domain.List.ServiceList;
import domain.List.VehicleList;

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
import domain.Service;

public class TranspoRouteGUI extends JFrame {
    private final GuiController controller;
    private GraphPanel graphPanel;
    private JTextArea historialArea;
    private JTextArea reporteArea;
    private JLabel reporteUpdateLabel;
    private int reporteUpdateCount = 0;

    public TranspoRouteGUI(GuiController controller) {
        this.controller = controller;

        setTitle("TranspoRoute - Sistema de Transporte");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Nueva Solicitud", crearPanelSolicitud());
        tabbedPane.addTab("Procesar", crearPanelProcesar());

        graphPanel = new GraphPanel(controller.getMapa());
        JScrollPane mapScroll = new JScrollPane(graphPanel, 
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tabbedPane.addTab("Mapa", mapScroll);
    
        tabbedPane.addTab("Exploración BFS", crearPanelBFS());

        tabbedPane.addTab("Reportes", crearPanelReportes());
        tabbedPane.addTab("Historial", crearPanelHistorial());
        tabbedPane.addTab("Guardado", crearPanelPersistencia());

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel crearPanelSolicitud() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField clienteField = new JTextField();
        JComboBox<String> origenCombo = new JComboBox<>();
        JComboBox<String> destinoCombo = new JComboBox<>();
        JComboBox<String> prioridadCombo = new JComboBox<>(new String[] {
                "1 - Baja", "2 - Media", "3 - Alta", "4 - Emergencia" });

        JButton registrarBtn = new JButton("Registrar Solicitud");

        // Cargar nodos disponibles en los combos
        StringList nodos = controller.obtenerNodosDisponibles();
        for (int i = 0; i < nodos.getSize(); i++) {
            String nodo = nodos.get(i);
            origenCombo.addItem(nodo);
            destinoCombo.addItem(nodo);
        }

        panel.add(new JLabel("Cliente:"));
        panel.add(clienteField);
        panel.add(new JLabel("Origen:"));
        panel.add(origenCombo);
        panel.add(new JLabel("Destino:"));
        panel.add(destinoCombo);
        panel.add(new JLabel("Prioridad:"));
        panel.add(prioridadCombo);
        panel.add(new JLabel(""));
        panel.add(registrarBtn);

        registrarBtn.addActionListener(e -> {
            String cliente = clienteField.getText();
            String origen = (String) origenCombo.getSelectedItem();
            String destino = (String) destinoCombo.getSelectedItem();
            int prioridad = prioridadCombo.getSelectedIndex() + 1;

            if (cliente.isEmpty() || origen == null || destino == null) {
                return;
            }

            if (origen.equals(destino)) {
                return;
            }

            controller.registrarSolicitud(cliente, origen, destino, prioridad);
            agregarAlHistorial("Solicitud registrada - Cliente: " + cliente + ", " + origen + " -> " + destino + ", Prioridad: " + prioridad);

            clienteField.setText("");
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
            Service servicio = controller.procesarSiguienteServicio();
            if (servicio == null) {
                resultadoArea.setText("No hay solicitudes pendientes\n");
                agregarAlHistorial("Intento de procesar solicitud - Sin solicitudes pendientes");
            } else {
                resultadoArea.setText(""); // Limpiar área
                resultadoArea.append("=====================================================\n");
                resultadoArea.append("SERVICIO #" + servicio.id + " COMPLETADO\n");
                resultadoArea.append("=====================================================\n\n");

                resultadoArea.append("INFORMACIÓN GENERAL:\n");
                resultadoArea.append("  Cliente: " + servicio.request.getClientName() + "\n");
                resultadoArea.append("  Vehículo: " + servicio.vehicle.getId() + " (Zona: " + servicio.vehicle.getCurrentZone() + ")\n");
                resultadoArea.append("  Ruta: " + servicio.request.getOrigin() + " -> " + servicio.request.getDestination() + "\n");
                resultadoArea.append("  Costo Total: $" + servicio.cost + "\n\n");

                resultadoArea.append("RUTA DEL VEHÍCULO AL CLIENTE:\n");
                String rutaVehiculo = servicio.vehicleToClientRoute != null && !servicio.vehicleToClientRoute.isEmpty() 
                    ? servicio.vehicleToClientRoute 
                    : "Vehículo ya en ubicación";
                resultadoArea.append("  " + rutaVehiculo + "\n\n");

                resultadoArea.append("RUTA DEL CLIENTE AL DESTINO:\n");
                String rutaCliente = servicio.clientToDestinationRoute != null && !servicio.clientToDestinationRoute.isEmpty() 
                    ? servicio.clientToDestinationRoute 
                    : "No disponible";
                resultadoArea.append("  " + rutaCliente + "\n\n");

                if (servicio.algorithmDetail != null && !servicio.algorithmDetail.isEmpty()) {
                    resultadoArea.append("=====================================================\n");
                    resultadoArea.append("DETALLE DEL ALGORITMO\n");
                    resultadoArea.append("=====================================================\n\n");
                    resultadoArea.append(servicio.algorithmDetail);
                }

                agregarAlHistorial("Solicitud procesada - Servicio #" + servicio.id + " para " + servicio.request.getClientName());

                // Posicionar el scroll al inicio
                resultadoArea.setCaretPosition(0);
            }
        });

        return panel;
    }

    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout());

        reporteArea = new JTextArea(20, 60);
        reporteArea.setEditable(false);

        JPanel topPanel = new JPanel(new BorderLayout());
        reporteUpdateLabel = new JLabel("Actualizaciones del reporte: 0");
        topPanel.add(reporteUpdateLabel, BorderLayout.WEST);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(reporteArea), BorderLayout.CENTER);

        actualizarReporte();
        return panel;
    }

    private JPanel crearPanelHistorial() {
        JPanel panel = new JPanel(new BorderLayout());

        historialArea = new JTextArea(20, 60);
        historialArea.setEditable(false);
        historialArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 11));

        JPanel buttonPanel = new JPanel();

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(historialArea), BorderLayout.CENTER);

        inicializarHistorial();

        return panel;
    }

    private void inicializarHistorial() {
        historialArea.setText("");
        historialArea.append("HISTORIAL DE ACCIONES DEL SISTEMA\n");
        historialArea.append("═══════════════════════════════════════\n\n");
    }

    private void agregarAlHistorial(String evento) {
        if (historialArea != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss");
            String timestamp = sdf.format(new Date());
            historialArea.append("[" + timestamp + "] " + evento + "\n");
            historialArea.setCaretPosition(historialArea.getDocument().getLength());
        }
        actualizarReporte();
    }

    private void actualizarReporte() {
        if (reporteArea == null) {
            return;
        }

        reporteArea.setText("=== ÁRBOL DE TARIFAS ===\n\n");
        reporteArea.append("Estructura de precios del sistema:\n");
        reporteArea.append("  • Tarifa Básica: $10.00 por unidad de distancia\n");
        reporteArea.append("  • Tarifa Premium: $15.00 por unidad de distancia\n");
        reporteArea.append("  • Tarifa VIP: $25.00 por unidad de distancia\n\n");

        VehicleList vehiculos = controller.obtenerVehiculosOrdenadosQuickSort();
        reporteArea.append("=== VEHICULOS ORDENADOS (QUICKSORT) ===\n");
        for (int i = 0; i < vehiculos.getSize(); i++) {
            reporteArea.append(vehiculos.get(i) + "\n");
        }

        reporteArea.append("\n");
        String reporte = controller.obtenerColasReporte();
        reporteArea.append(reporte);

        reporteArea.append("\n=== SERVICIOS COMPLETADOS ===\n");
        ServiceList servicios = controller.obtenerServiciosCompletados();
        if (servicios.isEmpty()) {
            reporteArea.append("[Sin servicios completados]\n");
        } else {
            for (int i = 0; i < servicios.getSize(); i++) {
                Service servicio = servicios.get(i);
                reporteArea.append("#" + servicio.id + " | Cliente: " + servicio.request.getClientName());
                reporteArea.append(" | Ruta: " + servicio.request.getOrigin() + " -> " + servicio.request.getDestination());
                reporteArea.append(" | Vehículo: " + servicio.vehicle.getId());
                reporteArea.append(" | Costo: $" + servicio.cost + "\n");
            }
        }

        reporteUpdateCount++;
        if (reporteUpdateLabel != null) {
            reporteUpdateLabel.setText("Actualizaciones del reporte: " + reporteUpdateCount);
        }
    }

    private JPanel crearPanelBFS() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel();
        JLabel label = new JLabel("Nodo Inicial:");
        JComboBox<String> nodoCombo = new JComboBox<>();

        // Llenar combo con nodos disponibles
        StringList nodos = controller.obtenerNodosDisponibles();
        for (int i = 0; i < nodos.getSize(); i++) {
            nodoCombo.addItem(nodos.get(i));
        }

        JButton explorarBtn = new JButton("Explorar con BFS");

        topPanel.add(label);
        topPanel.add(nodoCombo);
        topPanel.add(explorarBtn);

        JTextArea resultadoArea = new JTextArea(25, 70);
        resultadoArea.setEditable(false);
        resultadoArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));

        explorarBtn.addActionListener(e -> {
            String nodoInicio = (String) nodoCombo.getSelectedItem();
            if (nodoInicio != null) {
                String resultado = controller.explorarMapaBFS(nodoInicio);
                resultadoArea.setText(resultado);
                agregarAlHistorial("Búsqueda BFS ejecutada desde nodo: " + nodoInicio);
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultadoArea), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelPersistencia() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel botonesPanel = new JPanel();
        JButton guardarBtn = new JButton("💾 Guardar Datos");

        botonesPanel.add(guardarBtn);

        JTextArea infoArea = new JTextArea(20, 60);
        infoArea.setEditable(false);
        infoArea.setText("SISTEMA DE GUARDADO\n\n");
        infoArea.append("DATOS DEL SISTEMA:\n");
        infoArea.append("  • Vehículos y su estado\n");
        infoArea.append("  • Servicios realizados\n\n");
        infoArea.append("HISTORIAL:\n");
        infoArea.append("  • Solicitudes agregadas\n");
        infoArea.append("  • Solicitudes procesadas\n");
        infoArea.append("  • Algoritmos de búsqueda ejecutados\n");
        infoArea.append("  • Eventos del sistema\n\n");
        infoArea.append("ARCHIVOS GENERADOS:\n");
        infoArea.append("  - vehiculos.txt\n");
        infoArea.append("  - servicios.txt\n");
        infoArea.append("  - historial.txt\n");

        guardarBtn.addActionListener(e -> {
            try {
                controller.guardarDatos();
                infoArea.append("\n✓ Datos guardados exitosamente\n");
            } catch (Exception ex) {
                infoArea.append("\n✗ Error al guardar: " + ex.getMessage() + "\n");
            }
        });

        panel.add(botonesPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        return panel;
    }
}
