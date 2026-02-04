package business;

import domain.List.VehicleList;
import domain.List.StringList;
import domain.List.ServiceList;
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
        domain.List.RequestQueue colaUrgente = requestController.obtenerColaUrgente();
        domain.List.RequestQueue colaNormal = requestController.obtenerColaNormal();
        StringBuilder resultado = new StringBuilder();

        resultado.append("=== COLA DE SERVICIOS (PROCESADAS POR ÁRBOL DE TARIFAS) ===\n\n");

        resultado.append("[EMERGENCIA] COLA URGENTE (Categoría: Emergencia):\n");
        resultado.append("Tamaño: ").append(colaUrgente.getSize()).append("\n");
        if (colaUrgente.isEmpty()) {
            resultado.append("  [Vacía]\n");
        } else {
            int index = 1;
            for (Request req : colaUrgente.getAll()) {
                resultado.append("  ").append(index++).append(". ");
                resultado.append("ID: ").append(req.getId());
                resultado.append(" | Cliente: ").append(req.getClientName());
                resultado.append(" | Ruta: ").append(req.getOrigin()).append(" -> ").append(req.getDestination());
                resultado.append(" | Categoría: Emergencia");
                resultado.append("\n");
            }
        }

        resultado.append("\n");

        resultado.append("[NORMAL] COLA NORMAL (Procesadas por: VIP > Regular > Económico):\n");
        resultado.append("Tamaño: ").append(colaNormal.getSize()).append("\n");
        if (colaNormal.isEmpty()) {
            resultado.append("  [Vacía]\n");
        } else {
            int index = 1;
            for (Request req : colaNormal.getAll()) {
                String categoria = obtenerNombreCategoria(req.getClientCategory());
                resultado.append("  ").append(index++).append(". ");
                resultado.append("ID: ").append(req.getId());
                resultado.append(" | Cliente: ").append(req.getClientName());
                resultado.append(" | Ruta: ").append(req.getOrigin()).append(" -> ").append(req.getDestination());
                resultado.append(" | Categoría: ").append(categoria);
                resultado.append("\n");
            }
        }

        resultado.append("\nTotal de solicitudes pendientes: ")
            .append(colaUrgente.getSize() + colaNormal.getSize())
            .append("\n");

        return resultado.toString();
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
