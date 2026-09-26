package ru.mirea.postamat.service;

import ru.mirea.postamat.exception.BusinessException;
import ru.mirea.postamat.exception.NotFoundException;
import ru.mirea.postamat.model.Cell;
import ru.mirea.postamat.model.CellStatus;
import ru.mirea.postamat.model.SizeType;
import ru.mirea.postamat.repository.CellRepo;
import java.util.List;

public class CellService {

    private final CellRepo cell_repo;

    public CellService(CellRepo cell_repo) {
        this.cell_repo = cell_repo;
    }

    public List<Cell> get_all() {
        return cell_repo.get_all();
    }

    public Cell get_by_id(int id) {
        Cell cell = cell_repo.get_by_id(id);
        if (cell == null) {
            throw new NotFoundException("Ячейка с ID=" + id + " не найдена");
        }
        return cell;
    }

    public void create(String locker_address, int cell_number,
                       SizeType size_type, CellStatus status) {

        // Правило: адрес не может быть пустым
        if (locker_address == null || locker_address.trim().isEmpty()) {
            throw new BusinessException("Адрес постамата не может быть пустым");
        }

        Cell cell = new Cell(locker_address.trim(), cell_number, size_type, status);
        cell_repo.save(cell);
    }

    public void update(int id, String locker_address, int cell_number,
                       SizeType size_type, CellStatus status) {

        if (locker_address == null || locker_address.trim().isEmpty()) {
            throw new BusinessException("Адрес постамата не может быть пустым");
        }

        Cell cell = get_by_id(id);

        cell.set_locker_address(locker_address.trim());
        cell.set_cell_number(cell_number);
        cell.set_size_type(size_type);
        cell.set_status(status);

        cell_repo.update(cell);
    }

    public void delete(int id) {
        get_by_id(id);
        cell_repo.delete(id);
    }

    public List<Cell> search_by_address(String address) {
        return cell_repo.get_by_address(address);
    }

    public List<String> get_all_addresses() {
        return cell_repo.get_all_addresses();
    }

    public List<Cell> search_by_cell_number(int cell_number) {
        return cell_repo.get_by_cell_number(cell_number);
    }

    public List<Cell> filter_by_status(CellStatus status) {
        return cell_repo.filter_by_status(status);
    }

    public List<Cell> filter_by_size(SizeType size) {
        return cell_repo.filter_by_size(size);
    }

    public List<Cell> sort_by_address() {
        return cell_repo.sort_by_address();
    }

    public List<Cell> sort_by_cell_number() {
        return cell_repo.sort_by_cell_number();
    }

    public int[] get_stats() {
        List<Cell> all = cell_repo.get_all();
        int total = all.size();
        int free = 0, occupied = 0, not_working = 0;

        for (Cell c : all) {
            switch (c.get_status()) {
                case FREE -> free++;
                case OCCUPIED -> occupied++;
                case NOT_WORKING -> not_working++;
            }
        }
        return new int[]{total, free, occupied, not_working};
    }
}