package domain;

public class Service {
    static int counter = 1;
    public int id;
    public Request request;
    public Vehicle vehicle;
    public String route;
    public double cost;
    public String vehicleToClientRoute;
    public String clientToDestinationRoute;
    public String algorithmDetail;

    public Service(Request request, Vehicle vehicle, String route, double cost) {
        this.id = counter++;
        this.request = request;
        this.vehicle = vehicle;
        this.route = route;
        this.cost = cost;
        this.vehicle.incrementServiceCount();
        this.vehicle.setAvailable(false);
    }
}
