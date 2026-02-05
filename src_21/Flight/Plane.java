package Flight;

import java.io.Serializable;
import java.util.*;

public class Plane implements Serializable {
    private static final long serialVersionUID = 1L;

    private String planeID;
    private String planeModel;
    private int capacity;
    private Map<String, Seat> seatMatrix = new LinkedHashMap<>(); // LinkedHashMap, ekleme sırasını korur (Koltuk düzeni bozulmaz).
    
    //Atama işlemlerini setter metotları ile kontrol ederek yapıyoruz.
    public Plane(String planeID, String planeModel, int capacity) {
        setPlaneID(planeID);
        setPlaneModel(planeModel);
        setCapacity(capacity);
    }

    public String getPlaneID() {
        return planeID;
    }

    public void setPlaneID(String planeID) {
        if (planeID == null || planeID.trim().isEmpty()) {
            throw new IllegalArgumentException("HATA: Uçak ID boş olamaz!");
        }
        this.planeID = planeID;
    }

    public String getPlaneModel() {
        return planeModel;
    }

    public void setPlaneModel(String planeModel) {
        if (planeModel == null || planeModel.trim().isEmpty()) {
            throw new IllegalArgumentException("HATA: Uçak modeli boş olamaz!");
        }
        this.planeModel = planeModel;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("HATA: Uçak kapasitesi 0 veya negatif olamaz: " + capacity);
        }
        if (seatMatrix != null && seatMatrix.size() > capacity) {
            throw new IllegalArgumentException(
                    "HATA: Kapasite (" + capacity + ") mevcut koltuk sayısından (" + seatMatrix.size() + ") küçük olamaz.");
        }
        this.capacity = capacity;
    }

    public Map<String, Seat> getSeatMatrix() {
        return seatMatrix;
    }

    public int getSeatCount() {
        return seatMatrix.size();
    }

    public boolean isCapacityFull() {
        return seatMatrix.size() >= capacity;
    }

    public void addSeat(Seat seat) {
        if (seat == null) {
            throw new IllegalArgumentException("HATA: Koltuk (seat) null olamaz.");
        }

        String seatNum = seat.getSeatNum();
        if (seatNum == null || seatNum.trim().isEmpty()) {
            throw new IllegalArgumentException("HATA: Koltuk numarası (seatNum) boş olamaz.");
        }

        // Kapasite kontrolü: capacity kadar koltuktan fazlası eklenemez
        if (seatMatrix.size() >= capacity) {
            throw new IllegalStateException(
                    "HATA: Uçak kapasitesi dolu. Kapasite: " + capacity + ", Mevcut koltuk: " + seatMatrix.size());
        }

        // Aynı seatNum seçilirse tekrar alınamasın.
        if (seatMatrix.containsKey(seatNum)) {
            throw new IllegalArgumentException("HATA: Bu koltuk numarası zaten mevcut: " + seatNum);
        }

        seatMatrix.put(seatNum, seat);
    }

    public Seat getSeat(String seatNum) {
        return seatMatrix.get(seatNum);
    }

    public List<Seat> getAvailableSeats() {
        List<Seat> result = new ArrayList<>();
        for (Seat s : seatMatrix.values()) {
            if (!s.isReserved()) result.add(s);
        }
        return result;
    }

    @Override
    public String toString() {
        return planeModel + " [" + planeID + "] - Kapasite: " + capacity;
    }
}