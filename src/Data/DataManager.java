package Data;
import java.io.*;
import domain.*;

public class DataManager {
    
    // Guardar vehículos
    public void guardarVehiculos(java.util.List<Vehiculo> vehiculos, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            for (Vehiculo v : vehiculos) {
                writer.println(v.id + "," + v.zona + "," + v.servicios + "," + v.disponible + "," + v.calificacion);
            }
        } catch (IOException e) {
            System.out.println("Error guardando vehículos: " + e.getMessage());
        }
    }

    public java.util.List<Vehiculo> cargarVehiculos(String archivo) {
        java.util.List<Vehiculo> lista = new java.util.ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length >= 3) {
                    Vehiculo v = new Vehiculo(partes[0], partes[1]);
                    v.servicios = Integer.parseInt(partes[2]);
                    if (partes.length >= 4) v.disponible = Boolean.parseBoolean(partes[3]);
                    if (partes.length >= 5) v.calificacion = Double.parseDouble(partes[4]);
                    lista.add(v);
                }
            }
        } catch (IOException e) {
            System.out.println("Error cargando vehículos: " + e.getMessage());
        }
        return lista;
    }
    
    // Guardar solicitudes
    public void guardarSolicitudes(java.util.List<Solicitud> solicitudes, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            for (Solicitud s : solicitudes) {
                writer.println(s.id + "," + s.cliente + "," + s.origen + "," + s.destino + "," + s.prioridad);
            }
        } catch (IOException e) {
            System.out.println("Error guardando solicitudes: " + e.getMessage());
        }
    }
    
    // Guardar servicios
    public void guardarServicios(java.util.List<Servicio> servicios, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            for (Servicio s : servicios) {
                writer.println(s.id + "," + s.solicitud.id + "," + s.vehiculo.id + "," + s.ruta + "," + s.costo);
            }
        } catch (IOException e) {
            System.out.println("Error guardando servicios: " + e.getMessage());
        }
    }
    
    // Guardar mapa (grafo)
    public void guardarMapa(Utils.Grafo grafo, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            var mapa = grafo.obtenerMapaAristas();
            for (var entrada : mapa.entrySet()) {
                String origen = entrada.getKey();
                for (var arista : entrada.getValue()) {
                    writer.println(origen + "," + arista.destino + "," + arista.distancia);
                }
            }
        } catch (IOException e) {
            System.out.println("Error guardando mapa: " + e.getMessage());
        }
    }
    
    // Cargar mapa (grafo)
    public void cargarMapa(Utils.Grafo grafo, String archivo) {
        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            java.util.Set<String> agregados = new java.util.HashSet<>();
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(",");
                if (partes.length >= 3) {
                    String clave = partes[0] + "-" + partes[1];
                    String claveInversa = partes[1] + "-" + partes[0];
                    if (!agregados.contains(clave) && !agregados.contains(claveInversa)) {
                        grafo.agregarConexion(partes[0], partes[1], Integer.parseInt(partes[2]));
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
        guardarVehiculos(utils.vehiculos.obtenerTodos(), "vehiculos.txt");
        guardarServicios(utils.servicios, "servicios.txt");
        guardarMapa(utils.mapa, "mapa.txt");
        System.out.println("Datos guardados exitosamente");
    }
    
    public void cargarTodo(Utils utils) {
        java.util.List<Vehiculo> vehiculos = cargarVehiculos("vehiculos.txt");
        for (Vehiculo v : vehiculos) {
            utils.vehiculos.agregar(v);
        }
        System.out.println("Datos cargados exitosamente");
    }
}
