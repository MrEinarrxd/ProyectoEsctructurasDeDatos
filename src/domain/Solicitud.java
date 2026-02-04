package domain;
public class Solicitud {
    static int contador = 1;
    public int id;
    public String cliente;
    public String origen;
    public String destino;
    public int prioridad; // 1=baja, 2=media, 3=alta, 4=emergencia

    public Solicitud(String cliente, String origen, String destino, int prioridad) {
        this.id = contador++;
        this.cliente = cliente;
        this.origen = origen;
        this.destino = destino;
        this.prioridad = prioridad;
    }

    public boolean esUrgente() {
        return prioridad >= 3;
    }
}
