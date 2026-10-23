package ru.mirea.postamat.model;

public class User {
    private int id;
    private String fullName;
    private String phone;
    private String email;
    private String passwordHash;
    private Role role;

    public User() {}

    public User(String fullName, String phone, String email, String passwordHash, Role role) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(int id, String fullName, String phone, String email, String passwordHash, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }

    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }

    public String getEmail() { return email; }
    public void setEmail(String v) { this.email = v; }

    public Role getRole() { return role; }
    public void setRole(Role v) { this.role = v; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String v) { this.passwordHash = v; }

    public boolean isAdmin() { return role == Role.ADMIN; }

    public boolean hasPassword() {
        return passwordHash != null && !passwordHash.isBlank();
    }

    @Override
    public String toString() {
        return "User{id=" + id +
                ", full_name='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", password_hash='<hidden>'}";
    }
}