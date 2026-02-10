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
        return requestController.registerRequest(cliente, origen, destino, prioridad);
    }
    
    public Request registrarSolicitud(String cliente, String origen, String destino, int prioridad, int categoria) {
        return requestController.registerRequest(cliente, origen, destino, prioridad, categoria);
    }

    public Service procesarSiguienteServicio() {
        return requestController.processNextService();
    }

    public VehicleList obtenerVehiculosOrdenadosQuickSort() {
        return requestController.getSortedVehiclesQuickSort();
    }
    
    public String explorarMapaBFS(String inicio) {
        return requestController.exploreMapBFS(inicio);
    }
    
    public String obtenerColasReporte() {
        domain.List.RequestQueue urgentQueue = requestController.getUrgentQueue();
        domain.List.RequestQueue normalQueue = requestController.getNormalQueue();
        StringBuilder result = new StringBuilder();

        result.append("=== COLA DE SERVICIOS (PROCESADAS POR ÁRBOL DE TARIFAS) ===\n\n");

        result.append("[EMERGENCIA] COLA URGENTE (Categoría: Emergencia):\n");
        result.append("Tamaño: ").append(urgentQueue.getSize()).append("\n");
        if (urgentQueue.isEmpty()) {
            result.append("  [Vacía]\n");
        } else {
            int index = 1;
            for (Request req : urgentQueue.getAll()) {
                result.append("  ").append(index++).append(". ");
                result.append("ID: ").append(req.getId());
                result.append(" | Cliente: ").append(req.getClientName());
                result.append(" | Ruta: ").append(req.getOrigin()).append(" -> ").append(req.getDestination());
                result.append(" | Categoría: Emergencia");
                result.append("\n");
            }
        }

        result.append("\n");

        result.append("[NORMAL] COLA NORMAL (Procesadas por: VIP > Regular > Económico):\n");
        result.append("Tamaño: ").append(normalQueue.getSize()).append("\n");
        if (normalQueue.isEmpty()) {
            result.append("  [Vacía]\n");
        } else {
            int index = 1;
            for (Request req : normalQueue.getAll()) {
                String[] categories = {"Económico", "Regular", "VIP", "Emergencia"};
                int cat = req.getClientCategory();
                String category = cat >= 0 && cat < 4 ? categories[cat] : "Desconocido";
                result.append("  ").append(index++).append(". ");
                result.append("ID: ").append(req.getId());
                result.append(" | Cliente: ").append(req.getClientName());
                result.append(" | Ruta: ").append(req.getOrigin()).append(" -> ").append(req.getDestination());
                result.append(" | Categoría: ").append(category);
                result.append("\n");
            }
        }

        result.append("\nTotal de solicitudes pendientes: ")
            .append(urgentQueue.getSize() + normalQueue.getSize())
            .append("\n");

        return result.toString();
    }

    public ServiceList obtenerServiciosCompletados() {
        return requestController.getCompletedServices();
    }
    
    public StringList obtenerNodosDisponibles() {
        return requestController.getAvailableNodes();
    }
    
    public void guardarDatos() {
        requestController.saveData();
    }
    
    public void cargarDatos() {
        requestController.loadData();
    }
}
