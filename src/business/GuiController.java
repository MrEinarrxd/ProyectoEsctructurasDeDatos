package business;

import domain.List.VehicleList;
import domain.List.StringList;
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
        RequestController.RequestQueuesData data = requestController.obtenerColasReporte();
        StringBuilder resultado = new StringBuilder();

        resultado.append("=== COLA DE SERVICIOS ===\n\n");

        resultado.append("[URGENTE] COLA URGENTE (Prioridad >= 3):\n");
        resultado.append("Tamaño: ").append(data.solicitudesUrgentes.getSize()).append("\n");
        if (data.solicitudesUrgentes.isEmpty()) {
            resultado.append("  [Vacía]\n");
        } else {
            int index = 1;
            for (Request req : data.solicitudesUrgentes.getAll()) {
                resultado.append("  ").append(index++).append(". ");
                resultado.append("ID: ").append(req.getId());
                resultado.append(" | Cliente: ").append(req.getClientName());
                resultado.append(" | Ruta: ").append(req.getOrigin()).append(" -> ").append(req.getDestination());
                resultado.append(" | Prioridad: ").append(req.getPriority());
                resultado.append("\n");
            }
        }

        resultado.append("\n");

        resultado.append("[NORMAL] COLA NORMAL (Prioridad < 3):\n");
        resultado.append("Tamaño: ").append(data.solicitudesNormales.getSize()).append("\n");
        if (data.solicitudesNormales.isEmpty()) {
            resultado.append("  [Vacía]\n");
        } else {
            int index = 1;
            for (Request req : data.solicitudesNormales.getAll()) {
                resultado.append("  ").append(index++).append(". ");
                resultado.append("ID: ").append(req.getId());
                resultado.append(" | Cliente: ").append(req.getClientName());
                resultado.append(" | Ruta: ").append(req.getOrigin()).append(" -> ").append(req.getDestination());
                resultado.append(" | Prioridad: ").append(req.getPriority());
                resultado.append("\n");
            }
        }

        resultado.append("\nTotal de solicitudes pendientes: ")
            .append(data.solicitudesUrgentes.getSize() + data.solicitudesNormales.getSize())
            .append("\n");

        return resultado.toString();
    }

    public java.util.List<Service> obtenerServiciosCompletados() {
        return requestController.obtenerServiciosCompletados();
    }
    
    public StringList obtenerNodosDisponibles() {
        return requestController.obtenerNodosDisponibles();
    }
    
    public void guardarDatos() {
        requestController.guardarDatos();
    }
    
    public void cargarDatos() {
        requestController.cargarDatos();
    }
}
