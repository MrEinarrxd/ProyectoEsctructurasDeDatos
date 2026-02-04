package Data;
import java.io.*;

import domain.Vehiculo;

public class DataManager {
    public void guardarVehiculos(java.util.List<Vehiculo> vehiculos, String archivo) {
        try (PrintWriter writer = new PrintWriter(archivo)) {
            for (Vehiculo v : vehiculos) {
                writer.println(v.id + "," + v.zona + "," + v.servicios);
            }
        } catch (IOException e) {
            System.out.println("Error guardando: " + e.getMessage());
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
                    lista.add(v);
                }
            }
        } catch (IOException e) {
            System.out.println("Error cargando: " + e.getMessage());
        }
        return lista;
    }
}
