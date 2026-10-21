package ru.mirea.postamat.model;

public class User {
    private int id;
    private String full_name;
    private String phone;
    private String email;
    private String password_hash;
    private Role role;

    public User() {}

    public User(String full_name, String phone, String email,
                String password_hash, Role role) {
        this.full_name = full_name;
        this.phone = phone;
        this.email = email;
        this.password_hash = password_hash;
        this.role = role;
    }

    public User(int id, String full_name, String phone, String email,
                String password_hash, Role role) {
        this.id = id;
        this.full_name = full_name;
        this.phone = phone;
        this.email = email;
        this.password_hash = password_hash;
        this.role = role;
    }

    public int get_id() { return id; }
    public void set_id(int id) { this.id = id; }

    public String get_full_name() { return full_name; }
    public void set_full_name(String v) { this.full_name = v; }

    public String get_phone() { return phone; }
    public void set_phone(String v) { this.phone = v; }

    public String get_email() { return email; }
    public void set_email(String v) { this.email = v; }

    public String get_password_hash() { return password_hash; }
    public void set_password_hash(String v) { this.password_hash = v; }

    public Role get_role() { return role; }
    public void set_role(Role v) { this.role = v; }

    public String getPasswordHash() { return password_hash; }
    public void setPasswordHash(String v) { this.password_hash = v; }

    public Role getRole() { return role; }
    public void setRole(Role v) { this.role = v; }

    public boolean is_admin() { return role == Role.ADMIN; }

    public boolean has_password() {
        return password_hash != null && !password_hash.isBlank();
    }

    @Override
    public String toString() {
        return "User{id=" + id +
                ", full_name='" + full_name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", password_hash='<hidden>'}";
    }
}