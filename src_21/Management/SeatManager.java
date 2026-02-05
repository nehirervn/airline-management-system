package Management;

import Flight.Plane;
import Flight.Seat;
import Flight.SeatType;
import java.util.List;

public class SeatManager {

    public void createSeatLayout(Plane plane, int capacity, double ecoPrice) {
        if (plane == null) return;

        char[] cols = {'A', 'B', 'C', 'D', 'E', 'F'}; 
        int seatsPerRow = cols.length;
        int totalRows = capacity / seatsPerRow; 

        for (int row = 1; row <= totalRows; row++) {
            for (char col : cols) {
                String seatNum = row + String.valueOf(col);
                
                // İlk 3 sıra Business, kalanı Economy
                SeatType type = (row <= 3) ? SeatType.BUSINESS : SeatType.ECONOMY;
                
                double price = ecoPrice; 

                Seat seat = new Seat(seatNum, type, price);
                plane.addSeat(seat);
            }
        }
        plane.setCapacity(totalRows * seatsPerRow);
    }
    
    
    public int emptySeatsCount(Plane plane) {
        if (plane == null) return 0;
        List<Seat> available = plane.getAvailableSeats();
        return (available != null) ? available.size() : 0;
    }

    
    public boolean reserveSeat(Plane plane, String seatNum) {
        if (plane == null || seatNum == null) throw new IllegalArgumentException("Hata");
        Seat seat = plane.getSeat(seatNum);
        if (seat == null) throw new IllegalArgumentException("Koltuk bulunamadı");
        if (seat.isReserved()) return false;
        seat.setReserved(true);
        return true;
    }
    
    
    public void releaseSeat(Seat seat) {
        if (seat != null) seat.setReserved(false);
    }
}