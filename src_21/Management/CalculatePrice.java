package Management;

import Flight.Seat;
import Flight.SeatType;

public class CalculatePrice {
    private static final double EXTRA_BAGGAGE_FEE_PER_KG = 50.0; // Ekstra kg başına fiyat 50 artar.
    private static final double BUSINESS_MULTIPLIER = 2.0; // Business economynin 2 katı fiyata sahip.

    public double calculatePrice(double basePrice, Seat seat, double baggageWeight, int baggageAllowance) {
        if (basePrice < 0) {
            throw new IllegalArgumentException("Taban fiyat negatif olamaz.");
        }

        double seatPrice = basePrice;
        if (seat.getType() == SeatType.BUSINESS) {
            seatPrice = basePrice * BUSINESS_MULTIPLIER;
        }

        seat.setPrice(seatPrice);

        double totalPrice = seatPrice;
        if (baggageWeight > baggageAllowance) {
            double extraWeight = baggageWeight - baggageAllowance;
            totalPrice += (extraWeight * EXTRA_BAGGAGE_FEE_PER_KG);
        }

        return totalPrice;
    }
}