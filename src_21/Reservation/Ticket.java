package Reservation;

import java.io.Serializable;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;
    private String ticketID;
    private Reservation reservation;
    private double price;
    private int baggageAllowance;
    private Baggage baggage;
    private boolean isCancelled = false;

    public Ticket(String ticketID, Reservation reservation, double price, int baggageAllowance,Baggage baggage) {
        this.ticketID = ticketID;
        setReservation(reservation);//Kontrollü atama yapılır
        setPrice(price);           
        setBaggageAllowance(baggageAllowance);
        this.baggage = baggage;
        this.isCancelled = false;
    }


    public void setReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Bilet bir rezervasyona bağlı olmalıdır.");
        }
        this.reservation = reservation;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Bilet fiyatı negatif olamaz: " + price);
        }
        this.price = price;
    }

    public int getBaggageAllowance() {
        return baggageAllowance;
    }

    public void setBaggageAllowance(int baggageAllowance) {
        if (baggageAllowance < 0) {
            throw new IllegalArgumentException("Bagaj hakkı negatif olamaz: " + baggageAllowance);
        }
        this.baggageAllowance = baggageAllowance;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void cancelTicket() {
        this.isCancelled = true;
    }
    
    public String getTicketID() { return ticketID; }
    public void setTicketID(String ticketID) { this.ticketID = ticketID; }
    public Reservation getReservation() { return reservation; }
    public Baggage getBaggage() { return baggage; }
    public void setBaggage(Baggage baggage) { this.baggage = baggage; }
    
    public double getTotalBaggageWeight() {
        return baggage != null ? baggage.getWeight() : 0.0;
    }
    
    public boolean isOverAllowance() {
        return getTotalBaggageWeight() > baggageAllowance;
    }
    
    
    
    @Override
    public String toString() {
        String durum = isCancelled ? "[İPTAL]" : "[AKTİF]";

        String ucusBilgisi = "N/A";
        String yolcuAdi = "N/A";
        String koltukNo = "N/A";

        if (reservation != null) {
            if (reservation.getFlight() != null) {
                ucusBilgisi = reservation.getFlight().getFlightNum() + " (" + 
                              reservation.getFlight().getDeparturePlace() + "->" + 
                              reservation.getFlight().getArrivalPlace() + ")";
            }
            
            // Yolcu Adı
            if (reservation.getPassenger() != null) {
                yolcuAdi = reservation.getPassenger().getName() + " " + reservation.getPassenger().getSurname();
            }
            
            // Koltuk
            if (reservation.getSeat() != null) {
                koltukNo = reservation.getSeat().getSeatNum();
            }
        }
        return String.format("%-7s | ID: %-8s | ✈ %-22s |  %-15s |  %-3s |  %.2f TL",
                durum, ticketID, ucusBilgisi, yolcuAdi, koltukNo, price);
    }
}