package domain;

public class Servicio {
    static int contador = 1;
    public int id;
    public Solicitud solicitud;
    public Vehiculo vehiculo;
    public String ruta;
    public double costo;
    public String rutaVehiculoCliente;
    public String rutaClienteDestino;
    public String algoritmoDetalle;

    public Servicio(Solicitud solicitud, Vehiculo vehiculo, String ruta, double costo) {
        this.id = contador++;
        this.solicitud = solicitud;
        this.vehiculo = vehiculo;
        this.ruta = ruta;
        this.costo = costo;
        this.vehiculo.servicios++;
        this.vehiculo.disponible = false;
    }
}
