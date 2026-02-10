package business;

import domain.List.VehicleList;
import domain.List.StringList;
import domain.List.ServiceList;
import domain.List.RequestQueue;
import domain.Service;
import domain.Utils;
import domain.Request;
import domain.Graphs.Graph;

public class GuiController {
    private final RequestController requestController;

    public GuiController(RequestController requestController) {
        this.requestController = requestController;
    }

    public Graph getMap() {
        return requestController.getMap();
    }

    public Request registerRequest(String clientName, String origin, String destination, int priority) {
        return requestController.registerRequest(clientName, origin, destination, priority);
    }
    
    public Request registerRequest(String clientName, String origin, String destination, int priority, int category) {
        return requestController.registerRequest(clientName, origin, destination, priority, category);
    }

    public Service processNextService() {
        return requestController.processNextService();
    }

    public VehicleList getSortedVehiclesQuickSort() {
        return requestController.getSortedVehiclesQuickSort();
    }
    
    public String exploreMapBFS(String start) {
        return requestController.exploreMapBFS(start);
    }
    
    public String getQueuesReport() {
        RequestQueue urgentQueue = requestController.getUrgentQueue();
        RequestQueue normalQueue = requestController.getNormalQueue();
        String result = "";

        result += "=== COLA DE SERVICIOS (PROCESADAS POR ÁRBOL DE TARIFAS) ===\n\n";

        result += "[EMERGENCIA] COLA URGENTE (Categoría: Emergencia):\n";
        result += "Tamaño: " + urgentQueue.getSize() + "\n";
        if (urgentQueue.isEmpty()) {
            result += "  [Vacía]\n";
        } else {
            int index = 1;
            for (int i = 0; i < urgentQueue.getSize(); i++) {
                Request req = urgentQueue.get(i);
                result += "  " + index++ + ". ";
                result += "ID: " + req.getId();
                result += " | Cliente: " + req.getClientName();
                result += " | Ruta: " + req.getOrigin() + " -> " + req.getDestination();
                result += " | Categoría: Emergencia";
                result += "\n";
            }
        }

        result += "\n";

        result += "[NORMAL] COLA NORMAL (Procesadas por: VIP > Regular > Económico):\n";
        result += "Tamaño: " + normalQueue.getSize() + "\n";
        if (normalQueue.isEmpty()) {
            result += "  [Vacía]\n";
        } else {
            int index = 1;
            for (int i = 0; i < normalQueue.getSize(); i++) {
                Request req = normalQueue.get(i);
                String category = Utils.getCategoryName(req.getClientCategory());
                result += "  " + index++ + ". ";
                result += "ID: " + req.getId();
                result += " | Cliente: " + req.getClientName();
                result += " | Ruta: " + req.getOrigin() + " -> " + req.getDestination();
                result += " | Categoría: " + category;
                result += "\n";
            }
        }

        result += "\nTotal de solicitudes pendientes: " + (urgentQueue.getSize() + normalQueue.getSize()) + "\n";

        return result;
    }

    public ServiceList getCompletedServices() {
        return requestController.getCompletedServices();
    }
    
    public StringList getAvailableNodes() {
        return requestController.getAvailableNodes();
    }
    
    
    public void saveData() {
        requestController.saveData();
    }
    
    public void loadData() {
        requestController.loadData();
    }
    
    public StringList getHistory() {
        return requestController.getHistory();
    }
}
