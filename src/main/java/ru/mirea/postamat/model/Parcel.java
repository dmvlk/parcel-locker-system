package ru.mirea.postamat.model;

import java.time.LocalDateTime;

public class Parcel {

    private int id;
    private String code;
    private int userId;
    private Integer cellId;
    private ParcelStatus status;
    private LocalDateTime createdAt;

    public Parcel() {}

    public Parcel(String code, int userId, Integer cellId, ParcelStatus status) {
        this.code = code;
        this.userId = userId;
        this.cellId = cellId;
        this.status = status;
    }

    public Parcel(int id, String code, int userId, Integer cellId, ParcelStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.code = code;
        this.userId = userId;
        this.cellId = cellId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Integer getCellId() {
        return cellId;
    }

    public void setCellId(Integer cellId) {
        this.cellId = cellId;
    }

    public ParcelStatus getStatus() {
        return status;
    }

    public void setStatus(ParcelStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Parcel{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", user_id=" + userId +
                ", cell_id=" + cellId +
                ", status=" + status +
                ", created_at=" + createdAt +
                '}';
    }
}