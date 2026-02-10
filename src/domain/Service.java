package domain;

public class Service {
    private static int counter = 1;
    public final int id;
    public final Request request;
    public final Vehicle vehicle;
    public final String route;
    public final double cost;
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
