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
    
    // Guardar vehículos
    public void guardarVehiculos(VehicleList vehiculos, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            for (Vehicle v : vehiculos.obtenerTodos()) {
                writer.println(v.getId() + "," + v.getCurrentZone() + "," + v.getServiceCount() + "," + v.isAvailable() + "," + v.getDriverName() + "," + v.getVehicleType());
            }
        } catch (IOException e) {
            System.out.println("Error guardando vehículos: " + e.getMessage());
        }
    }

    public VehicleList cargarVehiculos(String archivo) {
        VehicleList lista = new VehicleList();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length >= 3) {
                    String driverName = partes.length >= 5 ? partes[4] : "Driver";
                    String vehicleType = partes.length >= 6 ? partes[5] : "sedan";
                    Vehicle v = new Vehicle(partes[0], driverName, partes[1], vehicleType);
                    // Establecer serviceCount usando incrementServiceCount varias veces
                    int serviceCount = Integer.parseInt(partes[2]);
                    for (int i = 0; i < serviceCount; i++) {
                        v.incrementServiceCount();
                    }
                    if (partes.length >= 4) v.setAvailable(Boolean.parseBoolean(partes[3]));
                    lista.add(v);
                }
            }
        } catch (IOException e) {
            System.out.println("Error cargando vehículos: " + e.getMessage());
        }
        return lista;
    }
    
    // Guardar solicitudes
    public void guardarSolicitudes(domain.List.RequestQueue solicitudes, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            for (Request s : solicitudes.getAll()) {
                writer.println(s.getId() + "," + s.getClientName() + "," + s.getOrigin() + "," + s.getDestination() + "," + s.getPriority());
            }
        } catch (IOException e) {
            System.out.println("Error guardando solicitudes: " + e.getMessage());
        }
    }
    
    // Guardar servicios
    public void guardarServicios(domain.List.ServiceList servicios, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            for (Service s : servicios.getAll()) {
                writer.println(s.id + "," + s.request.getId() + "," + s.vehicle.getId() + "," + s.route + "," + s.cost);
            }
        } catch (IOException e) {
            System.out.println("Error guardando servicios: " + e.getMessage());
        }
    }
    
    // Guardar mapa (grafo)
    public void guardarMapa(Graph grafo, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            var mapa = grafo.obtenerMapaAristas();
            for (var entrada : mapa.entrySet()) {
                String origen = entrada.getKey();
                for (Edge arista : entrada.getValue()) {
                    writer.println(origen + "," + arista.getTo() + "," + arista.getWeight());
                }
            }
        } catch (IOException e) {
            System.out.println("Error guardando mapa: " + e.getMessage());
        }
    }
    
    // Cargar mapa (grafo)
    public void cargarMapa(Graph grafo, String archivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            StringList agregados = new StringList();
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length >= 3) {
                    String clave = partes[0] + "-" + partes[1];
                    String claveInversa = partes[1] + "-" + partes[0];
                    if (!agregados.contains(clave) && !agregados.contains(claveInversa)) {
                        grafo.addEdge(partes[0], partes[1], Integer.parseInt(partes[2]));
                        agregados.add(clave);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error cargando mapa: " + e.getMessage());
        }
    }
    
    // Métodos de conveniencia con nombres por defecto
    public void guardarTodo(Utils utils) {
        try (PrintWriter writer = new PrintWriter("vehiculos.txt")) {
            for (Vehicle v : utils.vehiculos.obtenerTodos()) {
                writer.println(v.getId() + "," + v.getCurrentZone() + "," + v.getServiceCount() + "," + v.isAvailable() + "," + v.getDriverName() + "," + v.getVehicleType());
            }
        } catch (IOException e) {
            System.out.println("Error guardando vehículos: " + e.getMessage());
        }
        System.out.println("Datos guardados exitosamente");
    }
    
    public void cargarTodo(Utils utils) {
        VehicleList vehiculos = cargarVehiculos("vehiculos.txt");
        for (Vehicle v : vehiculos.obtenerTodos()) {
            utils.vehiculos.agregar(v);
        }
        System.out.println("Datos cargados exitosamente");
    }
    
    // Guardar historial
    public void guardarHistorial(StringList historialEventos, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            for (String evento : historialEventos.getAll()) {
                writer.println(evento);
            }
        } catch (IOException e) {
            System.out.println("Error guardando historial: " + e.getMessage());
        }
    }
    
    // Cargar historial
    public List<String> cargarHistorial(String archivo) {
        List<String> lista = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                lista.add(linea);
            }
        } catch (IOException e) {
            System.out.println("Error cargando historial: " + e.getMessage());
        }
        return lista;
    }
    
    // Cargar datos iniciales desde archivo de configuración
    public void cargarDatosIniciales(Utils utils, String archivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            String seccion = "";
            
            while ((linea = reader.readLine()) != null) {
                linea = linea.trim();
                
                // Ignorar líneas vacías
                if (linea.isEmpty()) continue;
                
                // Detectar secciones
                if (linea.startsWith("===")) {
                    seccion = linea.replace("===", "").trim();
                    continue;
                }
                
                // Procesar según la sección
                if (seccion.contains("VEHÍCULOS")) {
                    String[] partes = linea.split(",");
                    if (partes.length >= 4) {
                        String id = partes[0].trim();
                        String driverName = partes[1].trim();
                        String zona = partes[2].trim();
                        String vehicleType = partes[3].trim();
                        utils.vehiculos.agregar(new Vehicle(id, driverName, zona, vehicleType));
                    }
                }
                else if (seccion.contains("MAPA")) {
                    String[] partes = linea.split(",");
                    if (partes.length >= 3) {
                        String origen = partes[0].trim();
                        String destino = partes[1].trim();
                        int peso = Integer.parseInt(partes[2].trim());
                        utils.mapa.addEdge(origen, destino, peso);
                    }
                }
                else if (seccion.contains("TARIFAS")) {
                    String[] partes = linea.split(",");
                    if (partes.length >= 2) {
                        String categoria = partes[0].trim();
                        double precio = Double.parseDouble(partes[1].trim());
                        utils.tarifas.add(categoria, precio);
                    }
                }
                else if (seccion.contains("SOLICITUDES")) {
                    String[] partes = linea.split(",");
                    if (partes.length >= 5) {
                        String id = partes[0].trim();
                        String cliente = partes[1].trim();
                        String origen = partes[2].trim();
                        String destino = partes[3].trim();
                        int prioridad = Integer.parseInt(partes[4].trim());
                        Request solicitud = new Request(id, origen, destino, cliente, prioridad);
                        if (solicitud.getPriority() >= 3) {
                            utils.colaUrgente.enqueue(solicitud, solicitud.getPriority());
                            utils.historialEventos.push("URGENTE: " + solicitud.getClientName() + " de " + solicitud.getOrigin() + " a " + solicitud.getDestination());
                        } else {
                            utils.colaNormal.enqueue(solicitud);
                            utils.historialEventos.push("NORMAL: " + solicitud.getClientName() + " de " + solicitud.getOrigin() + " a " + solicitud.getDestination());
                        }
                    }
                }
            }
            System.out.println("Datos iniciales cargados desde " + archivo);
        } catch (IOException e) {
            System.out.println("Error cargando datos iniciales: " + e.getMessage());
        }
    }
}