package Data;
import java.io.*;
import domain.*;
import domain.Graphs.Edge;
import domain.Graphs.Graph;
import domain.List.VehicleList;
import domain.List.StringList;
import domain.List.RequestQueue;
import domain.List.ServiceList;

public class DataManager {
    
    
    public void saveVehicles(VehicleList vehicles, String file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            VehicleList allVehicles = vehicles.getAll();
            for (int i = 0; i < allVehicles.getSize(); i++) {
                Vehicle v = allVehicles.get(i);
                writer.println(v.getId() + "," + v.getCurrentZone() + "," + v.getServiceCount() + "," + v.isAvailable() + "," + v.getDriverName() + "," + v.getVehicleType());
            }
        } catch (IOException e) {
            System.out.println("Error guardando vehículos: " + e.getMessage());
        }
    }

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
                    int serviceCount = Integer.parseInt(parts[2]);
                    for (int i = 0; i < serviceCount; i++) {
                        v.incrementServiceCount();
                    }
                    if (parts.length >= 4) v.setAvailable(Boolean.parseBoolean(parts[3]));
                    list.add(v);
                }
            }
        } catch (IOException e) {
            System.out.println("Error cargando vehículos: " + e.getMessage());
        }
        return list;
    }
    
    public void saveRequests(RequestQueue requests, String file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            RequestQueue queue = requests.getAll();
            for (int i = 0; i < queue.getSize(); i++) {
                Request s = queue.get(i);
                writer.println(s.getId() + "," + s.getClientName() + "," + s.getOrigin() + "," + s.getDestination() + "," + s.getClientCategory());
            }
        } catch (IOException e) {
            System.out.println("Error guardando solicitudes: " + e.getMessage());
        }
    }
    
    public void saveServices(ServiceList services, String file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            ServiceList serviceList = services.getAll();
            for (int i = 0; i < serviceList.getSize(); i++) {
                Service s = serviceList.get(i);
                writer.println(s.id + "," + s.request.getId() + "," + s.vehicle.getId() + "," + s.route + "," + s.cost);
            }
        } catch (IOException e) {
            System.out.println("Error guardando servicios: " + e.getMessage());
        }
    }
    
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
            System.out.println("Error guardando mapa: " + e.getMessage());
        }
    }
    
    public void loadMap(Graph graph, String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            StringList added = new StringList();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String key = parts[0] + "-" + parts[1];
                    String inverseKey = parts[1] + "-" + parts[0];
                    if (!added.contains(key) && !added.contains(inverseKey)) {
                        graph.addEdge(parts[0], parts[1], Integer.parseInt(parts[2]));
                        added.add(key);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error cargando mapa: " + e.getMessage());
        }
    }
    
    public void saveAll(Utils utils) {
        try (PrintWriter writer = new PrintWriter("vehiculos.txt")) {
            VehicleList allVehicles = utils.vehiculos.getAll();
            for (int i = 0; i < allVehicles.getSize(); i++) {
                Vehicle v = allVehicles.get(i);
                writer.println(v.getId() + "," + v.getCurrentZone() + "," + v.getServiceCount() + "," + v.isAvailable() + "," + v.getDriverName() + "," + v.getVehicleType());
            }
        } catch (IOException e) {
            System.out.println("Error guardando vehículos: " + e.getMessage());
        }
        System.out.println("Datos guardados exitosamente");
    }
    
    public void loadAll(Utils utils) {
        VehicleList vehicles = loadVehicles("vehiculos.txt");
        VehicleList allVehicles = vehicles.getAll();
        for (int i = 0; i < allVehicles.getSize(); i++) {
            Vehicle v = allVehicles.get(i);
            utils.vehiculos.add(v);
        }
        System.out.println("Datos cargados exitosamente");
    }
    
    public void saveHistory(StringList events, String file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            for (int i = 0; i < events.getSize(); i++) {
                writer.println(events.get(i));
            }
        } catch (IOException e) {
            System.out.println("Error guardando historial: " + e.getMessage());
        }
    }
    
    public StringList loadHistory(String file) {
        StringList list = new StringList();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error cargando historial: " + e.getMessage());
        }
        return list;
    }
    
    // Cargar datos iniciales desde archivo de configuración
    public void loadInitialData(Utils utils, String file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String section = "";
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                
                if (line.isEmpty()) continue;
                
                if (line.startsWith("===")) {
                    section = line.replace("===", "").trim();
                    continue;
                }
                
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
                            String categoryName = Utils.getCategoryName(category);
                            utils.historialEventos.push(categoryName.toUpperCase() + ": " + request.getClientName() + " de " + request.getOrigin() + " a " + request.getDestination());
                        }
                    }
                }
            }
            System.out.println("Datos iniciales cargados desde " + file);
        } catch (IOException e) {
            System.out.println("Error cargando datos iniciales: " + e.getMessage());
        }
    }
}