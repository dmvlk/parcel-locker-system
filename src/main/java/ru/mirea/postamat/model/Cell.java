package ru.mirea.postamat.model;

public class Cell {

    private int id;
    private String lockerAddress;
    private int cellNumber;
    private SizeType sizeType;
    private CellStatus status;


    public Cell() {}

    public Cell(String lockerAddress, int cellNumber, SizeType sizeType, CellStatus status) {
        this.lockerAddress = lockerAddress;
        this.cellNumber = cellNumber;
        this.sizeType = sizeType;
        this.status = status;
    }

    public Cell(int id, String lockerAddress, int cellNumber, SizeType sizeType, CellStatus status) {
        this.id = id;
        this.lockerAddress = lockerAddress;
        this.cellNumber = cellNumber;
        this.sizeType = sizeType;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLockerAddress() { return lockerAddress; }
    public void setLockerAddress(String lockerAddress) { this.lockerAddress = lockerAddress; }

    public int getCellNumber() { return cellNumber; }
    public void setCellNumber(int cellNumber) { this.cellNumber = cellNumber; }

    public SizeType getSizeType() { return sizeType; }
    public void setSizeType(SizeType sizeType) { this.sizeType = sizeType; }

    public CellStatus getStatus() { return status; }
    public void setStatus(CellStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Cell{" +
                "id=" + id +
                ", locker_address='" + lockerAddress + '\'' +
                ", cell_number=" + cellNumber +
                ", size_type=" + sizeType +
                ", status=" + status +
                '}';
    }
}