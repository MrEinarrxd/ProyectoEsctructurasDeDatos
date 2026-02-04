package business;

import java.util.List;

import domain.Servicio;
import domain.Solicitud;
import domain.Utils;
import domain.Vehiculo;

public class RequestController {

	private final Utils utils;

	public RequestController() {
		this.utils = new Utils();
		initDatos();
	}

	private void initDatos() {
		utils.vehiculos.agregar(new Vehiculo("V1", "Centro"));
		utils.vehiculos.agregar(new Vehiculo("V2", "Norte"));
		utils.vehiculos.agregar(new Vehiculo("V3", "Sur"));

		utils.mapa.agregarConexion("Centro", "Norte", 5);
		utils.mapa.agregarConexion("Centro", "Sur", 4);
		utils.mapa.agregarConexion("Norte", "Este", 7);
		utils.mapa.agregarConexion("Sur", "Oeste", 6);
		utils.mapa.agregarConexion("Este", "Oeste", 8);

		utils.tarifas.agregar("basica", 10.0);
		utils.tarifas.agregar("premium", 15.0);
		utils.tarifas.agregar("vip", 25.0);
	}

	public Utils.Grafo getMapa() {
		return utils.mapa;
	}

	public Solicitud registrarSolicitud(String cliente, String origen, String destino, int prioridad) {
		Solicitud solicitud = new Solicitud(cliente, origen, destino, prioridad);
		utils.agregarSolicitud(solicitud);
		return solicitud;
	}

	public Servicio procesarSiguienteServicio() {
		return utils.procesarSiguiente();
	}

	public List<Vehiculo> obtenerVehiculosOrdenadosBurbuja() {
		List<Vehiculo> vehiculos = utils.vehiculos.obtenerTodos();
		utils.ordenarBurbuja(vehiculos);
		return vehiculos;
	}

	public List<Vehiculo> obtenerVehiculosOrdenadosQuickSort() {
		List<Vehiculo> vehiculos = utils.vehiculos.obtenerTodos();
		if (!vehiculos.isEmpty()) {
			utils.ordenarRapido(vehiculos, 0, vehiculos.size() - 1);
		}
		return vehiculos;
	}
}
