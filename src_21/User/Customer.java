package User;

import java.io.Serializable;

public class Customer extends User implements Serializable {

    private static final long serialVersionUID = 1L;
    private String contactInfo; 

    public Customer(String username, String password, String name, String surname, String contactInfo) {
        super(username, password, name, surname);
        this.contactInfo = contactInfo;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    @Override
    public String toString() {
        return super.toString() + " [Tel: " + contactInfo + "]";
    }
}