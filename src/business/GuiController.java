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

    public VehicleList obtenerVehiculosOrdenadosBurbuja() {
        return requestController.obtenerVehiculosOrdenadosBurbuja();
    }

    public VehicleList obtenerVehiculosOrdenadosQuickSort() {
        return requestController.obtenerVehiculosOrdenadosQuickSort();
    }
    
    public String explorarMapaBFS(String inicio) {
        return requestController.explorarMapaBFS(inicio);
    }
    
    public String obtenerColasReporte() {
        RequestController.RequestQueuesData data = requestController.obtenerColasReporte();
        String resultado = "";
        
        resultado += "=== COLAS DE SOLICITUDES ===" + "\n\n";
        
        resultado += "[URGENTE] COLA URGENTE (Prioridad >= 3):" + "\n";
        resultado += "Tamaño: " + data.solicitudesUrgentes.getSize() + "\n";
        if (data.solicitudesUrgentes.isEmpty()) {
            resultado += "  [Vacía]\n";
        } else {
            int index = 1;
            for (Request req : data.solicitudesUrgentes.getAll()) {
                resultado += "  " + index++ + ". ";
                resultado += "ID: " + req.getId();
                resultado += " | Cliente: " + req.getClientName();
                resultado += " | Ruta: " + req.getOrigin() + " -> " + req.getDestination();
                resultado += " | Prioridad: " + req.getPriority();
                resultado += "\n";
            }
        }
        
        resultado += "\n";
        
        resultado += "[NORMAL] COLA NORMAL (Prioridad < 3):" + "\n";
        resultado += "Tamaño: " + data.solicitudesNormales.getSize() + "\n";
        if (data.solicitudesNormales.isEmpty()) {
            resultado += "  [Vacía]\n";
        } else {
            int index = 1;
            for (Request req : data.solicitudesNormales.getAll()) {
                resultado += "  " + index++ + ". ";
                resultado += "ID: " + req.getId();
                resultado += " | Cliente: " + req.getClientName();
                resultado += " | Ruta: " + req.getOrigin() + " -> " + req.getDestination();
                resultado += " | Prioridad: " + req.getPriority();
                resultado += "\n";
            }
        }
        
        resultado += "\nTotal de solicitudes pendientes: " + (data.solicitudesUrgentes.getSize() + data.solicitudesNormales.getSize());
        
        return resultado;
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
