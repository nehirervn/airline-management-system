package Flight;

import java.io.Serializable;
import java.time.*;

public class Flight implements Serializable {
    private static final long serialVersionUID = 1L;

    private String flightNum;
    private Route route;
    private LocalDate date;
    private LocalTime hour;
    private int durationMinutes;
    private String arrivalPlace;
    private String departurePlace;
    private Plane plane;
    private double price; 

    public Flight(String flightNum, Route route, LocalDate date, LocalTime hour, int durationMinutes,
                   String departurePlace, String arrivalPlace, Plane plane) {
        
        setFlightNum(flightNum);
        setRoute(route);
        setDate(date);
        setHour(hour);
        setDurationMinutes(durationMinutes);
        setDeparturePlace(departurePlace);
        setArrivalPlace(arrivalPlace);
        setPlane(plane);
        this.price = 0.0; // Sonrasında set edilecek.
    }


    public String getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(String flightNum) {
        if (flightNum == null || flightNum.trim().isEmpty()) {
            throw new IllegalArgumentException("HATA: Uçuş numarası boş olamaz.");
        }
        this.flightNum = flightNum;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        if (route == null) {
            throw new IllegalArgumentException("HATA: Uçuş rotası (Route) boş olamaz.");
        }
        this.route = route;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("HATA: Uçuş tarihi boş olamaz.");
        }
        this.date = date;
    }

    public LocalTime getHour() {
        return hour;
    }

    public void setHour(LocalTime hour) {
        if (hour == null) {
            throw new IllegalArgumentException("HATA: Uçuş saati boş olamaz.");
        }
        this.hour = hour;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("HATA: Uçuş süresi 0 veya negatif olamaz: " + durationMinutes);
        }
        this.durationMinutes = durationMinutes;
    }

    public String getDeparturePlace() {
        return departurePlace;
    }

    public void setDeparturePlace(String departurePlace) {
        if (departurePlace == null || departurePlace.trim().isEmpty()) {
            throw new IllegalArgumentException("HATA: Kalkış yeri boş olamaz.");
        }
        this.departurePlace = departurePlace;
    }

    public String getArrivalPlace() {
        return arrivalPlace;
    }

    public void setArrivalPlace(String arrivalPlace) {
        if (arrivalPlace == null || arrivalPlace.trim().isEmpty()) {
            throw new IllegalArgumentException("HATA: Varış yeri boş olamaz.");
        }
        this.arrivalPlace = arrivalPlace;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane plane) {
        if (plane == null) {
            throw new IllegalArgumentException("HATA: Uçuşa bir uçak atanmalıdır.");
        }
        this.plane = plane;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("HATA: Uçuş fiyatı negatif olamaz.");
        }
        this.price = price;
    }

    public String getDetails() {
         return flightNum + " - " + (route != null ? route.toString() : "Rota Yok") + 
                " - " + date + " " + hour + " (" + durationMinutes + " dk)";
    }
    
    @Override
    public String toString() { 
        return "Flight{" + getDetails() + ", Price=" + price + "}"; 
    }
}