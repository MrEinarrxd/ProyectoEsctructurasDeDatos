package business;

import domain.List.VehicleList;
import domain.List.StringList;
import domain.List.ServiceList;
import domain.List.RequestQueue;
import domain.Service;
import domain.Request;
import domain.Graphs.Graph;

public class GuiController {
    private final RequestController requestController;

    public GuiController(RequestController requestController) {
        this.requestController = requestController;
    }

    public Graph getMapa() {
        return requestController.getMapa();
    }

    public Request registrarSolicitud(String cliente, String origen, String destino, int prioridad) {
        return requestController.registrarSolicitud(cliente, origen, destino, prioridad);
    }
    
    public Request registrarSolicitud(String cliente, String origen, String destino, int prioridad, int categoria) {
        return requestController.registrarSolicitud(cliente, origen, destino, prioridad, categoria);
    }

    public Service procesarSiguienteServicio() {
        return requestController.procesarSiguienteServicio();
    }

    public VehicleList obtenerVehiculosOrdenadosQuickSort() {
        return requestController.obtenerVehiculosOrdenadosQuickSort();
    }
    
    public String explorarMapaBFS(String inicio) {
        return requestController.explorarMapaBFS(inicio);
    }
    
    public String obtenerColasReporte() {
        RequestQueue colaUrgente = requestController.obtenerColaUrgente();
        RequestQueue colaNormal = requestController.obtenerColaNormal();
        String resultado = "";

        resultado += "=== COLA DE SERVICIOS (PROCESADAS POR ÁRBOL DE TARIFAS) ===\n\n";

        resultado += "[EMERGENCIA] COLA URGENTE (Categoría: Emergencia):\n";
        resultado += "Tamaño: " + colaUrgente.getSize() + "\n";
        if (colaUrgente.isEmpty()) {
            resultado += "  [Vacía]\n";
        } else {
            int index = 1;
            for (int i = 0; i < colaUrgente.getSize(); i++) {
                Request req = colaUrgente.get(i);
                resultado += "  " + index++ + ". ";
                resultado += "ID: " + req.getId();
                resultado += " | Cliente: " + req.getClientName();
                resultado += " | Ruta: " + req.getOrigin() + " -> " + req.getDestination();
                resultado += " | Categoría: Emergencia";
                resultado += "\n";
            }
        }

        resultado += "\n";

        resultado += "[NORMAL] COLA NORMAL (Procesadas por: VIP > Regular > Económico):\n";
        resultado += "Tamaño: " + colaNormal.getSize() + "\n";
        if (colaNormal.isEmpty()) {
            resultado += "  [Vacía]\n";
        } else {
            int index = 1;
            for (int i = 0; i < colaNormal.getSize(); i++) {
                Request req = colaNormal.get(i);
                String categoria = obtenerNombreCategoria(req.getClientCategory());
                resultado += "  " + index++ + ". ";
                resultado += "ID: " + req.getId();
                resultado += " | Cliente: " + req.getClientName();
                resultado += " | Ruta: " + req.getOrigin() + " -> " + req.getDestination();
                resultado += " | Categoría: " + categoria;
                resultado += "\n";
            }
        }

        resultado += "\nTotal de solicitudes pendientes: " + (colaUrgente.getSize() + colaNormal.getSize()) + "\n";

        return resultado;
    }

    public ServiceList obtenerServiciosCompletados() {
        return requestController.obtenerServiciosCompletados();
    }
    
    public StringList obtenerNodosDisponibles() {
        return requestController.obtenerNodosDisponibles();
    }
    
    private String obtenerNombreCategoria(int categoria) {
        switch(categoria) {
            case 0: return "Económico";
            case 1: return "Regular";
            case 2: return "VIP";
            case 3: return "Emergencia";
            default: return "Desconocido";
        }
    }
    
    public void guardarDatos() {
        requestController.guardarDatos();
    }
    
    public void cargarDatos() {
        requestController.cargarDatos();
    }
}
