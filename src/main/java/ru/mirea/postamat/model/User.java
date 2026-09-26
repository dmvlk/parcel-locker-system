package ru.mirea.postamat.model;

public class User {

    private int id;
    private String full_name;
    private String phone;
    private String email;

    public User() {}

    public User(String full_name, String phone, String email) {
        this.full_name = full_name;
        this.phone = phone;
        this.email = email;
    }

    public User(int id, String full_name, String phone, String email) {
        this.id = id;
        this.full_name = full_name;
        this.phone = phone;
        this.email = email;
    }

    public int get_id() { return id; }
    public void set_id(int id) { this.id = id; }

    public String get_full_name() { return full_name; }
    public void set_full_name(String full_name) { this.full_name = full_name; }

    public String get_phone() { return phone; }
    public void set_phone(String phone) { this.phone = phone; }

    public String get_email() { return email; }
    public void set_email(String email) { this.email = email; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", full_name='" + full_name + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}