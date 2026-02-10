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
import domain.Graphs.Path;
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
		return registrarSolicitud(cliente, origen, destino, prioridad, 1); // Por defecto regular
	}

	public Request registrarSolicitud(String cliente, String origen, String destino, int prioridad, int clientCategory) {
		int numeroRandom = (int)(Math.random() * 1000);
		String id = "REQ" + numeroRandom;
		Request solicitud = new Request(id, origen, destino, cliente, clientCategory);
		agregarSolicitud(solicitud);
		String categoria = obtenerNombreCategoria(clientCategory);
		registrarEvento("Solicitud registrada - Cliente: " + cliente + " (" + categoria + "), " + origen + " -> " + destino);
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
		return utils.historialEventos.getAll();
	}
	
	public void registrarEvento(String evento) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(new Date());
		utils.historialEventos.push("[" + timestamp + "] " + evento);
	}

	// ====== Métodos del controller ======
	private String obtenerNombreCategoria(int categoria) {
		switch(categoria) {
			case 0: return "Económico";
			case 1: return "Regular";
			case 2: return "VIP";
			case 3: return "Emergencia";
			default: return "Desconocido";
		}
	}

	private void agregarSolicitud(Request s) {
		// Procesar por categoría de cliente (mayor categoría = mayor prioridad en procesamiento)
		// 3=Emergencia, 2=VIP, 1=Regular, 0=Económico
		if (s.getClientCategory() == 3) {
			// Emergencia va a cola urgente
			utils.colaUrgente.enqueue(s, 4);
			utils.historialEventos.push("EMERGENCIA: " + s.getClientName() + " de " + s.getOrigin() + " a " + s.getDestination());
		} else {
			// Todas las demás (VIP, Regular, Económico) van a cola normal, ordenadas por categoría
			utils.colaNormal.enqueue(s);
			String categoria = obtenerNombreCategoria(s.getClientCategory());
			utils.historialEventos.push(categoria.toUpperCase() + ": " + s.getClientName() + " de " + s.getOrigin() + " a " + s.getDestination());
		}
	}

	private Service procesarSiguiente() {
		// Primero buscar si hay emergencias
		Request solicitud = utils.colaUrgente.dequeue();
		
		// Si no hay emergencias, buscar en la cola normal por categoría de tarifa
		// Orden: VIP (2) > Regular (1) > Económico (0)
		if (solicitud == null) {
			solicitud = buscarSolicitudPorCategoria(2); // VIP
		}
		if (solicitud == null) {
			solicitud = buscarSolicitudPorCategoria(1); // Regular
		}
		if (solicitud == null) {
			solicitud = buscarSolicitudPorCategoria(0); // Económico
		}

		if (solicitud == null) {
			utils.historialEventos.push("No hay solicitudes pendientes");
			return null;
		}

		Vehicle vehiculo = asignarVehiculoGreedy(solicitud.getOrigin());
		if (vehiculo == null) {
			utils.historialEventos.push("ERROR: No hay vehiculos para " + solicitud.getOrigin());
			return null;
		}

		Path rutaVehiculo = utils.mapa.calcularRutaDijkstra(vehiculo.getCurrentZone(), solicitud.getOrigin());
		Path rutaCliente = utils.mapa.calcularRutaDijkstra(solicitud.getOrigin(), solicitud.getDestination());

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
		utils.historialEventos.push("SERVICIO #" + servicio.id + " creado: $" + costo);

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

	// Buscar la primera solicitud de una categoría específica en la cola normal
	private Request buscarSolicitudPorCategoria(int categoria) {
		RequestQueue colaNormal = utils.colaNormal;
		RequestQueue temp = new RequestQueue();
		Request encontrada = null;

		// Desencolar todos para buscar
		while (true) {
			Request req = colaNormal.dequeue();
			if (req == null) break;
			
			if (encontrada == null && req.getClientCategory() == categoria) {
				encontrada = req; // Encontramos la primera de esta categoría
			} else {
				temp.enqueue(req); // Guardar para devolver a la cola
			}
		}

		// Devolver solicitudes a la cola
		RequestQueue tempAll = temp.getAll();
		for (int i = 0; i < tempAll.size(); i++) {
			colaNormal.enqueue(tempAll.get(i));
		}

		return encontrada;
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