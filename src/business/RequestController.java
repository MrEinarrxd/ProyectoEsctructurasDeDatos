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

	/**
	 * Constructor del controlador de solicitudes.
	 * Inicializa las utilidades y carga los datos iniciales desde archivo.
	 */
	public RequestController() {
		this.utils = new Utils();
		this.dataManager = new DataManager();
		initData();
	}

	/**
	 * Carga los datos iniciales del sistema desde el archivo de configuración.
	 * Incluye vehículos, mapa de zonas y tarifas base.
	 */
	private void initData() {
		dataManager.loadInitialData(utils, "datos_iniciales.txt");
	}

	public Graph getMapa() {
		return utils.mapa;
	}

	/**
	 * Registra una nueva solicitud con categoría Regular por defecto.
	 */
	public Request registerRequest(String client, String origin, String destination, int priority) {
		return registerRequest(client, origin, destination, priority, 1); // Por defecto Regular
	}

	/**
	 * Registra una nueva solicitud de transporte en el sistema.
	 * Genera un ID único, crea la solicitud y la agrega a la cola correspondiente.
	 */
	public Request registerRequest(String client, String origin, String destination, int priority, int clientCategory) {
		// Generar ID único aleatorio para la solicitud
		int randomNumber = (int)(Math.random() * 1000);
		String id = "REQ" + randomNumber;
		Request request = new Request(id, origin, destination, client, clientCategory);
		addRequest(request);
		String category = getCategoryName(clientCategory);
		registerEvent("Solicitud registrada - Cliente: " + client + " (" + category + "), " + origin + " -> " + destination);
		return request;
	}

	public Service processNextService() {
		Service service = processNext();
		if (service == null) {
			registerEvent("Intento de procesar solicitud - Sin solicitudes pendientes");
		} else {
			registerEvent("Solicitud procesada - Servicio #" + service.id + " para " + service.request.getClientName());
		}
		return service;
	}

	public VehicleList getSortedVehiclesQuickSort() {
		VehicleList vehicles = new VehicleList();
		int size = utils.vehiculos.getSize();
		Vehicle[] temp = new Vehicle[size];
		for (int i = 0; i < size; i++) {
			temp[i] = utils.vehiculos.get(i);
		}
		if (temp.length > 0) {
			quickSort(temp, 0, temp.length - 1);
		}
		for (Vehicle v : temp) {
			vehicles.add(v);
		}
		return vehicles;
	}
	
	public String exploreMapBFS(String start) {
		String result = utils.mapa.bfs(start);
		registerEvent("Búsqueda BFS ejecutada desde nodo: " + start);
		return result;
	}
	
	public RequestQueue getUrgentQueue() {
		return utils.colaUrgente.getAll();
	}
	
	public RequestQueue getNormalQueue() {
		return utils.colaNormal;
	}
	
	public ServiceList getCompletedServices() {
		return utils.servicios;
	}
	
	public StringList getAvailableNodes() {
		StringList nodes = new StringList();
		var map = utils.mapa.getEdgeMap();
		for (String node : map.keySet()) {
			nodes.add(node);
		}
		return nodes;
	}
	
	public void saveData() {
		dataManager.saveAll(utils);
		dataManager.saveHistory(getHistory(), "historial.txt");
	}
	
	public void loadData() {
		dataManager.loadAll(utils);
	}
	
	public StringList getHistory() {
		StringList result = new StringList();
		for (String event : utils.historialEventos.getAll()) {
			result.add(event);
		}
		return result;
	}
	
	public void registerEvent(String event) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(new Date());
		utils.historialEventos.push("[" + timestamp + "] " + event);
	}

	// ====== Controller methods ======
	private String getCategoryName(int category) {
		switch(category) {
			case 0: return "Económico";
			case 1: return "Regular";
			case 2: return "VIP";
			case 3: return "Emergencia";
			default: return "Desconocido";
		}
	}

	/**
	 * Agrega una solicitud a la cola correspondiente según su categoría.
	 * LÓGICA DE ENCOLAMIENTO:
	 * - Emergencia (3): Va a cola de prioridad urgente con prioridad 4 (máxima)
	 * - VIP/Regular/Económico (2/1/0): Van a cola normal, se procesan por categoría
	 */
	private void addRequest(Request r) {
		// Procesar por categoría de cliente (mayor categoría = mayor prioridad en procesamiento)
		// 3=Emergencia, 2=VIP, 1=Regular, 0=Económico
		if (r.getClientCategory() == 3) {
			// Emergencia va a cola urgente con prioridad máxima
			utils.colaUrgente.enqueue(r, 4);
			utils.historialEventos.push("EMERGENCIA: " + r.getClientName() + " de " + r.getOrigin() + " a " + r.getDestination());
		} else {
			// Todas las demás (VIP, Regular, Económico) van a cola normal
			utils.colaNormal.enqueue(r);
			String category = getCategoryName(r.getClientCategory());
			utils.historialEventos.push(category.toUpperCase() + ": " + r.getClientName() + " de " + r.getOrigin() + " a " + r.getDestination());
		}
	}

	/**
	 * Procesa la siguiente solicitud pendiente según prioridades.
	 * ALGORITMO DE PRIORIZACIÓN:
	 * 1. Primero procesa solicitudes de Emergencia (cola urgente)
	 * 2. Luego procesa por categoría de tarifa: VIP > Regular > Económico
	 * 3. Asigna vehículo usando estrategia greedy (primero disponible en zona)
	 * 4. Calcula rutas usando algoritmo de Dijkstra
	 * 5. Calcula costo basado en distancia total y tarifa base
	 */
	private Service processNext() {
		// Primero buscar si hay emergencias (máxima prioridad)
		Request request = utils.colaUrgente.dequeue();
		
		// Si no hay emergencias, buscar en la cola normal por categoría de tarifa
		// Orden de prioridad: VIP (2) > Regular (1) > Económico (0)
		if (request == null) {
			request = findRequestByCategory(2); // VIP
		}
		if (request == null) {
			request = findRequestByCategory(1); // Regular
		}
		if (request == null) {
			request = findRequestByCategory(0); // Económico
		}

		// Si no hay ninguna solicitud pendiente, terminar
		if (request == null) {
			utils.historialEventos.push("No hay solicitudes pendientes");
			return null;
		}

		// Asignar vehículo usando algoritmo greedy (primero en zona, luego cualquier disponible)
		Vehicle vehicle = assignVehicleGreedy(request.getOrigin());
		if (vehicle == null) {
			utils.historialEventos.push("ERROR: No hay vehiculos para " + request.getOrigin());
			return null;
		}

		// Calcular ruta óptima del vehículo al cliente usando Dijkstra
		Path vehicleRoute = utils.mapa.calculateDijkstraRoute(vehicle.getCurrentZone(), request.getOrigin());
		// Calcular ruta óptima del cliente a su destino usando Dijkstra
		Path clientRoute = utils.mapa.calculateDijkstraRoute(request.getOrigin(), request.getDestination());

        // Calcular distancia total del servicio
        int totalDistance = vehicleRoute.distance + clientRoute.distance;

        // Obtener tarifa base del árbol de tarifas y calcular costo
        double baseRate = utils.tarifas.search("basica");
        if (baseRate == 0) baseRate = 10.0; // Tarifa por defecto si no existe
        double cost = baseRate * totalDistance;

        // Crear el servicio con toda la información
        Service service = new Service(request, vehicle,
            request.getOrigin() + "->" + request.getDestination(), cost);

        // Guardar las rutas detalladas y el algoritmo usado
        service.vehicleToClientRoute = String.join(" -> ", vehicleRoute.path.toArray());
        service.clientToDestinationRoute = String.join(" -> ", clientRoute.path.toArray());
        service.algorithmDetail = clientRoute.algorithmDetail;

		utils.servicios.add(service);
		utils.historialEventos.push("SERVICIO #" + service.id + " creado: $" + cost);

		return service;
	}

	/**
	 * Asigna un vehículo usando estrategia greedy (algoritmo voraz).
	 * ESTRATEGIA DE ASIGNACIÓN:
	 * 1. Buscar primero vehículo disponible en la zona solicitada (óptimo)
	 * 2. Si no hay en zona, buscar cualquier vehículo disponible (subóptimo)
	 */
	private Vehicle assignVehicleGreedy(String zone) {
		// Primero intentar encontrar vehículo en la zona (minimiza distancia)
		Vehicle v = utils.vehiculos.findAvailable(zone);
		if (v != null) return v;

		// Si no hay en zona, buscar cualquier vehículo disponible
		int size = utils.vehiculos.getSize();
		for (int i = 0; i < size; i++) {
			Vehicle vehicle = utils.vehiculos.get(i);
			if (vehicle.isAvailable()) {
				return vehicle;
			}
		}
		return null;
	}

	/**
	 * Algoritmo QuickSort recursivo para ordenar vehículos.
	 * Ordena por cantidad de servicios (mayor a menor), desempata por ID alfabético.
	 * Complejidad: O(n log n) promedio, O(n²) peor caso
	 */
	private void quickSort(Vehicle[] list, int start, int end) {
		if (start < end) {
			// Particionar el array y obtener posición del pivote
			int pivot = partition(list, start, end);
			// Ordenar recursivamente las dos mitades
			quickSort(list, start, pivot - 1);
			quickSort(list, pivot + 1, end);
		}
	}

	/**
	 * Busca la primera solicitud de una categoría específica en la cola normal.
	 * Desencola todos los elementos, busca el primero que coincida y vuelve a encolar el resto.
	 */
	private Request findRequestByCategory(int category) {
		domain.List.RequestQueue normalQueue = utils.colaNormal;
		domain.List.RequestQueue temp = new domain.List.RequestQueue();
		Request found = null;

		// Desencolar todos los elementos para buscar
		while (true) {
			Request req = normalQueue.dequeue();
			if (req == null) break;
			
			if (found == null && req.getClientCategory() == category) {
				found = req; // Encontramos la primera de esta categoría
			} else {
				temp.enqueue(req); // Guardar para devolver a la cola
			}
		}

		// Devolver todas las solicitudes no seleccionadas a la cola
		domain.List.RequestQueue tempAll = temp;
		for (Request req : tempAll.getAll()) {
			normalQueue.enqueue(req);
		}

		return found;
	}

	/**
	 * Método de partición para QuickSort.
	 * Reacomoda el array para que elementos mayores al pivote queden a la izquierda.
	 * CRITERIO DE ORDENAMIENTO:
	 * - Primario: Cantidad de servicios (descendente)
	 * - Secundario: ID del vehículo (alfabético)
	 */
	private int partition(Vehicle[] list, int start, int end) {
		Vehicle pivot = list[end]; // Usar último elemento como pivote
		int i = start - 1; // Índice del elemento menor

		// Recorrer array comparando con pivote
		for (int j = start; j < end; j++) {
			int cmp = Integer.compare(list[j].getServiceCount(), pivot.getServiceCount());
			// Si tiene más servicios O (mismos servicios pero ID alfabéticamente menor)
			if (cmp > 0 || (cmp == 0 && list[j].getId().compareTo(pivot.getId()) < 0)) {
				i++;
				// Intercambiar elementos
				Vehicle temp = list[i];
				list[i] = list[j];
				list[j] = temp;
			}
		}

		// Colocar el pivote en su posición final
		Vehicle temp = list[i + 1];
		list[i + 1] = list[end];
		list[end] = temp;

		return i + 1;
	}
}