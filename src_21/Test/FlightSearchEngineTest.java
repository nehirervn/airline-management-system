package Test;

import Flight.Flight;
import Flight.Plane;
import Flight.Route;
import Management.FlightManager;
import Management.FlightSearchEngine;
import Management.SeatManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlightSearchEngineTest {

    private FlightSearchEngine searchEngine;
    private FlightManager flightManager;
    private SeatManager seatManager;

    //Flights.dat dosyasına yazmasını engellemek için test dosyasına yazdırıyoruz.
    // Testlerin kullanacağı özel dosya ismi
    private final String TEST_FILE_NAME = "test_flights.dat";

    @BeforeEach
    void setUp() {
        File file = new File(TEST_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }

        flightManager = new FlightManager(TEST_FILE_NAME); 
        
        seatManager = new SeatManager();
        searchEngine = new FlightSearchEngine(flightManager, seatManager);

        // Örnek veriler oluşturulur.
        Plane plane = new Plane("P1", "TestPlane", 180);
        Route route1 = new Route("Istanbul", "Ankara", 100);
        Route route2 = new Route("Izmir", "Antalya", 200);

        Flight futureFlight = new Flight("F001", route1, LocalDate.now().plusDays(1), LocalTime.of(10, 0), 60, "Istanbul", "Ankara", plane); 
        Flight pastFlight = new Flight("F002", route1, LocalDate.now().minusDays(1), LocalTime.of(10, 0), 60, "Istanbul", "Ankara", plane); 
        Flight otherRouteFlight = new Flight("F003", route2, LocalDate.now().plusDays(1), LocalTime.of(12, 0), 60, "Izmir", "Antalya", plane);

        flightManager.createFlight(futureFlight);
        flightManager.createFlight(pastFlight); 
        flightManager.createFlight(otherRouteFlight);
    }
    
    @AfterEach
    void tearDown() {
        File file = new File(TEST_FILE_NAME);
        if (file.exists()) {
            file.delete();
        }
    }

    // --- TESTLER ---

    //Rotaya göre uçuş arama.
    @Test
    void testSearchMatchingRoute() {
        List<Flight> results = searchEngine.searchFlights("Istanbul", "Ankara", null);
        assertEquals(1, results.size(), "F001 bulunmalı");
        assertEquals("F001", results.get(0).getFlightNum());
    }

    //Geçmiş tarihteki uçuşlar filtrelenir.
    @Test
    void testEliminatePastFlights() {
        List<Flight> results = searchEngine.searchFlights("Istanbul", "Ankara", null);
        boolean hasPastFlight = results.stream().anyMatch(f -> f.getFlightNum().equals("F002"));
        assertFalse(hasPastFlight, "Geçmiş tarihli uçuş gelmemeli");
    }

    @Test
    void testCaseInsensitivity() {
        List<Flight> results = searchEngine.searchFlights("istanbul", "ANKARA", null);
        assertEquals(1, results.size());
    }

    //belirli bir tarihe göre arama kontrol edilir.
    @Test
    void testSearchBySpecificDate() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Flight> results = searchEngine.searchFlights("Istanbul", "Ankara", tomorrow);
        assertEquals(1, results.size());
    }

    //eşleşmeyen uçuşlar kontrol edilir.
    @Test
    void testNoMatchingFlights() {
        List<Flight> results = searchEngine.searchFlights("Ankara", "Van", null);
        assertTrue(results.isEmpty());
    }
    
    
    @Test
    void testSearchWhenNoFlightsExist() {
        FlightManager emptyFlightManager = new FlightManager("empty_flights.dat");
        SeatManager seatManager = new SeatManager();
        FlightSearchEngine emptyEngine = new FlightSearchEngine(emptyFlightManager, seatManager);

        // Herhangi bir şehir çiftiyle arama yap.
        List<Flight> results = emptyEngine.searchFlights("Istanbul", "Ankara", null);

        assertNotNull(results, "Sonuç listesi null olmamalı.");
        assertTrue(results.isEmpty(), "Hiç uçuş yokken arama sonucu boş liste olmalı.");
    }
}