package ru.mirea.postamat.repository;

import ru.mirea.postamat.model.Parcel;
import ru.mirea.postamat.model.ParcelStatus;
import ru.mirea.postamat.util.Db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParcelRepoImpl implements ParcelRepo {

    @Override
    public List<Parcel> get_all() {
        List<Parcel> parcels = new ArrayList<>();
        String sql = "SELECT id, code, user_id, cell_id, status, created_at " +
                "FROM parcels ORDER BY id";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                parcels.add(map_row(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении посылок: " + e.getMessage(), e);
        }
        return parcels;
    }

    @Override
    public Parcel get_by_id(int id) {
        String sql = "SELECT id, code, user_id, cell_id, status, created_at " +
                "FROM parcels WHERE id = ?";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map_row(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске посылки: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void save(Parcel parcel) {
        String sql = "INSERT INTO parcels (code, user_id, cell_id, status) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, parcel.get_code());
            ps.setInt(2, parcel.get_user_id());

            if (parcel.get_cell_id() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, parcel.get_cell_id());
            }

            ps.setString(4, parcel.get_status().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    parcel.set_id(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении посылки: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(Parcel parcel) {
        String sql = "UPDATE parcels SET code = ?, user_id = ?, cell_id = ?, status = ? " +
                "WHERE id = ?";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, parcel.get_code());
            ps.setInt(2, parcel.get_user_id());

            if (parcel.get_cell_id() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, parcel.get_cell_id());
            }

            ps.setString(4, parcel.get_status().name());
            ps.setInt(5, parcel.get_id());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении посылки: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM parcels WHERE id = ?";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении посылки: " + e.getMessage(), e);
        }
    }

    @Override
    public Parcel get_by_code(String code) {
        String sql = "SELECT id, code, user_id, cell_id, status, created_at " +
                "FROM parcels WHERE code = ?";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map_row(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по коду: " + e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean code_exists(String code) {
        String sql = "SELECT 1 FROM parcels WHERE code = ?";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка проверки кода: " + e.getMessage(), e);
        }
    }

    private Parcel map_row(ResultSet rs) throws SQLException {
        int cell_id_raw = rs.getInt("cell_id");
        Integer cell_id = rs.wasNull() ? null : cell_id_raw;

        Timestamp ts = rs.getTimestamp("created_at");

        return new Parcel(
                rs.getInt("id"),
                rs.getString("code"),
                rs.getInt("user_id"),
                cell_id,
                ParcelStatus.valueOf(rs.getString("status")),
                ts == null ? null : ts.toLocalDateTime()
        );
    }
}