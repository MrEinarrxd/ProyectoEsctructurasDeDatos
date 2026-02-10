package Data;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import domain.*;
import domain.Graphs.Edge;
import domain.Graphs.Graph;
import domain.List.VehicleList;
import domain.List.StringList;

public class DataManager {
    
    /**
     * Guarda la lista de vehículos en un archivo CSV.
     * Formato: ID,Zona,CantServicios,Disponible,Conductor,Tipo
     */
    public void saveVehicles(VehicleList vehicles, String file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Vehicle v : vehicles.getAll()) {
                writer.println(v.getId() + "," + v.getCurrentZone() + "," + v.getServiceCount() + "," + v.isAvailable() + "," + v.getDriverName() + "," + v.getVehicleType());
            }
        } catch (IOException e) {
            System.out.println("Error saving vehicles: " + e.getMessage());
        }
    }

    /**
     * Carga vehículos desde un archivo CSV.
     * Reconstruye objetos Vehicle con todos sus atributos.
     */
    public VehicleList loadVehicles(String file) {
        VehicleList list = new VehicleList();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String driverName = parts.length >= 5 ? parts[4] : "Driver";
                    String vehicleType = parts.length >= 6 ? parts[5] : "sedan";
                    Vehicle v = new Vehicle(parts[0], driverName, parts[1], vehicleType);
                    // Set serviceCount using incrementServiceCount multiple times
                    int serviceCount = Integer.parseInt(parts[2]);
                    for (int i = 0; i < serviceCount; i++) {
                        v.incrementServiceCount();
                    }
                    if (parts.length >= 4) v.setAvailable(Boolean.parseBoolean(parts[3]));
                    list.add(v);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
        return list;
    }
    
    // Save requests
    public void saveRequests(domain.List.RequestQueue requests, String file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Request s : requests.getAll()) {
                writer.println(s.getId() + "," + s.getClientName() + "," + s.getOrigin() + "," + s.getDestination() + "," + s.getClientCategory());
            }
        } catch (IOException e) {
            System.out.println("Error saving requests: " + e.getMessage());
        }
    }
    
    // Save services
    public void saveServices(domain.List.ServiceList services, String file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (Service s : services.getAll()) {
                writer.println(s.id + "," + s.request.getId() + "," + s.vehicle.getId() + "," + s.route + "," + s.cost);
            }
        } catch (IOException e) {
            System.out.println("Error saving services: " + e.getMessage());
        }
    }
    
    /**
     * Guarda el grafo (mapa de zonas) en un archivo CSV.
     * Formato: Origen,Destino,Peso
     */
    public void saveMap(Graph graph, String file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            var map = graph.getEdgeMap();
            for (var entry : map.entrySet()) {
                String origin = entry.getKey();
                for (Edge edge : entry.getValue()) {
                    writer.println(origin + "," + edge.getTo() + "," + edge.getWeight());
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving map: " + e.getMessage());
        }
    }
    
    /**
     * Carga el grafo desde un archivo CSV.
     * Evita duplicar aristas (A-B y B-A son la misma arista en grafo no dirigido).
     */
    public void loadMap(Graph graph, String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringList added = new StringList();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String key = parts[0] + "-" + parts[1];
                    String reverseKey = parts[1] + "-" + parts[0];
                    if (!added.contains(key) && !added.contains(reverseKey)) {
                        graph.addEdge(parts[0], parts[1], Integer.parseInt(parts[2]));
                        added.add(key);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading map: " + e.getMessage());
        }
    }
    
    // Convenience methods with default names
    public void saveAll(Utils utils) {
        saveVehicles(utils.vehiculos, "vehiculos.txt");
        System.out.println("Data saved successfully");
    }
    
    public void loadAll(Utils utils) {
        VehicleList vehicles = loadVehicles("vehiculos.txt");
        for (Vehicle v : vehicles.getAll()) {
            utils.vehiculos.add(v);
        }
        System.out.println("Data loaded successfully");
    }
    
    // Save history
    public void saveHistory(StringList historyEvents, String file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (String event : historyEvents.getAll()) {
                writer.println(event);
            }
        } catch (IOException e) {
            System.out.println("Error saving history: " + e.getMessage());
        }
    }
    
    // Load history
    public List<String> loadHistory(String file) {
        List<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error loading history: " + e.getMessage());
        }
        return list;
    }
    
    /**
     * Carga datos iniciales del sistema desde archivo de configuración.
     * FORMATO DEL ARCHIVO:
     * - Secciones separadas por líneas ===NOMBRE_SECCION===
     * - Secciones soportadas: ZONAS, VEHICULOS, CATEGORIAS, TARIFAS
     * - ZONAS: Origen,Destino,Peso (aristas del grafo)
     * - VEHICULOS: ID,Conductor,ZonaActual,TipoVehiculo
     * - CATEGORIAS: Nombre,Factor (no usado actualmente)
     * - TARIFAS: Nombre,Valor (árbol de tarifas)
     */
    public void loadInitialData(Utils utils, String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String section = "";
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                // Ignore empty lines
                if (line.isEmpty()) continue;
                
                // Detect sections
                if (line.startsWith("===")) {
                    section = line.replace("===", "").trim();
                    continue;
                }
                
                // Process according to section
                if (section.contains("VEHÍCULOS")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String id = parts[0].trim();
                        String driverName = parts[1].trim();
                        String zone = parts[2].trim();
                        String vehicleType = parts[3].trim();
                        utils.vehiculos.add(new Vehicle(id, driverName, zone, vehicleType));
                    }
                }
                else if (section.contains("MAPA")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 3) {
                        String origin = parts[0].trim();
                        String destination = parts[1].trim();
                        int weight = Integer.parseInt(parts[2].trim());
                        utils.mapa.addEdge(origin, destination, weight);
                    }
                }
                else if (section.contains("TARIFAS")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        String category = parts[0].trim();
                        double price = Double.parseDouble(parts[1].trim());
                        utils.tarifas.add(category, price);
                    }
                }
                else if (section.contains("SOLICITUDES")) {
                    String[] parts = line.split(",");
                    if (parts.length >= 5) {
                        String id = parts[0].trim();
                        String client = parts[1].trim();
                        String origin = parts[2].trim();
                        String destination = parts[3].trim();
                        int category = Integer.parseInt(parts[4].trim());
                        Request request = new Request(id, origin, destination, client, category);
                        if (request.getClientCategory() == 3) {
                            utils.colaUrgente.enqueue(request, 4);
                            utils.historialEventos.push("EMERGENCIA: " + request.getClientName() + " de " + request.getOrigin() + " a " + request.getDestination());
                        } else {
                            utils.colaNormal.enqueue(request);
                            String[] categories = {"ECONÓMICO", "REGULAR", "VIP", "EMERGENCIA"};
                            String categoryName = category >= 0 && category < 4 ? categories[category] : "DESCONOCIDO";
                            utils.historialEventos.push(categoryName + ": " + request.getClientName() + " de " + request.getOrigin() + " a " + request.getDestination());
                        }
                    }
                }
            }
            System.out.println("Initial data loaded from " + file);
        } catch (IOException e) {
            System.out.println("Error loading initial data: " + e.getMessage());
        }
    }
}