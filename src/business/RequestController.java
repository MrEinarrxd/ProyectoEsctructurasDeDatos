package business;

import java.util.Date;
import domain.List.VehicleList;
import domain.List.StringList;
import domain.List.RequestQueue;
import domain.List.ServiceList;

import domain.Service;
import domain.Request;
import domain.Utils;
import domain.Graphs.Graph;
import domain.Graphs.PathResult;
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
		agregarSolicitud(solicitud);
		registrarEvento("Solicitud registrada - Cliente: " + cliente + ", " + origen + " -> " + destino + ", Prioridad: " + prioridad);
		return solicitud;
	}

	public Service procesarSiguienteServicio() {
		Service servicio = procesarSiguiente();
		if (servicio == null) {
			registrarEvento("Intento de procesar solicitud - Sin solicitudes pendientes");
		} else {
			registrarEvento("Solicitud procesada - Servicio #" + servicio.id + " para " + servicio.request.getClientName());
		}
		return servicio;
	}

	public VehicleList obtenerVehiculosOrdenadosQuickSort() {
		VehicleList vehiculos = new VehicleList();
		int size = utils.vehiculos.getSize();
		Vehicle[] temp = new Vehicle[size];
		for (int i = 0; i < size; i++) {
			temp[i] = utils.vehiculos.get(i);
		}
		if (temp.length > 0) {
			ordenarRapido(temp, 0, temp.length - 1);
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
	
	public RequestQueue obtenerColaUrgente() {
		return utils.colaUrgente.getAll();
	}
	
	public RequestQueue obtenerColaNormal() {
		return utils.colaNormal;
	}
	
	public ServiceList obtenerServiciosCompletados() {
		return utils.servicios;
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

	// ====== Métodos del controller ======
	private void agregarSolicitud(Request s) {
		if (s.getPriority() >= 3) {
			utils.colaUrgente.enqueue(s, s.getPriority());
			utils.historialEventos.add("URGENTE: " + s.getClientName() + " de " + s.getOrigin() + " a " + s.getDestination());
		} else {
			utils.colaNormal.enqueue(s);
			utils.historialEventos.add("NORMAL: " + s.getClientName() + " de " + s.getOrigin() + " a " + s.getDestination());
		}
	}

	private Service procesarSiguiente() {
		Request solicitud = utils.colaUrgente.dequeue();
		if (solicitud == null) {
			solicitud = utils.colaNormal.dequeue();
		}

		if (solicitud == null) {
			utils.historialEventos.add("No hay solicitudes pendientes");
			return null;
		}

		Vehicle vehiculo = asignarVehiculoGreedy(solicitud.getOrigin());
		if (vehiculo == null) {
			utils.historialEventos.add("ERROR: No hay vehiculos para " + solicitud.getOrigin());
			return null;
		}

		PathResult rutaVehiculo = utils.mapa.calcularRutaDijkstra(vehiculo.getCurrentZone(), solicitud.getOrigin());
		PathResult rutaCliente = utils.mapa.calcularRutaDijkstra(solicitud.getOrigin(), solicitud.getDestination());

		int distanciaTotal = rutaVehiculo.distanciaTotal + rutaCliente.distanciaTotal;

		double tarifaBase = utils.tarifas.search("basica");
		if (tarifaBase == 0) tarifaBase = 10.0;
		double costo = tarifaBase * distanciaTotal;

		Service servicio = new Service(solicitud, vehiculo,
			solicitud.getOrigin() + "->" + solicitud.getDestination(), costo);

		servicio.vehicleToClientRoute = String.join(" -> ", rutaVehiculo.camino.toArray());
		servicio.clientToDestinationRoute = String.join(" -> ", rutaCliente.camino.toArray());
		servicio.algorithmDetail = rutaCliente.detalleAlgoritmo;

		utils.servicios.add(servicio);
		utils.historialEventos.add("SERVICIO #" + servicio.id + " creado: $" + costo);

		return servicio;
	}

	private Vehicle asignarVehiculoGreedy(String zona) {
		Vehicle v = utils.vehiculos.buscarDisponible(zona);
		if (v != null) return v;

		int size = utils.vehiculos.getSize();
		for (int i = 0; i < size; i++) {
			Vehicle vehiculo = utils.vehiculos.get(i);
			if (vehiculo.isAvailable()) {
				return vehiculo;
			}
		}
		return null;
	}

	private void ordenarRapido(Vehicle[] lista, int inicio, int fin) {
		if (inicio < fin) {
			int pivote = particionar(lista, inicio, fin);
			ordenarRapido(lista, inicio, pivote - 1);
			ordenarRapido(lista, pivote + 1, fin);
		}
	}

	private int particionar(Vehicle[] lista, int inicio, int fin) {
		Vehicle pivote = lista[fin];
		int i = inicio - 1;

		for (int j = inicio; j < fin; j++) {
			int cmp = Integer.compare(lista[j].getServiceCount(), pivote.getServiceCount());
			if (cmp > 0 || (cmp == 0 && lista[j].getId().compareTo(pivote.getId()) < 0)) {
				i++;
				Vehicle temp = lista[i];
				lista[i] = lista[j];
				lista[j] = temp;
			}
		}

		Vehicle temp = lista[i + 1];
		lista[i + 1] = lista[fin];
		lista[fin] = temp;

		return i + 1;
	}
}