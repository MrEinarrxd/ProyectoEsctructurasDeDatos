package business;

import java.util.Date;
import domain.List.VehicleList;
import domain.List.StringList;
import domain.List.RequestQueue;

import domain.Service;
import domain.Request;
import domain.Utils;
import domain.Graphs.Graph;
import domain.Vehicle;
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
		dataManager.cargarDatosIniciales(utils, "datos_iniciales.txt");
	}

	public Graph getMapa() {
		return utils.mapa;
	}

	public Request registrarSolicitud(String cliente, String origen, String destino, int prioridad) {
		int numeroRandom = (int)(Math.random() * 1000);
		String id = "REQ" + numeroRandom;
		Request solicitud = new Request(id, origen, destino, cliente, prioridad);
		utils.agregarSolicitud(solicitud);
		registrarEvento("Solicitud registrada - Cliente: " + cliente + ", " + origen + " -> " + destino + ", Prioridad: " + prioridad);
		return solicitud;
	}

	public Service procesarSiguienteServicio() {
		Service servicio = utils.procesarSiguiente();
		if (servicio == null) {
			registrarEvento("Intento de procesar solicitud - Sin solicitudes pendientes");
		} else {
			registrarEvento("Solicitud procesada - Servicio #" + servicio.id + " para " + servicio.request.getClientName());
		}
		return servicio;
	}

	public VehicleList obtenerVehiculosOrdenadosBurbuja() {
		VehicleList vehiculos = new VehicleList();
		java.util.List<Vehicle> temp = utils.vehiculos.obtenerTodos();
		utils.ordenarBurbuja(temp);
		for (Vehicle v : temp) {
			vehiculos.add(v);
		}
		return vehiculos;
	}

	public VehicleList obtenerVehiculosOrdenadosQuickSort() {
		VehicleList vehiculos = new VehicleList();
		java.util.List<Vehicle> temp = utils.vehiculos.obtenerTodos();
		if (!temp.isEmpty()) {
			utils.ordenarRapido(temp, 0, temp.size() - 1);
		}
		for (Vehicle v : temp) {
			vehiculos.add(v);
		}
		return vehiculos;
	}
	
	public String explorarMapaBFS(String inicio) {
		String resultado = utils.mapa.bfs(inicio);
		registrarEvento("Búsqueda BFS ejecutada desde nodo: " + inicio);
		return resultado;
	}
	
	public class RequestQueuesData {
		public RequestQueue solicitudesUrgentes;
		public RequestQueue solicitudesNormales;
		
		public RequestQueuesData(RequestQueue urgentes, RequestQueue normales) {
			this.solicitudesUrgentes = urgentes;
			this.solicitudesNormales = normales;
		}
	}
	
	public RequestQueuesData obtenerColasReporte() {
		return new RequestQueuesData(
			utils.colaUrgente.getAll(),
			utils.colaNormal
		);
	}
	
	public StringList obtenerNodosDisponibles() {
		StringList nodos = new StringList();
		var mapa = utils.mapa.obtenerMapaAristas();
		for (String nodo : mapa.keySet()) {
			nodos.add(nodo);
		}
		return nodos;
	}
	
	public void guardarDatos() {
		dataManager.guardarTodo(utils);
		dataManager.guardarHistorial(obtenerHistorial(), "historial.txt");
	}
	
	public void cargarDatos() {
		dataManager.cargarTodo(utils);
	}
	
	public StringList obtenerHistorial() {
		StringList result = new StringList();
		for (String evento : utils.historialEventos.getAll()) {
			result.add(evento);
		}
		return result;
	}
	
	public void registrarEvento(String evento) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(new Date());
		utils.historialEventos.add("[" + timestamp + "] " + evento);
	}
}
