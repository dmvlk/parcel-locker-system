package ru.mirea.postamat.service;

import ru.mirea.postamat.exception.BusinessException;
import ru.mirea.postamat.exception.NotFoundException;
import ru.mirea.postamat.model.Cell;
import ru.mirea.postamat.model.CellStatus;
import ru.mirea.postamat.model.SizeType;
import ru.mirea.postamat.repository.CellRepo;
import java.util.List;

public class CellService {
    private final CellRepo cellRepo;
    public CellService(CellRepo cellRepo) {
        this.cellRepo = cellRepo;
    }
    public List<Cell> getAll() {
        return cellRepo.getAll();
    }
    public Cell getById(int id) {
        Cell cell = cellRepo.getById(id);
        if (cell == null) {
            throw new NotFoundException("Ячейка с ID=" + id + " не найдена");
        }
        return cell;
    }

    public void create(String lockerAddress, int cellNumber, SizeType sizeType, CellStatus status) {

        if (lockerAddress == null || lockerAddress.trim().isEmpty())
            throw new BusinessException("Адрес постамата не может быть пустым");

        Cell cell = new Cell(lockerAddress.trim(), cellNumber, sizeType, status);
        cellRepo.save(cell);
    }

    public void update(int id, String lockerAddress, int cellNumber, SizeType sizeType, CellStatus status) {
        if (lockerAddress == null || lockerAddress.trim().isEmpty())
            throw new BusinessException("Адрес постамата не может быть пустым");

        Cell cell = getById(id);

        cell.setLockerAddress(lockerAddress.trim());
        cell.setCellNumber(cellNumber);
        cell.setSizeType(sizeType);
        cell.setStatus(status);

        cellRepo.update(cell);
    }

    public void delete(int id) {
        getById(id);
        cellRepo.delete(id);
    }

    public List<Cell> searchByAddress(String address) {
        return cellRepo.getByAddress(address);
    }

    public List<String> getAllAddresses() {
        return cellRepo.getAllAddresses();
    }

    public List<Cell> searchByCellNumber(int cell_number) {
        return cellRepo.getByCellNumber(cell_number);
    }

    public List<Cell> filterByStatus(CellStatus status) {
        return cellRepo.filterByStatus(status);
    }

    public List<Cell> filterBySize(SizeType size) {
        return cellRepo.filterBySize(size);
    }

    public List<Cell> sortByAddress() {
        return cellRepo.sortByAddress();
    }

    public List<Cell> sortByCellNumber() {
        return cellRepo.sortByCellNumber();
    }

    public int[] getStats() {
        List<Cell> all = cellRepo.getAll();
        int total = all.size();
        int free = 0, occupied = 0, notWorking = 0;

        for (Cell c : all) {
            switch (c.getStatus()) {
                case FREE -> free++;
                case OCCUPIED -> occupied++;
                case NOT_WORKING -> notWorking++;
            }
        }
        return new int[]{total, free, occupied, notWorking};
    }
}