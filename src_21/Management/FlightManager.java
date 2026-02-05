package Management;

import Flight.Flight;
import java.io.*; // Dosya işlemleri için gerekli
import java.util.*;

public class FlightManager {
    
    private final List<Flight> flights = new ArrayList<>();
    private final String fileName;

    //Constructor ilk oluştuğunda eski uçuşları dosyadan yükle
    public FlightManager() {
        this.fileName = "flights.dat";
        loadFlightsFromFile();
    }

    
    public FlightManager(String fileName) {
        this.fileName = fileName;
        loadFlightsFromFile();
    }
    
    
    public boolean createFlight(Flight flight) {
        if (flight == null || flight.getFlightNum() == null) {
            return false;
        }
        if (getFlightByNumber(flight.getFlightNum()) != null) {
            return false;
        }
        
        boolean added = flights.add(flight);
        
        if (added) {
            saveFlightsToFile();
        }
        return added;
    }


    public Flight getFlightByNumber(String flightNum) {
        if (flightNum == null) return null;
        for (Flight f : flights) {
            if (flightNum.equals(f.getFlightNum())) {
                return f;
            }
        }
        return null;
    }


    public boolean updateFlight(Flight updated) {
        if (updated == null || updated.getFlightNum() == null) {
            return false;
        }
        for (int i = 0; i < flights.size(); i++) {
            if (updated.getFlightNum().equals(flights.get(i).getFlightNum())) {
                flights.set(i, updated); // Listeyi güncelle
                saveFlightsToFile();     // Dosyayı güncelle
                return true;
            }
        }
        return false;
    }

    public boolean deleteFlight(String flightNum, ReservationManager resManager) {
        Flight f = getFlightByNumber(flightNum);
        
        if (f != null) {
            Management.TicketFileManager.cancelTicketsForFlight(flightNum);
            
            if (resManager != null) {
                resManager.deleteReservationsForFlight(flightNum);
            }

            boolean removed = flights.remove(f);
            if (removed) {
                saveFlightsToFile();
            }
            return removed;
        }
        return false;
    }


    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights);
    }


    
    // Uçuş listesini (flights) tek bir nesne olarak dosyaya yazar.
    private synchronized void saveFlightsToFile() {
    	try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
    		oos.writeObject(flights);
        } catch (IOException e) {
        	System.err.println("Uçuş kaydetme hatası: " + e.getMessage());
        }
    }

    
    // Program açıldığında dosyadan uçuş listesini okur.
    @SuppressWarnings("unchecked")
    private void loadFlightsFromFile() {
        File file = new File(fileName);
        if (!file.exists() || file.length() == 0) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object readObject = ois.readObject();
            if (readObject instanceof List) {
                this.flights.clear();
                this.flights.addAll((List<Flight>) readObject);
            }
        } catch (EOFException e) {
            // dosya boşsa bir şey yapmaz.
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Uçuş yükleme hatası: " + e.getMessage());
        }
    }
}