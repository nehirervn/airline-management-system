package User;

import Management.FlightManager;
import Management.ReservationManager;
import Flight.Flight;
import Reservation.Reservation;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

public class Staff extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    private double salary;

    public Staff(String username, String password, String name, String surname, double salary) {
        super(username, password, name, surname);
        this.salary = salary;
    }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }


    //Tüm uçuşlar listelenir.
    public void listAllFlights(FlightManager fm) {
        if (fm != null) {
            System.out.println("--- Güncel Uçuş Listesi ---");
            // ESKİSİ: fm.getFlights() -> YENİSİ: fm.getAllFlights()
            List<Flight> flights = fm.getAllFlights();
            
            if (flights.isEmpty()) {
                System.out.println("Sistemde kayıtlı uçuş yok.");
            } else {
                for (Flight f : flights) {
                    System.out.println(f.getDetails());
                }
            }
        }
    }

    //yeni uçuş eklenir
    public void addNewFlight(FlightManager fm, Flight flight) {
        if (fm != null) {
            // ESKİSİ: fm.addFlight -> YENİSİ: fm.createFlight
            boolean basarili = fm.createFlight(flight);
            if (basarili) {
                System.out.println("Personel: Yeni uçuş eklendi -> " + flight.getFlightNum());
            } else {
                System.out.println("Hata: Uçuş eklenemedi (Numara çakışması olabilir).");
            }
        }
    }

  //Uçuş silinir.
    public void removeFlight(FlightManager fm, ReservationManager rm, Flight flight) {
        if (fm != null && flight != null) {
            // ESKİSİ: fm.removeFlight(flight) -> YENİSİ: fm.deleteFlight(String, ResManager)
            boolean basarili = fm.deleteFlight(flight.getFlightNum(), rm);
            
            if (basarili) {
                System.out.println("Personel: Uçuş silindi -> " + flight.getFlightNum());
            } else {
                System.out.println("Hata: Uçuş silinemedi.");
            }
        }
    }

    //Uçuş düzenlenir.
    public void updateFlightTime(FlightManager fm, Flight flight, LocalDateTime newDate) {
        if (fm != null && flight != null) {

            flight.setDate(newDate.toLocalDate());
            flight.setHour(newDate.toLocalTime());
            
            boolean basarili = fm.updateFlight(flight);
            
            if (basarili) {
                System.out.println("Personel: Uçuş saati güncellendi ve kaydedildi -> " + flight.getFlightNum());
            } else {
                System.out.println("Hata: Güncelleme kaydedilemedi.");
            }
        }
    }

  
    //Rezervasyonlar görüntülenir.
    public void viewReservations(ReservationManager rm) {
        if (rm != null) {
            System.out.println("--- Sistemdeki Tüm Rezervasyonlar ---");
            List<Reservation> allReservations = rm.getAllReservations();
            
            if (allReservations.isEmpty()) {
                System.out.println("Henüz hiç rezervasyon yapılmamış.");
            } else {
                for (Reservation r : allReservations) {
                    System.out.println(r.toString()); 
                }
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + " [Maaş: " + salary + " TL]";
    }
}