package Test;

import Flight.Plane;
import Flight.Seat;
import Management.SeatManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SeatManagerTest {

    private SeatManager seatManager;
    private Plane plane;

    @BeforeEach
    void setUp() {
        seatManager = new SeatManager();
        // Uçak kapasitesini 180 olarak belirlenir.
        int capacity = 180;
        plane = new Plane("P001", "Boeing 737", capacity);
        
        // Test için fiyatı 1000.0 TL olarak belirliyoruz.
        seatManager.createSeatLayout(plane, capacity, 1000.0);
    }

    // Test 1: Toplam kapasite ve koltuk oluşturma kontrolü
    @Test
    void testCreateSeatLayout() {
        assertEquals(180, plane.getSeatMatrix().size(), "Toplam 180 koltuk oluşturulmalı");
        assertNotNull(plane.getSeat("1A"), "1A koltuğu oluşmuş olmalı");
    }

    // Test 2: Boş koltuk sayısı azalıyor mu kontrol edilir.
    @Test
    void testEmptySeatsCountDecrease() {
        int initialCount = seatManager.emptySeatsCount(plane);
        seatManager.reserveSeat(plane, "1A");
        int newCount = seatManager.emptySeatsCount(plane);
        
        assertEquals(initialCount - 1, newCount, "Rezervasyon sonrası boş koltuk sayısı 1 azalmalı");
    }

    // Test 3: Olmayan bir koltuğu rezerve etmeye çalışmak kontrol edilir.
    @Test
    void testReserveNonExistentSeat() {
        assertThrows(IllegalArgumentException.class, () -> {
            seatManager.reserveSeat(plane, "99Z");
        }, "Olmayan koltuk için hata fırlatılmalı");
    }

    // Test 4: Zaten dolu olan koltuğu rezerve etmeye çalışmak kontrol edilir.
    @Test
    void testReserveAlreadyReservedSeat() {
        seatManager.reserveSeat(plane, "5A"); // İlk rezervasyon (Başarılı olmalı)
        boolean result = seatManager.reserveSeat(plane, "5A"); // İkinci deneme (Başarısız olmalı)
        
        assertFalse(result, "Dolu koltuk tekrar rezerve edilememeli");
    }
    
    // Test 5: Koltuk iptali sonrası durum kontrol edilir.
    @Test
    void testReleaseSeat() {
        Seat seat = plane.getSeat("10A");
        seat.setReserved(true); 
        
        seatManager.releaseSeat(seat);
        
        assertFalse(seat.isReserved(), "Koltuk serbest bırakıldıktan sonra durumu false olmalı");
    }
    
    @Test
    void testReleaseSeat2() {
        Seat seatRef1 = plane.getSeat("10A");
        seatRef1.setReserved(true); 
        
        seatManager.releaseSeat(seatRef1);
        
        Seat seatRef2 = plane.getSeat("10A");
        assertFalse(seatRef2.isReserved(), "Map içindeki nesne de güncellenmiş olmalı");
        assertSame(seatRef1, seatRef2);
    }
    
    @Test
    void testEmptySeatsCountWithNullPlane() {
        SeatManager seatManager = new SeatManager();

        int emptySeats = seatManager.emptySeatsCount(null);

        assertEquals(0, emptySeats,
                "Plane null olduğunda emptySeatsCount metodunun 0 döndürmesi beklenir.");
    }
}