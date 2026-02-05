package Simulation;

import Flight.Plane;
import Flight.Seat;
import Management.SeatManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SeatSimulation {

    public interface SimulationCallback {
        void onSeatReserved(String seatNum);
        void onStatusUpdated(int occupied, int target);
    }

    private Plane simulationPlane;
    private final int TOTAL_SEATS = 180;
    private final int PASSENGER_COUNT = 90;
    private SeatManager seatManager;

    public SeatSimulation() {
        this.seatManager = new SeatManager();
        initSimulationPlane();
    }

    private void initSimulationPlane() {
        simulationPlane = new Plane("SIM-TEMP", "Simulation", TOTAL_SEATS);
        seatManager.createSeatLayout(simulationPlane, TOTAL_SEATS, 100.0);
    }

    public void runSimulation(boolean isSynchronized, SimulationCallback callback) {
        initSimulationPlane();
        
        if (callback != null) callback.onStatusUpdated(0, PASSENGER_COUNT);

        // 90 kişilik bir işçi havuzu oluşturulur.
        ExecutorService executor = Executors.newFixedThreadPool(PASSENGER_COUNT);

        for (int i = 0; i < PASSENGER_COUNT; i++) {
            
            executor.execute(() -> {
                try {
                    boolean success = false;
                    Seat targetSeat = null;
                    Random random = new Random();

                    List<Seat> availableSeats = new ArrayList<>();
                    for (Seat s : simulationPlane.getSeatMatrix().values()) {
                        if (!s.isReserved()) availableSeats.add(s);
                    }

                    //senkronize mi unsenkronize mi kontrolü yapılır.
                    if (isSynchronized) {
                        while (!success && !availableSeats.isEmpty()) {
                            targetSeat = availableSeats.get(random.nextInt(availableSeats.size()));
                            
                            synchronized (simulationPlane) {
                                if (!targetSeat.isReserved()) {
                                    success = seatManager.reserveSeat(simulationPlane, targetSeat.getSeatNum());
                                }
                            }
                            if (!success) {
                                availableSeats.clear();
                                for (Seat s : simulationPlane.getSeatMatrix().values()) if (!s.isReserved()) availableSeats.add(s);
                            }
                        }
                    } 
                    else {
                        if (!availableSeats.isEmpty()) {
                            targetSeat = availableSeats.get(random.nextInt(availableSeats.size()));
                            
                            try { Thread.sleep(10); } catch (Exception e) {}

                            if (!targetSeat.isReserved()) {
                                success = seatManager.reserveSeat(simulationPlane, targetSeat.getSeatNum());
                            }
                        }
                    }

                    if (success && callback != null && targetSeat != null) {
                        int occupied = TOTAL_SEATS - seatManager.emptySeatsCount(simulationPlane);
                        callback.onSeatReserved(targetSeat.getSeatNum());
                        callback.onStatusUpdated(occupied, PASSENGER_COUNT);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown(); // İşlem bitince havuz kapatılır.
    }
}