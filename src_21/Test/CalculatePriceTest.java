package Test;

import Management.CalculatePrice;
import Flight.Seat;       // <-- Eklendi
import Flight.SeatType;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CalculatePriceTest {

    private final CalculatePrice calculator = new CalculatePrice();

    // Test 1: Economy sınıfı hesaplaması (Çarpan: 1.0)
    @Test
    void testEconomyPrice() {
        // Hazırlık: Test için sahte bir koltuk oluştur (Başlangıç fiyatı 0)
        Seat economySeat = new Seat("1A", SeatType.ECONOMY, 0.0);
        
        // İşlem: 1000 TL baz fiyat ile hesapla
        double result = calculator.calculatePrice(1000.0, economySeat, 15.0, 20);
        
        // Kontrol 1: Metot dönüş değeri doğru mudur kontrol edilir.
        assertEquals(1000.0, result, 0.01, "Economy toplam fiyatı baz fiyata eşit olmalı");
        
        // Kontrol 2: Koltuk nesnesinin fiyatı güncellendi mi kontrol edilir.
        assertEquals(1000.0, economySeat.getPrice(), 0.01, "Seat nesnesinin fiyatı güncellenmeli");
    }

    // Test 2: Business sınıfı hesaplaması (Çarpan: 2.0)
    @Test
    void testBusinessPrice() {
        Seat businessSeat = new Seat("1A", SeatType.BUSINESS, 0.0);
        
        double result = calculator.calculatePrice(1000.0, businessSeat, 15.0, 20);
        
        // Kontrol 1: Dönüş değeri (1000 * 2 = 2000)
        assertEquals(2000.0, result, 0.01, "Business fiyatı baz fiyatın 2 katı olmalı");
        
        // Kontrol 2: Koltuk nesnesi güncellendi mi kontrol edilir.
        assertEquals(2000.0, businessSeat.getPrice(), 0.01, "Seat nesnesine 2000 TL yazılmalı");
    }

    // Test 3: Bagaj aşım ücreti hesaplaması
    // Limit: 20kg, Yolcu: 25kg (Fark: 5kg). Ekstra Ücret: 5 * 50 = 250 TL.
    // Baz Fiyat: 1000 (Economy). Toplam: 1250.0
    @Test
    void testBaggageOverAllowance() {
        Seat seat = new Seat("10B", SeatType.ECONOMY, 0.0);
        
        double result = calculator.calculatePrice(1000.0, seat, 25.0, 20);
        
        // Kontrol 1: Toplam Tutar (1000 koltuk + 250 bagaj = 1250)
        assertEquals(1250.0, result, 0.01, "Bagaj aşım ücreti toplama eklenmeli");
        
        // Kontrol 2: Koltuk Fiyatı 
        assertEquals(1000.0, seat.getPrice(), 0.01, "Koltuk fiyatına bagaj ücreti dahil edilmemeli");
    }

    // Test 4: Bagaj limiti aşılıp aşılmadığı kontrol edilir.
    @Test
    void testBaggageUnderAllowance() {
        Seat seat = new Seat("10C", SeatType.ECONOMY, 0.0);
        
        double result = calculator.calculatePrice(1000.0, seat, 10.0, 20);
        assertEquals(1000.0, result, 0.01, "Limit altı bagaj için ek ücret alınmamalı");
    }

    // Test 5: Negatif baz fiyat hatası kontrol edilir.
    @Test
    void testNegativeBasePrice() {
        Seat dummySeat = new Seat("1A", SeatType.ECONOMY, 0.0);
        
        assertThrows(IllegalArgumentException.class, () -> {
            calculator.calculatePrice(-500.0, dummySeat, 10.0, 20);
        }, "Negatif fiyat girildiğinde hata fırlatılmalı");
    }
}