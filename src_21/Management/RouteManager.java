package Management;

import Flight.Route;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RouteManager {
    private final List<Route> routes = new ArrayList<>();
    private static final String FILE_NAME = "routes.dat";

    public RouteManager() {
        loadRoutesFromFile();
    }

    /*
     * Yeni rota ekler.
     * EĞER AYNI KALKIŞ VE VARIŞ YERİNE SAHİP ROTA VARSA EKLEMEZ.
     */
    public boolean addRoute(Route newRoute) {
        if (newRoute == null) return false;

        for (Route r : routes) {
            boolean sameDep = r.getDepartureAirport().equalsIgnoreCase(newRoute.getDepartureAirport());
            boolean sameArr = r.getArrivalAirport().equalsIgnoreCase(newRoute.getArrivalAirport());
            
            if (sameDep && sameArr) {
                System.out.println("HATA: Bu rota (" + r.toString() + ") zaten kayıtlı!");
                return false;
            }
        }

        routes.add(newRoute);
        saveRoutesToFile();
        return true;
    }

    public List<Route> getAllRoutes() {
        return new ArrayList<>(routes);
    }

    
    private synchronized void saveRoutesToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(routes);
        } catch (IOException e) {
            System.err.println("Rota kayıt hatası: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadRoutesFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object readObject = ois.readObject();
            if (readObject instanceof List) {
                this.routes.clear();
                this.routes.addAll((List<Route>) readObject);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Rota yükleme hatası: " + e.getMessage());
        }
    }
}
