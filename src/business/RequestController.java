package business;

import java.util.List;
import java.util.ArrayList;

import domain.Servicio;
import domain.Solicitud;
import domain.Utils;
import domain.Vehiculo;
import Data.DataManager;

public class RequestController {

	private final Utils utils;
	private final DataManager dataManager;

	public RequestController() {
		this.utils = new Utils();
		this.dataManager = new DataManager();
		initDatos();
	}

	private void initDatos() {
		// Vehículos distribuidos en diferentes pueblos
		utils.vehiculos.agregar(new Vehiculo("V1", "Centro"));
		utils.vehiculos.agregar(new Vehiculo("V2", "Norte"));
		utils.vehiculos.agregar(new Vehiculo("V3", "Sur"));
		utils.vehiculos.agregar(new Vehiculo("V4", "Este"));
		utils.vehiculos.agregar(new Vehiculo("V5", "Oeste"));
		utils.vehiculos.agregar(new Vehiculo("V6", "Noroeste"));
		utils.vehiculos.agregar(new Vehiculo("V7", "Suroeste"));

		// Red en forma de cruz con nodos en esquinas
		utils.mapa.agregarConexion("Centro", "Norte", 10);
		utils.mapa.agregarConexion("Centro", "Sur", 12);
		utils.mapa.agregarConexion("Centro", "Este", 11);
		utils.mapa.agregarConexion("Centro", "Oeste", 9);
		
		// Conexiones en esquinas
		utils.mapa.agregarConexion("Noroeste", "Norte", 8);
		utils.mapa.agregarConexion("Noroeste", "Oeste", 7);
		utils.mapa.agregarConexion("Noroeste", "Centro", 14);
		
		utils.mapa.agregarConexion("Suroeste", "Sur", 9);
		utils.mapa.agregarConexion("Suroeste", "Oeste", 6);
		utils.mapa.agregarConexion("Suroeste", "Centro", 15);
		
		// Conexión entre esquinas
		utils.mapa.agregarConexion("Noroeste", "Suroeste", 16);
		
		// Diagonal
		utils.mapa.agregarConexion("Norte", "Este", 14);

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
	
	public String explorarMapaBFS(String inicio) {
		return utils.mapa.bfs(inicio);
	}
	
	public List<String> obtenerNodosDisponibles() {
		List<String> nodos = new ArrayList<>();
		var mapa = utils.mapa.obtenerMapaAristas();
		for (String nodo : mapa.keySet()) {
			nodos.add(nodo);
		}
		return nodos;
	}
	
	public void guardarDatos() {
		dataManager.guardarTodo(utils);
	}
	
	public void cargarDatos() {
		dataManager.cargarTodo(utils);
	}
}
