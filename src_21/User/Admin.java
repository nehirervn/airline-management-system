package User;

import Management.FlightManager;
import Management.ReservationManager;
import Flight.Flight;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public class Admin extends User implements Serializable {

    private static final long serialVersionUID = 1L;

    public Admin(String username, String password, String name, String surname) {
        super(username, password, name, surname);
    }


    //Admin çalışan ekleyebilir.
    public void addStaff(UserManager um, Staff newStaff) {
        if (um != null) {
            boolean basarili = um.addUser(newStaff);
            
            if (basarili) {
                System.out.println("Admin: Yeni personel işe alındı -> " + newStaff.getName());
            } else {
                System.out.println("Hata: Personel eklenemedi (Kullanıcı adı alınmış olabilir).");
            }
        }
    }


    //Admin çalışan silebilir.
    public void deleteStaff(UserManager um, int staffID) {
        if (um != null) {
            boolean basarili = um.deleteUser(staffID);
            
            if (basarili) {
                System.out.println("Admin: Personel silindi (ID: " + staffID + ")");
            } else {
                System.out.println("Hata: Personel bulunamadı.");
            }
        }
    }

 
    //Tüm çalışanlar gösterilir.
    public void viewAllStaff(UserManager um) {
        if (um != null) {
            System.out.println("--- Personel Listesi (Admin Görünümü) ---");
            List<Staff> staffList = um.getAllStaff();
            
            if (staffList.isEmpty()) {
                System.out.println("Sistemde kayıtlı personel yok.");
            } else {
                for (Staff s : staffList) {
                    System.out.println(s.toString());
                }
            }
        }
    }

    
    //ID' ye göre çalışan aranır.
    public void viewStaffById(UserManager um, int staffID) {
        if (um != null) {
            System.out.println("--- Personel Arama Sonucu (ID: " + staffID + ") ---");
            
            User user = um.getUserById(staffID);
            
            if (user != null && user instanceof Staff) {
                System.out.println(user.toString());
            } else {
                System.out.println("Hata: Bu ID'ye sahip bir personel bulunamadı.");
            }
        }
    }
    
    

    //Admin Staff'ı güncelleyebilir.
    public void updateStaffSalary(UserManager um, int staffID, double newSalary) {
        if (um != null) {
            User user = um.getUserById(staffID);
            
            if (user != null && user instanceof Staff) {
                Staff s = (Staff) user;
                s.setSalary(newSalary); 
                
                um.updateUser(s);       
                System.out.println("Admin: Personel maaşı güncellendi -> " + newSalary + " TL");
            } else {
                System.out.println("Hata: Bu ID'ye sahip bir personel bulunamadı.");
            }
        }
    }


    //Yeni uçuş eklenir.
    public void addNewFlight(FlightManager fm, Flight flight) {
        if (fm != null) {
            if (fm.createFlight(flight)) {
                System.out.println("Admin: Uçuş eklendi -> " + flight.getFlightNum());
            } else {
                System.out.println("Hata: Uçuş eklenemedi.");
            }
        }
    }

    
    //Uçuş silinir.
    public void removeFlight(FlightManager fm, ReservationManager rm, Flight flight) {
        if (fm != null && flight != null) {
            if (fm.deleteFlight(flight.getFlightNum(), rm)) {
                System.out.println("Admin: Uçuş silindi -> " + flight.getFlightNum());
            } else {
                System.out.println("Hata: Uçuş silinemedi.");
            }
        }
    }


    //Uçuş güncellenir.
    public void updateFlightTime(FlightManager fm, ReservationManager rm, Flight flight, LocalDateTime newDate) {
        if (fm != null && flight != null) {
            
            flight.setDate(newDate.toLocalDate());
            flight.setHour(newDate.toLocalTime());
            
            boolean flightUpdated = fm.updateFlight(flight);
            
            if (flightUpdated) {
                System.out.println("Sistem: Uçuş saati ana listede güncellendi.");
                
                if (rm != null) {
                    rm.updateFlightInfoInReservations(flight);
                }
                
                
                Management.TicketFileManager.updateTicketsForFlight(flight);
                
                System.out.println("İşlem Tamamlandı: İlgili tüm kayıtlar senkronize edildi.");
                
            } else {
                System.out.println("Hata: Uçuş ana listede güncellenemedi.");
            }
        }
    }
    @Override
    public String toString() {
        return super.toString() + " [YETKİ: YÖNETİCİ]";
    }
}
