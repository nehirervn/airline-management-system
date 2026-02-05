package Management;

import Flight.Flight;
import Flight.Plane;
import Flight.Seat;
import Reservation.Baggage;
import Reservation.Passenger;
import Reservation.Reservation;
import Reservation.Ticket;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class ReservationManager {

    private static final String FILE_NAME = "reservations.dat";

    private final List<Reservation> reservations = new ArrayList<>();
    private final CalculatePrice priceCalculator = new CalculatePrice();
    private final SeatManager seatManager;

    public ReservationManager(SeatManager seatManager) {
        this.seatManager = seatManager;
        loadReservationsFromFile();
    }

    public ReservationManager() {
        this(new SeatManager());
    }

    //Reservation code ve ticked id rastgele üretilir.
    private String generateReservationCode() {
        return "R-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String generateTicketID() {
        return "T-" + UUID.randomUUID().toString().substring(0, 8);
    }


    public synchronized Ticket makeReservationSync(Flight flight, Passenger passenger, Seat seat,
                                                   double baggageWeight, int baggageAllowanceKg) {
        return doMakeReservation(flight, passenger, seat, baggageWeight, baggageAllowanceKg);
    }


    public Ticket makeReservation(Flight flight, Passenger passenger, Seat seat,
                                  double baggageWeight, int baggageAllowanceKg) {
        return doMakeReservation(flight, passenger, seat, baggageWeight, baggageAllowanceKg);
    }


    private synchronized Ticket doMakeReservation(Flight flight, Passenger passenger, Seat seat,
                                                  double baggageWeight, int baggageAllowanceKg) {

        if (flight == null || passenger == null || seat == null) return null;
        if (seatManager == null) return null;

        Plane plane = flight.getPlane();
        if (plane == null) return null;

        // Koltuk başka thread tarafından alınmış olabilir.
        if (seat.isReserved()) {
        	return null;
        }

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

        // Koltuğu SeatManager üzerinden rezerve et
        boolean reservedOk;
        try {
            reservedOk = seatManager.reserveSeat(plane, seat.getSeatNum());
        } catch (RuntimeException ex) {
            return null;
        }

        if (!reservedOk) {
            return null;
        }

        String reservationCode = generateReservationCode();
        String ticketID = generateTicketID();

        Reservation res = new Reservation(reservationCode, flight, passenger, seat, LocalDateTime.now());
        reservations.add(res);

        // Fiyat hesapla
        double price = priceCalculator.calculatePrice(
                seat.getPrice(), seat, baggageWeight, baggageAllowanceKg
        );

        Baggage baggageObj = (baggageWeight > 0) ? new Baggage(baggageWeight) : null;

        Ticket newTicket = new Ticket(ticketID, res, price, baggageAllowanceKg, baggageObj);

        saveReservationsToFile();
        TicketFileManager.saveTicket(newTicket);

        return newTicket;
    }

    public synchronized boolean cancelReservation(String reservationCode) {
        if (reservationCode == null || seatManager == null) return false;

        for (int i = 0; i < reservations.size(); i++) {
            Reservation r = reservations.get(i);

            if (reservationCode.equals(r.getReservationCode())) {
                Seat seat = r.getSeat();

                seatManager.releaseSeat(seat);

                reservations.remove(i);
                saveReservationsToFile();
                return true;
            }
        }
        return false;
    }


    public synchronized void deleteReservationsForFlight(String flightNum) {
        if (flightNum == null || seatManager == null) return;

        Iterator<Reservation> it = reservations.iterator();
        boolean changed = false;

        while (it.hasNext()) {
            Reservation r = it.next();
            if (r.getFlight() != null && flightNum.equals(r.getFlight().getFlightNum())) {
                seatManager.releaseSeat(r.getSeat());
                it.remove();
                changed = true;
            }
        }

        if (changed) {
            saveReservationsToFile();
        }
    }

    /*
     * Uçuş saati/tarihi değiştiğinde, sadece gelecekteki rezervasyonları günceller.
     */
    public synchronized void updateFlightInfoInReservations(Flight updatedFlight) {
        if (updatedFlight == null) return;

        boolean changed = false;
        LocalDateTime now = LocalDateTime.now();

        for (Reservation r : reservations) {
            if (r.getFlight() == null) continue;

            if (r.getFlight().getFlightNum().equals(updatedFlight.getFlightNum())) {
                Flight oldFlight = r.getFlight();
                if (oldFlight.getDate() == null || oldFlight.getHour() == null) continue;

                LocalDateTime oldFlightTime = LocalDateTime.of(oldFlight.getDate(), oldFlight.getHour());

                if (oldFlightTime.isAfter(now)) {
                    r.setFlight(updatedFlight);
                    changed = true;
                }
            }
        }

        if (changed) {
            saveReservationsToFile();
        }
    }


    public List<Reservation> getReservationsForPassenger(int passengerId) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getPassenger() != null && r.getPassenger().getPassengerID() == passengerId) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Reservation> getReservationsForFlight(String flightNum) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : reservations) {
            if (r.getFlight() != null && r.getFlight().getFlightNum().equals(flightNum)) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations);
    }

    
    //reservations.dat dosyasına kaydeder.
    private synchronized void saveReservationsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(reservations);
        } catch (IOException e) {
            System.err.println("Rezervasyon kayıt hatası: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadReservationsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object readObject = ois.readObject();
            if (readObject instanceof List) {
                this.reservations.clear();
                this.reservations.addAll((List<Reservation>) readObject);
            }
        } catch (EOFException e) {
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Rezervasyon yükleme hatası: " + e.getMessage());
        }
    }
}