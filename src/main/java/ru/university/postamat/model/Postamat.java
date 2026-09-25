package ru.university.postamat.model;

/** Постамат — терминал с ячейками для хранения посылок. */
public class Postamat {

    private Long id;
    private String address;
    private int capacity;      // всего ячеек
    private int freeCells;     // свободных ячеек
    private PostamatStatus status;

    public Postamat() { }

    /** При создании все ячейки свободны, статус — ACTIVE. */
    public Postamat(String address, int capacity) {
        this.address = address;
        this.capacity = capacity;
        this.freeCells = capacity;
        this.status = PostamatStatus.ACTIVE;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public int getFreeCells() { return freeCells; }
    public void setFreeCells(int freeCells) { this.freeCells = freeCells; }
    public PostamatStatus getStatus() { return status; }
    public void setStatus(PostamatStatus status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Постамат №%d: %s | ячеек %d/%d | %s",
                id, address, capacity - freeCells, capacity, status.getDisplayName());
    }
}