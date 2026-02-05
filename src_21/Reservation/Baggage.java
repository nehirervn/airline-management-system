package Reservation;

import java.io.Serializable;

public class Baggage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private double weight;

    public Baggage(double weight) {
        setWeight(weight); // Kontrollü atama yapılır.
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        if (weight < 0) {
            throw new IllegalArgumentException("Bagaj ağırlığı negatif olamaz: " + weight);
        }
        this.weight = weight;
    }
    
    @Override
    public String toString() {
        return weight + " kg Bagaj";
    }
}