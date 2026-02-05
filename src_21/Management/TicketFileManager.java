package Management;

import Reservation.Reservation;
import Reservation.Ticket;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Flight.Flight;

public class TicketFileManager {

    private static final String FILE_NAME = "tickets.dat";

    /*
     * Tek bir bileti dosyaya kaydeder.
     * Önce mevcut dosyayı oku -> Listeye ekle -> Tekrar dosyaya yaz.
     */
    public static void saveTicket(Ticket ticket) {
        List<Ticket> allTickets = loadAllTickets(); 
        allTickets.add(ticket); 
        writeListToFile(allTickets); 
    }
    
    public static void saveAllTickets(List<Ticket> tickets) {
        writeListToFile(tickets);
    }
    
    
    public static boolean cancelTicketByReservationCode(String reservationCode) {
        if (reservationCode == null) return false;

        List<Ticket> allTickets = loadAllTickets();
        boolean changed = false;

        for (Ticket t : allTickets) {

            if (t != null) {
                Reservation r = t.getReservation();

                if (r != null) {
                    if (reservationCode.equals(r.getReservationCode())) {

                        if (!t.isCancelled()) {
                            t.cancelTicket();
                            changed = true;
                        }
                    }
                }
            }
        }
        if (changed) {
            writeListToFile(allTickets);
        }
        return changed;
    }

    /*
     * Dosyadaki TÜM biletleri okur ve liste olarak döner.
     */
    @SuppressWarnings("unchecked")
    public static List<Ticket> loadAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        File file = new File(FILE_NAME);

        // Dosya yoksa boş liste döner.
        if (!file.exists()) {
            return tickets;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            tickets = (List<Ticket>) ois.readObject();
        } catch (EOFException e) {
            // dosya boşsa boş liste döner.
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return tickets;
    }
    
    
    public static void cancelTicketsForFlight(String flightNum) {
        List<Ticket> allTickets = loadAllTickets();
        boolean changed = false;

        for (Ticket t : allTickets) {
            if (t.getReservation().getFlight().getFlightNum().equals(flightNum)) {
                t.cancelTicket(); 
                changed = true;
            }
        }

        // Eğer en az bir bilet değiştiyse dosyayı günceller.
        if (changed) {
            writeListToFile(allTickets);
        }
    }
    
    
    public static void updateTicketsForFlight(Flight updatedFlight) {
        List<Ticket> allTickets = loadAllTickets(); 
        boolean changed = false;
        LocalDateTime now = LocalDateTime.now(); // Şu anki zaman

        for (Ticket t : allTickets) {
            if (t.getReservation() != null && t.getReservation().getFlight() != null) {
                
                Flight oldFlight = t.getReservation().getFlight();

                if (oldFlight.getFlightNum().equals(updatedFlight.getFlightNum())) {
                    
                    LocalDateTime oldFlightTime = LocalDateTime.of(oldFlight.getDate(), oldFlight.getHour());

                    if (oldFlightTime.isAfter(now)) {
                        t.getReservation().setFlight(updatedFlight);
                        changed = true;
                    }
                }
            }
        }

        // Eğer herhangi bir değişiklik yaptıysak dosyayı yeniden yazılır
        if (changed) {
            writeListToFile(allTickets);
            System.out.println("Sadece gelecek uçuşlara ait biletler güncellendi.");
        }
    }
    
    
     // Listeyi komple dosyaya yazar.
    private static void writeListToFile(List<Ticket> tickets) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(tickets);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Biletler dosyaya kaydedilirken hata oluştu!");
        }
    }
}
