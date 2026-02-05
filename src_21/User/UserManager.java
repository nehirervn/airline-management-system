package User;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private static UserManager instance;
    private static final String FILE_NAME = "users.dat";
    
    public UserManager() {
    }
    
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }

    //Giriş yaparken kontrol işlemi yapılır.
    public User authenticate(String username, String password) {
        List<User> users = getAllUsers(); 
        for (User u : users) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
                return u; // Giriş başarılı
            }
        }
        return null; // Başarısız
    }

   //Kullanıcı ekleme 
    public boolean addUser(User newUser) {
        if (newUser == null) return false;

        List<User> users = getAllUsers();

        for (User u : users) {
            if (u.getUsername().equals(newUser.getUsername())) {
                System.out.println("Hata: Bu kullanıcı adı ('" + newUser.getUsername() + "') zaten alınmış.");
                return false;
            }
        }

        if (newUser instanceof Customer) {
            Customer newCustomer = (Customer) newUser;
            String newPhone = newCustomer.getContactInfo();

            for (User u : users) {
                if (u instanceof Customer) {
                    Customer existingCustomer = (Customer) u;
                    String existingPhone = existingCustomer.getContactInfo();
                    
                    if (existingPhone != null && existingPhone.equals(newPhone)) {
                        System.out.println("Hata: Bu telefon numarası ('" + newPhone + "') zaten sistemde kayıtlı.");
                        return false; 
                    }
                }
            }
        }

        users.add(newUser);
        saveUsersToFile(users); 
        return true;
    }

    public boolean deleteUser(int userId) {
        List<User> users = getAllUsers();
        
        boolean removed = users.removeIf(u -> u.getUserID() == userId);
        
        if (removed) {
            saveUsersToFile(users); 
            System.out.println("Kullanıcı (ID: " + userId + ") sistemden silindi.");
        }
        return removed;
    }


    public boolean updateUser(User updatedUser) {
        List<User> users = getAllUsers();
        
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID() == updatedUser.getUserID()) {
                users.set(i, updatedUser); 
                saveUsersToFile(users);    
                return true;
            }
        }
        return false;
    }


    public List<Staff> getAllStaff() {
        List<User> allUsers = getAllUsers();
        List<Staff> staffList = new ArrayList<>();

        for (User u : allUsers) {
            if (u instanceof Staff) {
                staffList.add((Staff) u);
            }
        }
        return staffList;
    }


    public User getUserById(int id) {
        List<User> users = getAllUsers();
        
        for (User u : users) {
            if (u.getUserID() == id) {
                return u; 
            }
        }
        return null; 
    }
    
    
    //Users.dat dosyasından veiler çekilir.
    @SuppressWarnings("unchecked")
    public List<User> getAllUsers() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>(); 
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    
    //users.dat dosyasına bilgiler yazılır.
    private void saveUsersToFile(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}