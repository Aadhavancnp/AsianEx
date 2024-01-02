package com.tourism.asianex.Models;

import javafx.beans.property.*;
import org.bson.Document;

import java.time.LocalDate;
import java.util.List;

public class User {

    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty email = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty("");
    private final ObjectProperty<LocalDate> dateOFBirth = new SimpleObjectProperty<>();
    private final StringProperty phone = new SimpleStringProperty("");
    private final ObjectProperty<Role> role = new SimpleObjectProperty<>();

    public User(int id, String name, String email, String password, LocalDate dateOfBirth, String phone, Role role) {
        setId(id);
        setName(name);
        setEmail(email);
        setPassword(password);
        setDateOFBirth(dateOfBirth);
        setPhone(phone);
        setRole(role);
    }

    public static List<User> getUsers() {
        return List.of(
                new User(1, "admin", "admin@gmail.com", "abcd", LocalDate.now(), "1234567890", Role.ADMIN),
                new User(2, "anna", "abc@gmail.com", "abcd", LocalDate.now(), "2314567890", Role.USER),
                new User(3, "john", "abcde@gmail.com", "abcd", LocalDate.now(), "3214567890", Role.USER),
                new User(4, "jane", "efgh@gmail.com", "abcd", LocalDate.now(), "7356128910", Role.USER)
        );
    }

    public static List<User> fromDocuments(List<Document> documents) {
        for (int i = 0; i < documents.size(); i++) {
            documents.get(i).put("_id", i);
        }
        return documents.stream().map(document -> new User(
                document.getInteger("_id"),
                document.getString("name"),
                document.getString("email"),
                document.getString("password"),
                document.getString("dateOfBirth") == null || document.getString("dateOfBirth").isEmpty() ? null : LocalDate.parse(document.getString("dateOfBirth")),
                document.getString("phone"),
                Role.valueOf(document.getString("role"))
        )).toList();
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public LocalDate getDateOFBirth() {
        return dateOFBirth.get();
    }

    public void setDateOFBirth(LocalDate dateOFBirth) {
        this.dateOFBirth.set(dateOFBirth);
    }

    public String getPhone() {
        return phone.get();
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public Role getRole() {
        return role.get();
    }

    public void setRole(Role role) {
        this.role.set(role);
    }

    @Override

    public String toString() {
        return "User{" +
                "id=" + id +
                ", name=" + name +
                ", email=" + email +
                ", password=" + password +
                ", dateOFBirth=" + dateOFBirth +
                ", phone=" + phone +
                ", role=" + role +
                '}';
    }

}
