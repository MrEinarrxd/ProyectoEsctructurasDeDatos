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
        tabbedPane.addTab("Persistencia", crearPanelPersistencia());

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
        List<String> nodos = controller.obtenerNodosDisponibles();
        for (String nodo : nodos) {
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

            Solicitud solicitud = controller.registrarSolicitud(cliente, origen, destino, prioridad);

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
            Servicio servicio = controller.procesarSiguienteServicio();
            if (servicio == null) {
                resultadoArea.setText("No hay solicitudes pendientes\n");
            } else {
                resultadoArea.setText(""); // Limpiar área
                resultadoArea.append("═══════════════════════════════════════════════════════════════\n");
                resultadoArea.append("SERVICIO #" + servicio.id + " COMPLETADO\n");
                resultadoArea.append("═══════════════════════════════════════════════════════════════\n\n");
                
                resultadoArea.append("INFORMACIÓN GENERAL:\n");
                resultadoArea.append("  Cliente: " + servicio.solicitud.cliente + "\n");
                resultadoArea.append("  Vehículo: " + servicio.vehiculo.id + " (Zona: " + servicio.vehiculo.zona + ")\n");
                resultadoArea.append("  Ruta: " + servicio.solicitud.origen + " → " + servicio.solicitud.destino + "\n");
                resultadoArea.append("  Costo Total: $" + String.format("%.2f", servicio.costo) + "\n\n");
                
                resultadoArea.append("RUTA DEL VEHÍCULO AL CLIENTE:\n");
                String rutaVehiculo = servicio.rutaVehiculoCliente != null && !servicio.rutaVehiculoCliente.isEmpty() 
                    ? servicio.rutaVehiculoCliente 
                    : "Vehículo ya en ubicación";
                resultadoArea.append("  " + rutaVehiculo + "\n\n");
                
                resultadoArea.append("RUTA DEL CLIENTE AL DESTINO:\n");
                String rutaCliente = servicio.rutaClienteDestino != null && !servicio.rutaClienteDestino.isEmpty() 
                    ? servicio.rutaClienteDestino 
                    : "No disponible";
                resultadoArea.append("  " + rutaCliente + "\n\n");
                
                if (servicio.algoritmoDetalle != null && !servicio.algoritmoDetalle.isEmpty()) {
                    resultadoArea.append("═══════════════════════════════════════════════════════════════\n");
                    resultadoArea.append("DETALLE DEL ALGORITMO\n");
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
    
    private JPanel crearPanelBFS() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JPanel topPanel = new JPanel();
        JLabel label = new JLabel("Nodo Inicial:");
        JComboBox<String> nodoCombo = new JComboBox<>();
        
        // Llenar combo con nodos disponibles
        List<String> nodos = controller.obtenerNodosDisponibles();
        for (String nodo : nodos) {
            nodoCombo.addItem(nodo);
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
        JButton cargarBtn = new JButton("📂 Cargar Datos");
        
        botonesPanel.add(guardarBtn);
        botonesPanel.add(cargarBtn);
        
        JTextArea infoArea = new JTextArea(20, 60);
        infoArea.setEditable(false);
        infoArea.setText("SISTEMA DE PERSISTENCIA\n\n");
        infoArea.append("Aquí puedes guardar y cargar los datos del sistema.\n\n");
        infoArea.append("Se guardan:\n");
        infoArea.append("  • Vehículos y su estado\n");
        infoArea.append("  • Servicios realizados\n");
        infoArea.append("  • Mapa de conexiones\n\n");
        infoArea.append("Los archivos se guardan en:\n");
        infoArea.append("  - vehiculos.txt\n");
        infoArea.append("  - servicios.txt\n");
        infoArea.append("  - mapa.txt\n");
        
        guardarBtn.addActionListener(e -> {
            try {
                controller.guardarDatos();
                infoArea.append("\n✓ Datos guardados exitosamente\n");
            } catch (Exception ex) {
                infoArea.append("\n✗ Error al guardar: " + ex.getMessage() + "\n");
            }
        });
        
        cargarBtn.addActionListener(e -> {
            try {
                controller.cargarDatos();
                infoArea.append("\n✓ Datos cargados exitosamente\n");
            } catch (Exception ex) {
                infoArea.append("\n✗ Error al cargar: " + ex.getMessage() + "\n");
            }
        });
        
        panel.add(botonesPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        
        return panel;
    }
}
