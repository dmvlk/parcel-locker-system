package ru.mirea.postamat.model;

public class Cell {

    private int id;
    private String locker_address;
    private int cell_number;
    private SizeType size_type;
    private CellStatus status;

    public Cell() {}

    public Cell(String locker_address, int cell_number,
                SizeType size_type, CellStatus status) {
        this.locker_address = locker_address;
        this.cell_number = cell_number;
        this.size_type = size_type;
        this.status = status;
    }

    public Cell(int id, String locker_address, int cell_number,
                SizeType size_type, CellStatus status) {
        this.id = id;
        this.locker_address = locker_address;
        this.cell_number = cell_number;
        this.size_type = size_type;
        this.status = status;
    }

    public int get_id() { return id; }
    public void set_id(int id) { this.id = id; }

    public String get_locker_address() { return locker_address; }
    public void set_locker_address(String locker_address) { this.locker_address = locker_address; }

    public int get_cell_number() { return cell_number; }
    public void set_cell_number(int cell_number) { this.cell_number = cell_number; }

    public SizeType get_size_type() { return size_type; }
    public void set_size_type(SizeType size_type) { this.size_type = size_type; }

    public CellStatus get_status() { return status; }
    public void set_status(CellStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Cell{" +
                "id=" + id +
                ", locker_address='" + locker_address + '\'' +
                ", cell_number=" + cell_number +
                ", size_type=" + size_type +
                ", status=" + status +
                '}';
    }
}