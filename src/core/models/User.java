package core.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class User {

    private final String id;
    private String firstName;
    private String lastName;
    private final String email;
    private String phone;
    private String password;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private final List<Reunion> reunionsHistory;

    public User(String id, String firstName, String lastName, String email,
                String phone, String password, LocalDateTime createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
        this.reunionsHistory = new ArrayList<>();
    }



    public String getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<Reunion> getReunionsHistory() { return reunionsHistory; }



    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPassword(String password) { this.password = password; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return String.format("User{id='%s', name='%s %s', email='%s'}", id, firstName, lastName, email);
    }
}
