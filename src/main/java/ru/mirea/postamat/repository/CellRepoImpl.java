package ru.mirea.postamat.repository;

import ru.mirea.postamat.model.Cell;
import ru.mirea.postamat.model.CellStatus;
import ru.mirea.postamat.model.SizeType;
import ru.mirea.postamat.util.Db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CellRepoImpl implements CellRepo {

    @Override
    public List<Cell> getAll() {
        List<Cell> cells = new ArrayList<>();
        String sql = "SELECT id, locker_address, cell_number, size_type, status " + "FROM cells ORDER BY id";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cells.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении ячеек: " + e.getMessage(), e);
        }
        return cells;
    }

    @Override
    public Cell getById(int id) {
        String sql = "SELECT id, locker_address, cell_number, size_type, status " + "FROM cells WHERE id = ?";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске ячейки: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void save(Cell cell) {
        String sql = "INSERT INTO cells (locker_address, cell_number, size_type, status) " + "VALUES (?, ?, ?, ?)";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cell.getLockerAddress());
            ps.setInt(2, cell.getCellNumber());
            ps.setString(3, cell.getSizeType().name());
            ps.setString(4, cell.getStatus().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    cell.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении ячейки: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Cell cell) {
        String sql = "UPDATE cells SET locker_address = ?, cell_number = ?, " + "size_type = ?, status = ? WHERE id = ?";
        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cell.getLockerAddress());
            ps.setInt(2, cell.getCellNumber());
            ps.setString(3, cell.getSizeType().name());
            ps.setString(4, cell.getStatus().name());
            ps.setInt(5, cell.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении ячейки: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM cells WHERE id = ?";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении ячейки: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Cell> getByAddress(String address) {
        List<Cell> cells = new ArrayList<>();
        String sql = "SELECT id, locker_address, cell_number, size_type, status " + "FROM cells WHERE locker_address LIKE ? ORDER BY id";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + address + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cells.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по адресу: " + e.getMessage(), e);
        }
        return cells;
    }

    @Override
    public List<String> getAllAddresses() {
        List<String> addresses = new ArrayList<>();
        String sql = "SELECT DISTINCT locker_address FROM cells ORDER BY locker_address";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                addresses.add(rs.getString("locker_address"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении адресов: " + e.getMessage(), e);
        }
        return addresses;
    }

    @Override
    public List<Cell> getByCellNumber(int cellNumber) {
        List<Cell> cells = new ArrayList<>();
        String sql = "SELECT id, locker_address, cell_number, size_type, status " + "FROM cells WHERE cell_number = ? ORDER BY id";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, cellNumber);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cells.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по номеру ячейки: " + e.getMessage(), e);
        }
        return cells;
    }

    @Override
    public List<Cell> filterByStatus(CellStatus status) {
        List<Cell> cells = new ArrayList<>();
        String sql = "SELECT id, locker_address, cell_number, size_type, status " + "FROM cells WHERE status = ? ORDER BY id";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cells.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка фильтра по статусу: " + e.getMessage(), e);
        }
        return cells;
    }

    @Override
    public List<Cell> filterBySize(SizeType size) {
        List<Cell> cells = new ArrayList<>();
        String sql = "SELECT id, locker_address, cell_number, size_type, status " + "FROM cells WHERE size_type = ? ORDER BY id";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, size.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) cells.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка фильтра по размеру: " + e.getMessage(), e);
        }
        return cells;
    }

    @Override
    public List<Cell> sortByAddress() {
        List<Cell> cells = new ArrayList<>();
        String sql = "SELECT id, locker_address, cell_number, size_type, status " + "FROM cells ORDER BY locker_address, cell_number";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) cells.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сортировки по адресу: " + e.getMessage(), e);
        }
        return cells;
    }

    @Override
    public List<Cell> sortByCellNumber() {
        List<Cell> cells = new ArrayList<>();
        String sql = "SELECT id, locker_address, cell_number, size_type, status " + "FROM cells ORDER BY cell_number, locker_address";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) cells.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка сортировки по номеру: " + e.getMessage(), e);
        }
        return cells;
    }

    @Override
    public Cell findFreeCell(String lockerAddress, SizeType size) {
        String sql = "SELECT id, locker_address, cell_number, size_type, status " + "FROM cells " + "WHERE locker_address = ? AND size_type = ? AND status = 'FREE' " + "ORDER BY cell_number LIMIT 1";

        try (Connection conn = Db.getConn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, lockerAddress);
            ps.setString(2, size.name());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска свободной ячейки: " + e.getMessage(), e);
        }
        return null;
    }

    private Cell mapRow(ResultSet rs) throws SQLException {
        return new Cell(
                rs.getInt("id"),
                rs.getString("locker_address"),
                rs.getInt("cell_number"),
                SizeType.valueOf(rs.getString("size_type")),
                CellStatus.valueOf(rs.getString("status"))
        );
    }
}