package Reservation;

import Flight.Flight;
import Flight.Seat;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Reservation implements Serializable{
	private static final long serialVersionUID = 1L;
	 private String reservationCode;
	    private Flight flight;
	    private Passenger passenger;
	    private Seat seat;
	    private LocalDateTime dateOfReservation;

	    public Reservation(String reservationCode, Flight flight,Passenger passenger,Seat seat,LocalDateTime dateOfReservation) {
	        this.reservationCode = reservationCode;
	        this.flight = flight;
	        this.passenger = passenger;
	        this.seat = seat;
	        this.dateOfReservation = dateOfReservation;
	    }

	    public String getReservationCode() {
	        return reservationCode;
	    }

	    public void setReservationCode(String reservationCode) {
	        this.reservationCode = reservationCode;
	    }

	    public Flight getFlight() {
	        return flight;
	    }

	    public void setFlight(Flight flight) {
	        this.flight = flight;
	    }

	    public Passenger getPassenger() {
	        return passenger;
	    }

	    public void setPassenger(Passenger passenger) {
	        this.passenger = passenger;
	    }

	    public Seat getSeat() {
	        return seat;
	    }

	    public void setSeat(Seat seat) {
	        this.seat = seat;
	    }

	    public LocalDateTime getDateOfReservation() {
	        return dateOfReservation;
	    }

	    public void setDateOfReservation(LocalDateTime dateOfReservation) {
	        this.dateOfReservation = dateOfReservation;
	    }


	    public String getReservationDetails() {
	        return "Reservation " + reservationCode +
	                " | Flight: " + (flight != null ? flight.getFlightNum() : "N/A") +
	                " | Passenger: " + (passenger != null ? passenger.getName() + " " + passenger.getSurname() : "N/A") +
	                " | Seat: " + (seat != null ? seat.getSeatNum() : "N/A") +
	                " | Date: " + dateOfReservation;
	    }

	    @Override
	    public String toString() {
	        return getReservationDetails();
	    }
}
