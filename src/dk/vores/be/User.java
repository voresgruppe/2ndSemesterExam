package dk.vores.be;

import javafx.beans.property.SimpleStringProperty;

public class User {
    private int id;
    private String username;
    private String password;
    private boolean isAdmin;

    public User(int id, String username, String password, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public int getId() {
        return id;
    }

    public SimpleStringProperty getIdProperty(){
        return new SimpleStringProperty(String.valueOf(id));
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public SimpleStringProperty getUsernameProperty(){
        return new SimpleStringProperty(username);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public SimpleStringProperty getPasswordProperty(){
        return new SimpleStringProperty(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
