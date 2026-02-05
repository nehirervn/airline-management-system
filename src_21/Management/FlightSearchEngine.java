package Management;

import Flight.Flight;
import Flight.Seat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FlightSearchEngine {

    private FlightManager flightManager;
    private SeatManager seatManager;

    public FlightSearchEngine(FlightManager flightManager, SeatManager seatManager) {
        this.flightManager = flightManager;
        this.seatManager = seatManager;
    }

    /*
     * - selectedDate NULL ise: Şimdiki zamandan sonraki HER ŞEYİ getirir.
     * - selectedDate VAR ise: O tarihten SONRAKİ (o gün dahil) uçuşları getirir.
     */
    public List<Flight> searchFlights(String departure, String arrival, LocalDate selectedDate) {
        List<Flight> result = new ArrayList<>();

        if (flightManager != null && flightManager.getAllFlights() != null) {
            
        	//Zaman olarak şimdiki zaman kullanılıyor now ile
            LocalDateTime now = LocalDateTime.now();

            for (Flight flight : flightManager.getAllFlights()) {
                if (flight != null) {
                    
                    String dep = flight.getDeparturePlace(); //
                    String arr = flight.getArrivalPlace();   //

                    // Rota kontrol edilir.
                    if (dep != null && arr != null && 
                        dep.equalsIgnoreCase(departure) && 
                        arr.equalsIgnoreCase(arrival)) {

                        LocalDate fDate = flight.getDate(); //
                        LocalTime fTime = flight.getHour(); //

                        if (fDate != null && fTime != null) {
                            LocalDateTime flightDateTime = LocalDateTime.of(fDate, fTime);

                            // Zaman kontrol edilir.
                            if (!flightDateTime.isBefore(now)) {
                                if (selectedDate == null) {
                                    result.add(flight);
                                } else {
                                    if (!fDate.isBefore(selectedDate)) {
                                        result.add(flight);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    public List<Seat> getSeatList(Flight flight) {
        if (flight != null && flight.getPlane() != null) {
            //
            return new ArrayList<>(flight.getPlane().getSeatMatrix().values());
        }
        return new ArrayList<>();
    }
}