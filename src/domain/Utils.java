package domain;
import java.util.List;
import domain.List.RequestQueue;
import domain.List.RequestPriorityQueue;
import domain.List.StringList;
import domain.List.ServiceList;
import domain.Graphs.PathResult;
import domain.Graphs.Graph;
import domain.List.VehicleList;

public class Utils {
    public RequestQueue colaNormal = new RequestQueue();
    public RequestPriorityQueue colaUrgente = new RequestPriorityQueue();
    public StringList historialEventos = new StringList();
    public VehicleList vehiculos = new VehicleList();
    public Graph mapa = new Graph();
    public RateTree tarifas = new RateTree();
    public ServiceList servicios = new ServiceList();

    // ========== ALGORITMOS ==========

    // Bubble sort
    public void ordenarBurbuja(java.util.List<Vehicle> lista) {
        int n = lista.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (lista.get(j).getServiceCount() < lista.get(j + 1).getServiceCount()) {
                    Vehicle temp = lista.get(j);
                    lista.set(j, lista.get(j + 1));
                    lista.set(j + 1, temp);
                }
            }
        }
    }

    // Quick sort
    public void ordenarRapido(List<Vehicle> lista, int inicio, int fin) {
        if (inicio < fin) {
            int pivote = particionar(lista, inicio, fin);
            ordenarRapido(lista, inicio, pivote - 1);
            ordenarRapido(lista, pivote + 1, fin);
        }
    }

    private int particionar(List<Vehicle> lista, int inicio, int fin) {
        Vehicle pivote = lista.get(fin);
        int i = inicio - 1;

        for (int j = inicio; j < fin; j++) {
            if (lista.get(j).getServiceCount() >= pivote.getServiceCount()) {
                i++;
                Vehicle temp = lista.get(i);
                lista.set(i, lista.get(j));
                lista.set(j, temp);
            }
        }

        Vehicle temp = lista.get(i + 1);
        lista.set(i + 1, lista.get(fin));
        lista.set(fin, temp);

        return i + 1;
    }

    // Greedy
    public Vehicle asignarVehiculoGreedy(String zona) {
        Vehicle v = vehiculos.buscarDisponible(zona);
        if (v != null) return v;

        List<Vehicle> todos = vehiculos.obtenerTodos();
        for (Vehicle vehiculo : todos) {
            if (vehiculo.isAvailable()) {
                return vehiculo;
            }
        }
        return null;
    }

    // ========== METODOS PRINCIPALES ==========
    public void agregarSolicitud(Request s) {
        if (s.getPriority() >= 3) {
            colaUrgente.enqueue(s, s.getPriority());
            historialEventos.add("URGENTE: " + s.getClientName() + " de " + s.getOrigin() + " a " + s.getDestination());
        } else {
            colaNormal.enqueue(s);
            historialEventos.add("NORMAL: " + s.getClientName() + " de " + s.getOrigin() + " a " + s.getDestination());
        }
    }

    public Service procesarSiguiente() {
        Request solicitud = colaUrgente.dequeue();
        if (solicitud == null) {
            solicitud = colaNormal.dequeue();
        }

        if (solicitud == null) {
            historialEventos.add("No hay solicitudes pendientes");
            return null;
        }

        Vehicle vehiculo = asignarVehiculoGreedy(solicitud.getOrigin());
        if (vehiculo == null) {
            historialEventos.add("ERROR: No hay vehiculos para " + solicitud.getOrigin());
            return null;
        }

        // Calcular ruta detallada desde vehículo hasta cliente
        PathResult rutaVehiculo = mapa.calcularRutaDijkstra(vehiculo.getCurrentZone(), solicitud.getOrigin());
        
        // Calcular ruta detallada desde cliente hasta destino
        PathResult rutaCliente = mapa.calcularRutaDijkstra(solicitud.getOrigin(), solicitud.getDestination());
        
        int distanciaTotal = rutaVehiculo.distanciaTotal + rutaCliente.distanciaTotal;

        double tarifaBase = tarifas.search("basica");
        if (tarifaBase == 0) tarifaBase = 10.0;
        double costo = tarifaBase * distanciaTotal;

        Service servicio = new Service(solicitud, vehiculo,
            solicitud.getOrigin() + "->" + solicitud.getDestination(), costo);
        
        // Agregar información detallada de las rutas de forma simplificada
        servicio.vehicleToClientRoute = String.join(" -> ", rutaVehiculo.camino.toArray());
        servicio.clientToDestinationRoute = String.join(" -> ", rutaCliente.camino.toArray());
        servicio.algorithmDetail = rutaCliente.detalleAlgoritmo;

        servicios.add(servicio);
        historialEventos.add("SERVICIO #" + servicio.id + " creado: $" + costo);

        return servicio;
    }
}
