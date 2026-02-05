package Flight;

import java.io.Serializable;

public class Route implements Serializable {
    private static final long serialVersionUID = 1L;

    private String departureAirport;
    private String arrivalAirport;
    private double distance;
    
    
    public Route(String departureAirport, String arrivalAirport, double distance) {
        this.departureAirport = departureAirport;
        this.arrivalAirport = arrivalAirport;
        setDistance(distance); // Atamayı setter üzerinden güvenli şekilde yapıyoruz.
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(String departureAirport) {
        this.departureAirport = departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(String arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("Rota mesafesi negatif olamaz: " + distance);
        }
        this.distance = distance;
    }

    public String getRouteInfo() {
        return " (" + departureAirport + ") -> " + " (" + arrivalAirport + ")";
    }

    @Override
    public String toString() {
        return departureAirport + " -> " + arrivalAirport + " (" + distance + " km)";
    }
}