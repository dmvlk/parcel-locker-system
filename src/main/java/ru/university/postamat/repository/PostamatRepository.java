package ru.university.postamat.repository;

import ru.university.postamat.exception.BusinessException;
import ru.university.postamat.model.Postamat;
import ru.university.postamat.model.PostamatStatus;
import ru.university.postamat.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PostamatRepository implements CrudRepository<Postamat> {

    public Postamat create(Postamat postamat) {
        String sql = "INSERT INTO postamats (address, capacity, free_cells, status) VALUES (?, ?, ?, ?) RETURNING id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, postamat.getAddress());
            ps.setInt(2, postamat.getCapacity());
            ps.setInt(3, postamat.getFreeCells());
            ps.setString(4, postamat.getStatus().name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    postamat.setId(rs.getLong("id"));
                }
                return postamat;
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при создании постамата: " + e.getMessage());
        }
    }

    public Optional<Postamat> findById(long id) {
        String sql = "SELECT id, address, capacity, free_cells, status FROM postamats WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при поиске постамата: " + e.getMessage());
        }
    }

    public List<Postamat> findAll() {
        String sql = "SELECT id, address, capacity, free_cells, status FROM postamats ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<Postamat> postamats = new ArrayList<>();
            while (rs.next()) {
                postamats.add(mapRow(rs));
            }
            return postamats;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при чтении постаматов: " + e.getMessage());
        }
    }

    public boolean update(Postamat postamat) {
        String sql = "UPDATE postamats SET address = ?, capacity = ?, free_cells = ?, status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, postamat.getAddress());
            ps.setInt(2, postamat.getCapacity());
            ps.setInt(3, postamat.getFreeCells());
            ps.setString(4, postamat.getStatus().name());
            ps.setLong(5, postamat.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при обновлении постамата: " + e.getMessage());
        }
    }

    public boolean deleteById(long id) {
        String sql = "DELETE FROM postamats WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при удалении постамата: " + e.getMessage());
        }
    }

    // проверяем, что другой постамат с таким адресом уже есть
    public boolean existsByAddress(String address, long excludeId) {
        String sql = "SELECT 1 FROM postamats WHERE address = ? AND id <> ? LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, address);
            ps.setLong(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при проверке адреса: " + e.getMessage());
        }
    }

    private Postamat mapRow(ResultSet rs) throws SQLException {
        Postamat postamat = new Postamat();
        postamat.setId(rs.getLong("id"));
        postamat.setAddress(rs.getString("address"));
        postamat.setCapacity(rs.getInt("capacity"));
        postamat.setFreeCells(rs.getInt("free_cells"));
        postamat.setStatus(PostamatStatus.valueOf(rs.getString("status")));
        return postamat;
    }
}