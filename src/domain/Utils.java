package domain;
import domain.List.RequestQueue;
import domain.List.RequestPriorityQueue;
import domain.List.StringList;
import domain.List.ServiceList;
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
}
