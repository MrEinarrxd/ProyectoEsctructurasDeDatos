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
		initData();
	}

	private void initData() {
		dataManager.loadInitialData(utils, "datos_iniciales.txt");
	}

	public Graph getMap() {
		return utils.mapa;
	}

	public double getRate(String category) {
		return utils.tarifas.search(category);
	}

	public Request registerRequest(String client, String origin, String destination, int priority) {
		return registerRequest(client, origin, destination, priority, 1);
	}

	public Request registerRequest(String client, String origin, String destination, int priority, int clientCategory) {
		int randomNumber = (int)(Math.random() * 1000);
		String id = "REQ" + randomNumber;
		Request request = new Request(id, origin, destination, client, clientCategory);
		addRequest(request);
		String category = Utils.getCategoryName(clientCategory);
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
		var map = utils.mapa.obtenerMapaAristas();
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
		StringList history = utils.historialEventos.getAll();
		for (int i = 0; i < history.getSize(); i++) {
			result.add(history.get(i));
		}
		return result;
	}
	
	public void registerEvent(String event) {
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(new Date());
		utils.historialEventos.push("[" + timestamp + "] " + event);
	}


	private void addRequest(Request r) {
		if (r.getClientCategory() == 3) {
			utils.colaUrgente.enqueue(r, 4);
			utils.historialEventos.push("EMERGENCIA: " + r.getClientName() + " de " + r.getOrigin() + " a " + r.getDestination());
		} else {
			utils.colaNormal.enqueue(r);
			String category = Utils.getCategoryName(r.getClientCategory());
			utils.historialEventos.push(category.toUpperCase() + ": " + r.getClientName() + " de " + r.getOrigin() + " a " + r.getDestination());
		}
	}

	// Procesa siguiente solicitud por prioridad: Emergencia > VIP > Regular > Económico
	// Asigna vehículo disponible más cercano y calcula ruta óptima con Dijkstra
	private Service processNext() {
		Request request = utils.colaUrgente.dequeue();
		
		if (request == null) {
			request = findRequestByCategory(2);
		}
		if (request == null) {
			request = findRequestByCategory(1);
		}
		if (request == null) {
			request = findRequestByCategory(0);
		}

		if (request == null) {
			utils.historialEventos.push("No hay solicitudes pendientes");
			return null;
		}

		Vehicle vehicle = assignVehicleGreedy(request.getOrigin());
		if (vehicle == null) {
			utils.historialEventos.push("ERROR: No hay vehiculos para " + request.getOrigin());
			return null;
		}

		Path vehicleRoute = utils.mapa.dijkstra(vehicle.getCurrentZone(), request.getOrigin());
		Path clientRoute = utils.mapa.dijkstra(request.getOrigin(), request.getDestination());

		int totalDistance = vehicleRoute.distance + clientRoute.distance;

		double baseRate = utils.tarifas.search("basica");
		if (baseRate == 0) baseRate = 10.0;
		double cost = baseRate * totalDistance;

		Service service = new Service(request, vehicle,
			request.getOrigin() + "->" + request.getDestination(), cost);

		service.vehicleToClientRoute = String.join(" -> ", vehicleRoute.path.toArray());
		service.clientToDestinationRoute = String.join(" -> ", clientRoute.path.toArray());
		service.algorithmDetail = clientRoute.algorithmDetail;

		utils.servicios.add(service);
		utils.historialEventos.push("SERVICIO #" + service.id + " creado: $" + cost);

		vehicle.setAvailable(true);
		return service;
	}

	private Vehicle assignVehicleGreedy(String zone) {
		Vehicle v = utils.vehiculos.buscarDisponible(zone);
		if (v != null) return v;

		int size = utils.vehiculos.getSize();
		for (int i = 0; i < size; i++) {
			Vehicle vehicle = utils.vehiculos.get(i);
			if (vehicle.isAvailable()) {
				return vehicle;
			}
		}
		return null;
	}

	private void quickSort(Vehicle[] list, int start, int end) {
		if (start < end) {
			int pivot = partition(list, start, end);
			quickSort(list, start, pivot - 1);
			quickSort(list, pivot + 1, end);
		}
	}

	private Request findRequestByCategory(int category) {
		RequestQueue normalQueue = utils.colaNormal;
		RequestQueue temp = new RequestQueue();
		Request found = null;

		while (true) {
			Request req = normalQueue.dequeue();
			if (req == null) break;
			
			if (found == null && req.getClientCategory() == category) {
				found = req;
			} else {
				temp.enqueue(req);
			}
		}

		for (int i = 0; i < temp.getSize(); i++) {
			Request req = temp.get(i);
			normalQueue.enqueue(req);
		}

		return found;
	}

	private int partition(Vehicle[] list, int start, int end) {
		Vehicle pivot = list[end];
		int i = start - 1;

		for (int j = start; j < end; j++) {
			int cmp = Integer.compare(list[j].getServiceCount(), pivot.getServiceCount());
			if (cmp > 0 || (cmp == 0 && list[j].getId().compareTo(pivot.getId()) < 0)) {
				i++;
				Vehicle temp = list[i];
				list[i] = list[j];
				list[j] = temp;
			}
		}

		Vehicle temp = list[i + 1];
		list[i + 1] = list[end];
		list[end] = temp;

		return i + 1;
	}
}