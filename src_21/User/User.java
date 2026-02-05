package User;

import java.io.Serializable;
import java.util.Random;

public abstract class User implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int userID;
    private String username;
    private String password;
    private String name;
    private String surname;

    public User(String username, String password, String name, String surname) {
    	Random random = new Random();
        this.userID = 1000 + random.nextInt(99000);
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
    }

    public int getUserID() {
    	return userID; 
    
    }
    public String getUsername() {
    	return username; 
    }
    public String getPassword() { 
    	return password;
    }
    public String getName() { 
    	return name;
    }
    public String getSurname() { 
    	return surname; 
    }

    public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Override
    public String toString() {
        return name + " " + surname + " (" + username + ")";
    }
}


