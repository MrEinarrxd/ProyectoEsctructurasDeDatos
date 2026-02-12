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
        StringBuilder sb = new StringBuilder();

        sb.append("SOLICITUDES PENDIENTES\n");
        sb.append("================================\n\n");

        sb.append("URGENTES (Emergencia):\n");
        if (urgentQueue.isEmpty()) {
            sb.append("  Ninguna\n");
        } else {
            for (int i = 0; i < urgentQueue.getSize(); i++) {
                Request req = urgentQueue.get(i);
                sb.append("  ").append(i+1).append(". ").append(req.getClientName())
                  .append(" | ").append(req.getOrigin()).append(" -> ")
                  .append(req.getDestination()).append("\n");
            }
        }

        sb.append("\nNORMALES (por prioridad):\n");
        if (normalQueue.isEmpty()) {
            sb.append("  Ninguna\n");
        } else {
            for (int i = 0; i < normalQueue.getSize(); i++) {
                Request req = normalQueue.get(i);
                String category = Utils.getCategoryName(req.getClientCategory());
                sb.append("  ").append(i+1).append(". ").append(req.getClientName())
                  .append(" (").append(category).append(") | ")
                  .append(req.getOrigin()).append(" -> ")
                  .append(req.getDestination()).append("\n");
            }
        }

        sb.append("\nTotal: ").append(urgentQueue.getSize() + normalQueue.getSize())
          .append(" solicitudes\n");

        return sb.toString();
    }

    public ServiceList getCompletedServices() {
        return requestController.getCompletedServices();
    }
    
    public StringList getAvailableNodes() {
        return requestController.getAvailableNodes();
    }

    public double getRate(String category) {
        return requestController.getRate(category);
    }
    
    public String getVehiclesReport() {
        VehicleList vehicles = getSortedVehiclesQuickSort();
        StringBuilder sb = new StringBuilder();
        
        sb.append("VEHICULOS DISPONIBLES (Ordenados por servicios)\n");
        sb.append("================================\n\n");
        
        for (int i = 0; i < vehicles.getSize(); i++) {
            domain.Vehicle v = vehicles.get(i);
            String status = v.isAvailable() ? "Disponible" : "En servicio";
            sb.append(v.getId()).append(" | Zona: ").append(v.getCurrentZone())
              .append(" | Conductor: ").append(v.getDriverName())
              .append(" | Tipo: ").append(v.getVehicleType())
              .append(" | Servicios: ").append(v.getServiceCount())
              .append(" | Estado: ").append(status).append("\n");
        }
        
        return sb.toString();
    }
    
    public String getCompletedServicesReport() {
        ServiceList services = getCompletedServices();
        StringBuilder sb = new StringBuilder();
        
        sb.append("SERVICIOS COMPLETADOS\n");
        sb.append("================================\n\n");
        
        if (services.getSize() == 0) {
            sb.append("Ninguno\n");
        } else {
            for (int i = 0; i < services.getSize(); i++) {
                Service service = services.get(i);
                sb.append("SERVICIO #").append(service.id).append("\n");
                sb.append("  Cliente: ").append(service.request.getClientName()).append("\n");
                sb.append("  Ruta: ").append(service.request.getOrigin())
                  .append(" -> ").append(service.request.getDestination()).append("\n");
                sb.append("  Vehiculo: ").append(service.vehicle.getId()).append("\n");
                sb.append("  Costo: $").append(String.format("%.2f", service.cost)).append("\n");
                sb.append("  Distancia: ").append(String.join(" -> ", service.clientToDestinationRoute.split(" -> ")))
                  .append("\n\n");
            }
        }
        
        return sb.toString();
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
    
    public String displayServiceDetails(Service service) {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================================\n");
        sb.append("SERVICIO #").append(service.id).append(" COMPLETADO\n");
        sb.append("=====================================================\n\n");
        
        sb.append("INFORMACIÓN GENERAL:\n");
        sb.append("  Cliente: ").append(service.request.getClientName()).append("\n");
        sb.append("  Vehículo: ").append(service.vehicle.getId())
          .append(" (Zona: ").append(service.vehicle.getCurrentZone()).append(")\n");
        sb.append("  Ruta: ").append(service.request.getOrigin())
          .append(" -> ").append(service.request.getDestination()).append("\n");
        sb.append("  Costo Total: $").append(String.format("%.2f", service.cost)).append("\n\n");
        
        sb.append("RUTA DEL VEHÍCULO AL CLIENTE:\n");
        sb.append("  ").append(service.vehicleToClientRoute).append("\n\n");
        
        sb.append("RUTA DEL CLIENTE AL DESTINO:\n");
        sb.append("  ").append(service.clientToDestinationRoute).append("\n\n");
        
        sb.append("DETALLE DEL ALGORITMO\n");
        sb.append("=====================================================\n\n");
        sb.append(service.algorithmDetail);
        
        return sb.toString();
    }
}
