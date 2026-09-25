package ru.university.postamat.model;

import java.time.LocalDateTime;

/** Пользователь — получатель отправлений. */
public class User {

    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private LocalDateTime registeredAt;

    public User() { }

    public User(String fullName, String phone, String email) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    @Override
    public String toString() {
        return String.format("Пользователь №%d: %s, тел. %s, %s", id, fullName, phone, email);
    }
}