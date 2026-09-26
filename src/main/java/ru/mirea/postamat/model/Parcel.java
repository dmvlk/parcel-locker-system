package ru.mirea.postamat.model;

import java.time.LocalDateTime;

public class Parcel {

    private int id;
    private String code;
    private int user_id;
    private Integer cell_id;
    private ParcelStatus status;
    private LocalDateTime created_at;

    public Parcel() {}

public Parcel(String code, int user_id, Integer cell_id, ParcelStatus status) {
    this.code = code;
    this.user_id = user_id;
    this.cell_id = cell_id;
    this.status = status;
}

public Parcel(int id, String code, int user_id, Integer cell_id,
              ParcelStatus status, LocalDateTime created_at) {
    this.id = id;
    this.code = code;
    this.user_id = user_id;
    this.cell_id = cell_id;
    this.status = status;
    this.created_at = created_at;
}

public int get_id() { return id; }
public void set_id(int id) { this.id = id; }

public String get_code() { return code; }
public void set_code(String code) { this.code = code; }

public int get_user_id() { return user_id; }
public void set_user_id(int user_id) { this.user_id = user_id; }

public Integer get_cell_id() { return cell_id; }
public void set_cell_id(Integer cell_id) { this.cell_id = cell_id; }

public ParcelStatus get_status() { return status; }
public void set_status(ParcelStatus status) { this.status = status; }

public LocalDateTime get_created_at() { return created_at; }
public void set_created_at(LocalDateTime created_at) { this.created_at = created_at; }

@Override
public String toString() {
    return "Parcel{" +
            "id=" + id +
            ", code='" + code + '\'' +
            ", user_id=" + user_id +
            ", cell_id=" + cell_id +
            ", status=" + status +
            ", created_at=" + created_at +
            '}';
}
}