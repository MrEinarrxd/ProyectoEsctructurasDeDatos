package Data;
import java.io.*;
import domain.*;
import domain.Graphs.Edge;
import domain.Graphs.Graph;
import domain.List.VehicleList;
import domain.List.StringList;

public class DataManager {
    
    private String obtenerNombreCategoria(int categoria) {
        switch(categoria) {
            case 0: return "Económico";
            case 1: return "Regular";
            case 2: return "VIP";
            case 3: return "Emergencia";
            default: return "Desconocido";
        }
    }
    
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
    
    public void guardarSolicitudes(domain.List.RequestQueue solicitudes, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            domain.List.RequestQueue queue = solicitudes.getAll();
            for (int i = 0; i < queue.getSize(); i++) {
                Request s = queue.get(i);
                writer.println(s.getId() + "," + s.getClientName() + "," + s.getOrigin() + "," + s.getDestination() + "," + s.getClientCategory());
            }
        } catch (IOException e) {
            System.out.println("Error guardando solicitudes: " + e.getMessage());
        }
    }
    
    public void guardarServicios(domain.List.ServiceList servicios, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            domain.List.ServiceList serviceList = servicios.getAll();
            for (int i = 0; i < serviceList.getSize(); i++) {
                Service s = serviceList.get(i);
                writer.println(s.id + "," + s.request.getId() + "," + s.vehicle.getId() + "," + s.route + "," + s.cost);
            }
        } catch (IOException e) {
            System.out.println("Error guardando servicios: " + e.getMessage());
        }
    }
    
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
            utils.vehiculos.add(v);
        }
        System.out.println("Datos cargados exitosamente");
    }
    
    public void guardarHistorial(StringList historialEventos, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            for (int i = 0; i < historialEventos.getSize(); i++) {
                writer.println(historialEventos.get(i));
            }
        } catch (IOException e) {
            System.out.println("Error guardando historial: " + e.getMessage());
        }
    }
    
    public StringList cargarHistorial(String archivo) {
        StringList lista = new StringList();
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
                
                if (linea.isEmpty()) continue;
                
                if (linea.startsWith("===")) {
                    seccion = linea.replace("===", "").trim();
                    continue;
                }
                
                if (seccion.contains("VEHÍCULOS")) {
                    String[] partes = linea.split(",");
                    if (partes.length >= 4) {
                        String id = partes[0].trim();
                        String driverName = partes[1].trim();
                        String zona = partes[2].trim();
                        String vehicleType = partes[3].trim();
                        utils.vehiculos.add(new Vehicle(id, driverName, zona, vehicleType));
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
                        int categoria = Integer.parseInt(partes[4].trim());
                        Request solicitud = new Request(id, origen, destino, cliente, categoria);
                        if (solicitud.getClientCategory() == 3) {
                            utils.colaUrgente.enqueue(solicitud, 4);
                            utils.historialEventos.push("EMERGENCIA: " + solicitud.getClientName() + " de " + solicitud.getOrigin() + " a " + solicitud.getDestination());
                        } else {
                            utils.colaNormal.enqueue(solicitud);
                            String categoriaNombre = obtenerNombreCategoria(categoria);
                            utils.historialEventos.push(categoriaNombre.toUpperCase() + ": " + solicitud.getClientName() + " de " + solicitud.getOrigin() + " a " + solicitud.getDestination());
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