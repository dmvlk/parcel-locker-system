package ru.mirea.postamat.repository;

import ru.mirea.postamat.model.Cell;
import ru.mirea.postamat.model.CellStatus;
import ru.mirea.postamat.model.SizeType;
import java.util.List;

public interface CellRepo {

    List<Cell> getAll();
    Cell getById(int id);
    void save(Cell cell);
    void update(Cell cell);
    void delete(int id);

    List<Cell> getByAddress(String address);
    List<String> getAllAddresses();
    List<Cell> getByCellNumber(int cellNumber);

    List<Cell> filterByStatus(CellStatus status);
    List<Cell> filterBySize(SizeType size);

    List<Cell> sortByAddress();
    List<Cell> sortByCellNumber();

    Cell findFreeCell(String lockerAddress, SizeType size);
}