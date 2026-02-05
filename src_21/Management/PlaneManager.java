package Management;

import Flight.Plane;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PlaneManager {
    private final List<Plane> planes = new ArrayList<>();
    private static final String FILE_NAME = "planes.dat";
    private SeatManager seatManager = new SeatManager();
    
    //Veriler dosyadan çekilir.
    public PlaneManager() {
        loadPlanesFromFile();
    }


    public boolean addPlane(String planeID, String model, int capacity, double ecoPrice) {
        // ID kontrol edilir.
        for (Plane p : planes) {
            if (p.getPlaneID().equals(planeID)) {
                System.out.println("HATA: Bu ID'ye sahip uçak zaten var!");
                return false; 
            }
        }
        
        Plane newPlane = new Plane(planeID, model, capacity);

        // Koltukları yerleştirilir (Sabit: İlk 3 Business, gerisi Economy)
        seatManager.createSeatLayout(newPlane, capacity,ecoPrice);

        planes.add(newPlane);
        savePlanesToFile();
        return true;
    }

    public List<Plane> getAllPlanes() { 
    	return new ArrayList<>(planes);
    }
    
    public Plane getPlaneByID(String planeID) {
        for (Plane p : planes) {
            if (p.getPlaneID().equals(planeID)) return p;
        }
        return null;
    }

    //Uçaklar planes.dat dosyasına kaydedilir.
    private synchronized void savePlanesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(planes);
        } catch (IOException e) {
            System.err.println("Uçak kayıt hatası: " + e.getMessage());
        }
    }

    //Uçaklar planes.dat dosyasından çekilir.
    @SuppressWarnings("unchecked")
    private void loadPlanesFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object readObject = ois.readObject();
            if (readObject instanceof List) {
                this.planes.clear();
                this.planes.addAll((List<Plane>) readObject);
            }
        } catch (EOFException e) {
            // boş/yarım dosya
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Uçak yükleme hatası: " + e.getMessage());
        }
    }
}