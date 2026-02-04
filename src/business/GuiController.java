package business;

import java.util.List;

import domain.Servicio;
import domain.Solicitud;
import domain.Utils;
import domain.Vehiculo;

public class GuiController {
    private final RequestController requestController;

    public GuiController(RequestController requestController) {
        this.requestController = requestController;
    }

    public Utils.Grafo getMapa() {
        return requestController.getMapa();
    }

    public Solicitud registrarSolicitud(String cliente, String origen, String destino, int prioridad) {
        return requestController.registrarSolicitud(cliente, origen, destino, prioridad);
    }

    public Servicio procesarSiguienteServicio() {
        return requestController.procesarSiguienteServicio();
    }

    public List<Vehiculo> obtenerVehiculosOrdenadosBurbuja() {
        return requestController.obtenerVehiculosOrdenadosBurbuja();
    }

    public List<Vehiculo> obtenerVehiculosOrdenadosQuickSort() {
        return requestController.obtenerVehiculosOrdenadosQuickSort();
    }
}
