package domain;
public class Vehiculo {
    public String id;
    public String zona;
    public boolean disponible;
    public int servicios;
    public double calificacion;

    public Vehiculo(String id, String zona) {
        this.id = id;
        this.zona = zona;
        this.disponible = true;
        this.servicios = 0;
        this.calificacion = 5.0;
    }

    public String toString() {
        return id + " - Zona: " + zona + " - Servicios: " + servicios;
    }
}
