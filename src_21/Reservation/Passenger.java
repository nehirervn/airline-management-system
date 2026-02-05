package Reservation;

import java.io.Serializable;
import java.util.Random;

public class Passenger implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int passengerID;
    private String name;
    private String surname;
    private String contactInfo;
    
    Random rand = new Random();

    public Passenger(String name, String surname, String contactInfo) {
        setName(name); //Kontrollü atama yapılır.
        setSurname(surname);
        setContactInfo(contactInfo);
    }


    public int getPassengerID() {
        return passengerID;
    }

    public void setPassengerID(int passengerID) {
        this.passengerID = passengerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.length() < 2) {
            throw new IllegalArgumentException("HATA: İsim en az 2 harfli olmalıdır.");
        }
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        if (surname == null || surname.length() < 2) {
            throw new IllegalArgumentException("HATA: Soyisim en az 2 harfli olmalıdır.");
        }
        this.surname = surname;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        // Telefon için en az 5 karakter istenir.
        if (contactInfo == null || contactInfo.length() < 5) {
            throw new IllegalArgumentException("HATA: Geçersiz iletişim bilgisi (En az 5 karakter).");
        }
        this.contactInfo = contactInfo;
    }
    
    @Override
    public String toString() {
        return name + " " + surname + " (ID: " + passengerID + ")";
    }
    
    
}