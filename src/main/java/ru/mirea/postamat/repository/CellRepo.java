package ru.mirea.postamat.repository;

import ru.mirea.postamat.model.Cell;
import ru.mirea.postamat.model.CellStatus;
import ru.mirea.postamat.model.SizeType;
import java.util.List;

public interface CellRepo {

    List<Cell> get_all();
    Cell get_by_id(int id);
    void save(Cell cell);
    void update(Cell cell);
    void delete(int id);

    List<Cell> get_by_address(String address);
    List<String> get_all_addresses();
    List<Cell> get_by_cell_number(int cell_number);

    List<Cell> filter_by_status(CellStatus status);
    List<Cell> filter_by_size(SizeType size);

    List<Cell> sort_by_address();
    List<Cell> sort_by_cell_number();

    Cell find_free_cell(String locker_address, SizeType size);
}