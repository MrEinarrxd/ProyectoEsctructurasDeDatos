package presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import business.GuiController;
import domain.Service;
import domain.List.StringList;
import domain.List.ServiceList;
import domain.List.VehicleList;

public class TranspoRouteGUI extends JFrame {
    private final GuiController controller;
    private GraphPanel graphPanel;
    private JTextArea historialArea;
    private JTextArea reporteArea;
    private JLabel reporteUpdateLabel;
    private int reporteUpdateCount = 0;

    // Paleta de colores moderna
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color PANEL_BG = new Color(255, 255, 255);
    private static final Color TEXT_AREA_BG = new Color(250, 250, 250);
    private static final Color BORDER_COLOR = new Color(189, 195, 199);
    
    // Fuentes
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font MONO_FONT = new Font("Consolas", Font.PLAIN, 12);

    public TranspoRouteGUI(GuiController controller) {
        this.controller = controller;

        setTitle("TranspoRoute - Sistema de Transporte");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        getContentPane().setBackground(BACKGROUND_COLOR);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(TITLE_FONT);
        tabbedPane.setBackground(PANEL_BG);

        tabbedPane.addTab("Nueva Solicitud", crearPanelSolicitud());
        tabbedPane.addTab("Procesar", crearPanelProcesar());

        graphPanel = new GraphPanel(controller);
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
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel de título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(PANEL_BG);
        JLabel titleLabel = new JLabel("Registro de Nueva Solicitud");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        
        // Panel del formulario
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBackground(PANEL_BG);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JTextField clienteField = new JTextField();
        clienteField.setFont(TEXT_FONT);
        clienteField.setPreferredSize(new Dimension(250, 30));
        
        JComboBox<String> origenCombo = new JComboBox<>();
        origenCombo.setFont(TEXT_FONT);
        origenCombo.setPreferredSize(new Dimension(250, 30));
        
        JComboBox<String> destinoCombo = new JComboBox<>();
        destinoCombo.setFont(TEXT_FONT);
        destinoCombo.setPreferredSize(new Dimension(250, 30));
        
        JComboBox<String> categoriaCombo = new JComboBox<>(new String[] {
                "0 - Económico", "1 - Regular", "2 - VIP", "3 - Emergencia" });
        categoriaCombo.setFont(TEXT_FONT);
        categoriaCombo.setPreferredSize(new Dimension(250, 30));
        categoriaCombo.setSelectedIndex(1);

        // Cargar nodos disponibles en los combos
        StringList nodos = controller.getAvailableNodes();
        for (int i = 0; i < nodos.getSize(); i++) {
            String nodo = nodos.get(i);
            origenCombo.addItem(nodo);
            destinoCombo.addItem(nodo);
        }

        // Etiquetas con estilo
        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(LABEL_FONT);
        JLabel lblOrigen = new JLabel("Origen:");
        lblOrigen.setFont(LABEL_FONT);
        JLabel lblDestino = new JLabel("Destino:");
        lblDestino.setFont(LABEL_FONT);
        JLabel lblCategoria = new JLabel("Categoría Cliente:");
        lblCategoria.setFont(LABEL_FONT);

        formPanel.add(lblCliente);
        formPanel.add(clienteField);
        formPanel.add(lblOrigen);
        formPanel.add(origenCombo);
        formPanel.add(lblDestino);
        formPanel.add(destinoCombo);
        formPanel.add(lblCategoria);
        formPanel.add(categoriaCombo);

        // Panel de botón
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        buttonPanel.setBackground(PANEL_BG);
        
        JButton registrarBtn = crearBotonEstilizado("Registrar Solicitud", PRIMARY_COLOR);
        registrarBtn.setPreferredSize(new Dimension(200, 40));

        buttonPanel.add(registrarBtn);

        registrarBtn.addActionListener(e -> {
            String cliente = clienteField.getText();
            String origen = (String) origenCombo.getSelectedItem();
            String destino = (String) destinoCombo.getSelectedItem();
            int categoria = categoriaCombo.getSelectedIndex();
            int prioridad = categoria;

            if (cliente.isEmpty() || origen == null || destino == null) {
                return;
            }

            if (origen.equals(destino)) {
                return;
            }

            controller.registerRequest(cliente, origen, destino, prioridad, categoria);
            String categoriaNombre = categoriaCombo.getSelectedItem().toString();
            agregarAlHistorial("Solicitud registrada - Cliente: " + cliente + " (" + categoriaNombre + "), " + origen + " -> " + destino);

            clienteField.setText("");
        });

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel crearPanelProcesar() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Panel superior con título y botón
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(PANEL_BG);
        
        JLabel titleLabel = new JLabel("Procesamiento de Solicitudes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JButton procesarBtn = crearBotonEstilizado("Procesar Siguiente Solicitud", SUCCESS_COLOR);
        procesarBtn.setPreferredSize(new Dimension(250, 40));
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(procesarBtn, BorderLayout.EAST);

        // Área de resultado con estilo
        JTextArea resultadoArea = new JTextArea(30, 70);
        resultadoArea.setEditable(false);
        resultadoArea.setFont(MONO_FONT);
        resultadoArea.setBackground(TEXT_AREA_BG);
        resultadoArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        procesarBtn.addActionListener(e -> {
            Service service = controller.processNextService();
            if (service == null) {
                resultadoArea.setText("No hay solicitudes pendientes\n");
                agregarAlHistorial("Intento de procesar solicitud - Sin solicitudes pendientes");
            } else {
                resultadoArea.setText("");
                resultadoArea.append("=====================================================\n");
                resultadoArea.append("SERVICIO #" + service.id + " COMPLETADO\n");
                resultadoArea.append("=====================================================\n\n");

                resultadoArea.append("INFORMACIÓN GENERAL:\n");
                resultadoArea.append("  Cliente: " + service.request.getClientName() + "\n");
                resultadoArea.append("  Vehículo: " + service.vehicle.getId() + " (Zona: " + service.vehicle.getCurrentZone() + ")\n");
                resultadoArea.append("  Ruta: " + service.request.getOrigin() + " -> " + service.request.getDestination() + "\n");
                resultadoArea.append("  Costo Total: $" + service.cost + "\n\n");

                resultadoArea.append("RUTA DEL VEHÍCULO AL CLIENTE:\n");
                String rutaVehiculo = service.vehicleToClientRoute != null && !service.vehicleToClientRoute.isEmpty() 
                    ? service.vehicleToClientRoute 
                    : "Vehículo ya en ubicación";
                resultadoArea.append("  " + rutaVehiculo + "\n\n");

                resultadoArea.append("RUTA DEL CLIENTE AL DESTINO:\n");
                String rutaCliente = service.clientToDestinationRoute != null && !service.clientToDestinationRoute.isEmpty() 
                    ? service.clientToDestinationRoute 
                    : "No disponible";
                resultadoArea.append("  " + rutaCliente + "\n\n");

                if (service.algorithmDetail != null && !service.algorithmDetail.isEmpty()) {
                    resultadoArea.append("=====================================================\n");
                    resultadoArea.append("DETALLE DEL ALGORITMO\n");
                    resultadoArea.append("=====================================================\n\n");
                    resultadoArea.append(service.algorithmDetail);
                }

                String rutaSimplificada = rutaCliente.equals("No disponible") ? "No disponible" : rutaCliente;
                agregarAlHistorial("Solicitud procesada - Servicio #" + service.id + " para " + service.request.getClientName() + " | Ruta: " + rutaSimplificada);

                resultadoArea.setCaretPosition(0);
            }
        });

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel crearPanelReportes() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel superior con título
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PANEL_BG);
        
        JLabel titleLabel = new JLabel("Reportes del Sistema");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        reporteUpdateLabel = new JLabel("Actualizaciones: 0");
        reporteUpdateLabel.setFont(LABEL_FONT);
        reporteUpdateLabel.setForeground(new Color(127, 140, 141));
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(reporteUpdateLabel, BorderLayout.EAST);

        // Área de reporte con estilo
        reporteArea = new JTextArea(20, 60);
        reporteArea.setEditable(false);
        reporteArea.setFont(MONO_FONT);
        reporteArea.setBackground(TEXT_AREA_BG);
        reporteArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(reporteArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        actualizarReporte();
        return mainPanel;
    }

    private JPanel crearPanelHistorial() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(PANEL_BG);
        JLabel titleLabel = new JLabel("Historial de Acciones");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        // Área de historial con estilo
        historialArea = new JTextArea(20, 60);
        historialArea.setEditable(false);
        historialArea.setFont(MONO_FONT);
        historialArea.setBackground(TEXT_AREA_BG);
        historialArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(historialArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        inicializarHistorial();

        return mainPanel;
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
        double basica = controller.getRate("basica");
        double premium = controller.getRate("premium");
        double vip = controller.getRate("vip");
        if (basica == 0.0) basica = 10.0;
        if (premium == 0.0) premium = 15.0;
        if (vip == 0.0) vip = 25.0;
        reporteArea.append("  • Tarifa Básica: $" + String.format("%.2f", basica) + " por unidad de distancia\n");
        reporteArea.append("  • Tarifa Premium: $" + String.format("%.2f", premium) + " por unidad de distancia\n");
        reporteArea.append("  • Tarifa VIP: $" + String.format("%.2f", vip) + " por unidad de distancia\n\n");

        VehicleList vehiculos = controller.getSortedVehiclesQuickSort();
        reporteArea.append("=== VEHICULOS ORDENADOS (QUICKSORT) ===\n");
        for (int i = 0; i < vehiculos.getSize(); i++) {
            reporteArea.append(vehiculos.get(i) + "\n");
        }

        reporteArea.append("\n");
        String reporte = controller.getQueuesReport();
        reporteArea.append(reporte);

        reporteArea.append("\n=== SERVICIOS COMPLETADOS ===\n");
        ServiceList servicios = controller.getCompletedServices();
        if (servicios.isEmpty()) {
            reporteArea.append("[Sin servicios completados]\n");
        } else {
            for (int i = 0; i < servicios.getSize(); i++) {
                Service servicio = servicios.get(i);
                reporteArea.append("\n────────────────────────────────────────\n");
                reporteArea.append("SERVICIO #" + servicio.id + "\n");
                reporteArea.append("────────────────────────────────────────\n");
                reporteArea.append("Cliente: " + servicio.request.getClientName() + "\n");
                reporteArea.append("Ruta: " + servicio.request.getOrigin() + " -> " + servicio.request.getDestination() + "\n");
                reporteArea.append("Vehículo: " + servicio.vehicle.getId() + "\n");
                reporteArea.append("Costo: $" + servicio.cost + "\n\n");
                
                reporteArea.append("RUTA DEL VEHÍCULO AL CLIENTE:\n");
                String rutaVehiculo = servicio.vehicleToClientRoute != null && !servicio.vehicleToClientRoute.isEmpty() 
                    ? servicio.vehicleToClientRoute 
                    : "Vehículo ya en ubicación";
                reporteArea.append(rutaVehiculo + "\n\n");
                
                reporteArea.append("RUTA DEL CLIENTE AL DESTINO:\n");
                String rutaCliente = servicio.clientToDestinationRoute != null && !servicio.clientToDestinationRoute.isEmpty() 
                    ? servicio.clientToDestinationRoute 
                    : "No disponible";
                reporteArea.append(rutaCliente + "\n");
            }
        }

        reporteUpdateCount++;
        if (reporteUpdateLabel != null) {
            reporteUpdateLabel.setText("Actualizaciones: " + reporteUpdateCount);
        }
    }

    private JPanel crearPanelBFS() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel superior con título y controles
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(PANEL_BG);
        
        JLabel titleLabel = new JLabel("Exploración con BFS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsPanel.setBackground(PANEL_BG);
        
        JLabel label = new JLabel("Nodo Inicial:");
        label.setFont(LABEL_FONT);
        
        JComboBox<String> nodoCombo = new JComboBox<>();
        nodoCombo.setFont(TEXT_FONT);
        nodoCombo.setPreferredSize(new Dimension(120, 30));

        // Llenar combo con nodos disponibles
        StringList nodos = controller.getAvailableNodes();
        for (int i = 0; i < nodos.getSize(); i++) {
            nodoCombo.addItem(nodos.get(i));
        }

        JButton explorarBtn = crearBotonEstilizado("Explorar", PRIMARY_COLOR);
        explorarBtn.setPreferredSize(new Dimension(120, 35));

        controlsPanel.add(label);
        controlsPanel.add(nodoCombo);
        controlsPanel.add(explorarBtn);
        
        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(controlsPanel, BorderLayout.EAST);

        // Área de resultado con estilo
        JTextArea resultadoArea = new JTextArea(25, 70);
        resultadoArea.setEditable(false);
        resultadoArea.setFont(MONO_FONT);
        resultadoArea.setBackground(TEXT_AREA_BG);
        resultadoArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(resultadoArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        explorarBtn.addActionListener(e -> {
            String nodoInicio = (String) nodoCombo.getSelectedItem();
            if (nodoInicio != null) {
                String resultado = controller.exploreMapBFS(nodoInicio);
                resultadoArea.setText(resultado);
                agregarAlHistorial("Búsqueda BFS ejecutada desde nodo: " + nodoInicio);
            }
        });

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel crearPanelPersistencia() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(PANEL_BG);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel de título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(PANEL_BG);
        JLabel titleLabel = new JLabel("Persistencia de Datos");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        // Panel de botones
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        botonesPanel.setBackground(PANEL_BG);
        JButton guardarBtn = crearBotonEstilizado("Guardar Datos", SUCCESS_COLOR);
        guardarBtn.setPreferredSize(new Dimension(200, 40));
        botonesPanel.add(guardarBtn);

        // Área de información con estilo
        JTextArea infoArea = new JTextArea(20, 60);
        infoArea.setEditable(false);
        infoArea.setFont(TEXT_FONT);
        infoArea.setBackground(TEXT_AREA_BG);
        infoArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        
        infoArea.setText("═══════════════════════════════════════\n");
        infoArea.append("  SISTEMA DE GUARDADO\n");
        infoArea.append("═══════════════════════════════════════\n\n");
        infoArea.append("DATOS DEL SISTEMA:\n");
        infoArea.append("  • Vehículos y su estado actual\n");
        infoArea.append("  • Servicios realizados\n\n");
        infoArea.append("HISTORIAL:\n");
        infoArea.append("  • Solicitudes agregadas\n");
        infoArea.append("  • Solicitudes procesadas\n");
        infoArea.append("  • Algoritmos de búsqueda ejecutados\n");
        infoArea.append("  • Eventos del sistema\n\n");
        infoArea.append("ARCHIVOS GENERADOS:\n");
        infoArea.append("  - vehiculos.txt\n");
        infoArea.append("  - servicios.txt\n");
        infoArea.append("  - historial.txt\n\n");
        infoArea.append("═══════════════════════════════════════\n");
        
        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        guardarBtn.addActionListener(e -> {
            try {
                controller.saveData();
                infoArea.append("\n✓ Datos guardados exitosamente\n");
                infoArea.setCaretPosition(infoArea.getDocument().getLength());
            } catch (Exception ex) {
                infoArea.append("\n✗ Error al guardar: " + ex.getMessage() + "\n");
                infoArea.setCaretPosition(infoArea.getDocument().getLength());
            }
        });

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(botonesPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        return mainPanel;
    }
    
    // Método auxiliar para crear botones estilizados
    private JButton crearBotonEstilizado(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(LABEL_FONT);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setOpaque(true);
        boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(color.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(color);
            }
        });
        
        return boton;
    }
}
