package Simulation;

import Flight.Flight;
import Flight.Plane;
import Management.TicketFileManager; // Biletleri dosyalardan okumak için
import Reservation.Ticket;           // Ticket sınıfı
import java.util.List;

public class ReportSimulation {


    public interface ReportCallback {
        void onReportStart();
        void onReportReady(String reportResult);
    }

    public ReportSimulation() {
    }

    public void generateOccupancyReportAsync(List<Flight> flights, ReportCallback callback) {
    	//Ayrı bir threadde raporlama işlemi yapılır.
        Thread reportThread = new Thread(() -> {
            if (callback != null) callback.onReportStart();

            try {
                Thread.sleep(2000); 
            } catch (InterruptedException e) { }

            StringBuilder sb = new StringBuilder();
            sb.append("=== FLIGHT OCCUPANCY REPORT ===\n");
            sb.append("Generated at: ").append(java.time.LocalDateTime.now()).append("\n\n");
            sb.append(String.format("%-10s %-20s %-15s %s\n", "Flight No", "Route", "Occupancy", "Status"));
            sb.append("-------------------------------------------------------------\n");

            int totalFlights = 0;
            double totalOccupancy = 0;

            List<Ticket> allTickets = TicketFileManager.loadAllTickets(); 

            for (Flight f : flights) {
                Plane p = f.getPlane();
                
                if (p != null) {
                    int capacity = p.getCapacity();
                    int occupiedCount = 0;

                    for (Ticket t : allTickets) {
                        if (t.getReservation() != null && t.getReservation().getFlight() != null) {

                            String ticketFlightNum = t.getReservation().getFlight().getFlightNum();
                            
                            if (ticketFlightNum.equals(f.getFlightNum())) {
                                if (!t.isCancelled()) {
                                    occupiedCount++; 
                                }
                            }
                        }
                    }
                    
                    // Oran hesabı
                    double rate = 0;
                    if (capacity > 0) {
                        rate = (double) occupiedCount / capacity * 100;
                    }

                    String route = f.getDeparturePlace() + " -> " + f.getArrivalPlace();
                    sb.append(String.format("%-10s %-20s %%%-14.2f %d/%d\n", 
                            f.getFlightNum(), 
                            route, 
                            rate,
                            occupiedCount, capacity)); 
                    
                    totalOccupancy += rate;
                    totalFlights++;
                }
            }
            
            sb.append("\n-------------------------------------------------------------\n");
            if (totalFlights > 0) {
                sb.append(String.format("Average System Occupancy: %%%.2f", (totalOccupancy / totalFlights)));
            } else {
                sb.append("No flights found.");
            }

            if (callback != null) callback.onReportReady(sb.toString());
        });

        reportThread.start();
    }
}