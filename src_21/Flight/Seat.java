package Flight;

import java.io.Serializable;

public class Seat implements Serializable {
    private static final long serialVersionUID = 1L;

    private String seatNum;
    private SeatType type;
    private double price;
    private boolean reservedStatus;

    public Seat(String seatNum, SeatType type, double price) {
        setSeatNum(seatNum); // Setter kullanarak kontrollü şekilde atama yapıyoruz.
        setType(type);
        setPrice(price);   
        this.reservedStatus = false;
    }

    public String getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(String seatNum) {
        if (seatNum == null || seatNum.trim().isEmpty()) {
            throw new IllegalArgumentException("Koltuk numarası boş olamaz!");
        }
        this.seatNum = seatNum;
    }

    public SeatType getType() {
        return type;
    }

    public void setType(SeatType type) {
        if (type == null) {
            throw new IllegalArgumentException("Koltuk tipi (Economy/Business) boş olamaz!");
        }
        this.type = type;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Koltuk fiyatı negatif olamaz: " + price);
        }
        this.price = price;
    }

    public boolean isReserved() {
        return reservedStatus;
    }

    public void setReserved(boolean reservedStatus) {
        this.reservedStatus = reservedStatus;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatNum='" + seatNum + '\'' +
                ", type=" + type +
                ", price=" + price +
                ", reservedStatus=" + reservedStatus +
                '}';
    }
}