package Test;

import Reservation.Baggage; // Baggage sınıfının yeri
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BaggageTest {

    // Test: Negatif ağırlık girildiğinde sistem hata (Exception) vermeli
    @Test
    void testNegativeBaggageWeight() {
        
        // 1. Durum: Constructor üzerinden negatif giriş deneniyor
        assertThrows(IllegalArgumentException.class, () -> {
            new Baggage(-15.0); 
        }, "Constructor negatif bagaj ağırlığını kabul etmemeli, hata fırlatmalı.");

        // 2. Durum: Setter metodu üzerinden negatif giriş deneniyor
        Baggage baggage = new Baggage(10.0); // Önce geçerli bir bagaj oluşturduk
        
        assertThrows(IllegalArgumentException.class, () -> {
            baggage.setWeight(-5.0);
        }, "setWeight metodu negatif değeri kabul etmemeli.");
    }

    // Test: Geçerli (Pozitif) ağırlık girildiğinde sorun çıkmamalı
    @Test
    void testValidBaggageWeight() {
        Baggage baggage = new Baggage(20.0);
        assertEquals(20.0, baggage.getWeight(), 0.01, "Pozitif ağırlık doğru atanmalı");
        
        baggage.setWeight(30.5);
        assertEquals(30.5, baggage.getWeight(), 0.01, "Setter ile pozitif ağırlık güncellenebilmeli");
    }
}
