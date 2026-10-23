package ru.mirea.postamat.repository;

import ru.mirea.postamat.model.Role;
import ru.mirea.postamat.model.User;
import ru.mirea.postamat.util.Db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepoImpl implements UserRepo {

    private static final String SELECT_COLS = "SELECT id, full_name, phone, email, password_hash, role FROM users ";

    @Override
    public List<User> getAll() {
        return queryAll(SELECT_COLS + "ORDER BY id");
    }

    @Override
    public User getById(int id) {
        String sql = SELECT_COLS + "WHERE id = ?";
        try (Connection c = Db.getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске пользователя: " + e.getMessage(), e);
        }
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (full_name, phone, email, password_hash, role) " + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Db.getConn();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());

            if (user.getEmail() == null) ps.setNull(3, Types.VARCHAR);
            else ps.setString(3, user.getEmail());

            ps.setString(4, user.getPasswordHash() == null ? "" : user.getPasswordHash());
            ps.setString(5, user.getRole() == null ? Role.USER.name() : user.getRole().name());

            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении пользователя: " + e.getMessage(), e);
        }
    }

    @Override
    public void update(User user) {
        boolean changePassword = user.hasPassword();

        String sql = changePassword
                ? "UPDATE users SET full_name=?, phone=?, email=?, password_hash=?, role=? WHERE id=?"
                : "UPDATE users SET full_name=?, phone=?, email=?, role=? WHERE id=?";

        try (Connection c = Db.getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {

            int i = 1;
            ps.setString(i++, user.getFullName());
            ps.setString(i++, user.getPhone());
            if (user.getEmail() == null) ps.setNull(i++, Types.VARCHAR);
            else ps.setString(i++, user.getEmail());

            if (changePassword) ps.setString(i++, user.getPasswordHash());
            ps.setString(i++, user.getRole() == null ? Role.USER.name() : user.getRole().name());
            ps.setInt(i, user.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении пользователя: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        try (Connection c = Db.getConn();
             PreparedStatement ps = c.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении пользователя: " + e.getMessage(), e);
        }
    }

    @Override
    public List<User> getByPhone(String phone)   { return searchLike("phone", phone); }
    @Override
    public List<User> getByFullName(String fio) { return searchLike("full_name", fio); }
    @Override
    public List<User> getByEmail(String email)   { return searchLike("email", email); }

    @Override
    public User getByPhoneExact(String phone)   { return findExact("phone", phone); }
    @Override
    public User get_by_email_exact(String email)   { return findExact("email", email); }

    @Override
    public boolean phoneExists(String phone) {
        return exists("phone", phone, -1);
    }
    @Override
    public boolean phoneExistsForOther(String phone, int excludeId) {
        return exists("phone", phone, excludeId);
    }
    @Override
    public boolean emailExists(String email) {
        if (email == null || email.isBlank()) return false;
        return exists("email", email, -1);
    }
    @Override
    public boolean emailExistsForOther(String email, int excludeId) {
        if (email == null || email.isBlank()) return false;
        return exists("email", email, excludeId);
    }

    @Override
    public List<User> sortByFullName() { return queryAll(SELECT_COLS + "ORDER BY full_name, id"); }
    @Override
    public List<User> sortByPhone()     { return queryAll(SELECT_COLS + "ORDER BY phone, id"); }

    private List<User> queryAll(String sql) {
        List<User> users = new ArrayList<>();
        try (Connection c = Db.getConn();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) users.add(mapRow(rs));
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пользователей: " + e.getMessage(), e);
        }
        return users;
    }

    private List<User> searchLike(String column, String value) {
        List<User> users = new ArrayList<>();
        if (value == null || value.isBlank()) return users;

        String sql = SELECT_COLS + "WHERE " + column + " ILIKE ? ORDER BY id";
        try (Connection c = Db.getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + value.trim() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) users.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по " + column + ": " + e.getMessage(), e);
        }
        return users;
    }

    private User findExact(String column, String value) {
        if (value == null || value.isBlank()) return null;
        String sql = SELECT_COLS + "WHERE " + column + " = ? LIMIT 1";
        try (Connection c = Db.getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, value.trim());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по " + column + ": " + e.getMessage(), e);
        }
    }

    private boolean exists(String column, String value, int excludeId) {
        if (value == null || value.isBlank()) return false;

        String sql = excludeId >= 0
                ? "SELECT 1 FROM users WHERE " + column + " = ? AND id <> ? LIMIT 1"
                : "SELECT 1 FROM users WHERE " + column + " = ? LIMIT 1";

        try (Connection c = Db.getConn();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, value.trim());
            if (excludeId >= 0) ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка проверки уникальности " + column + ": " + e.getMessage(), e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        String roleStr = rs.getString("role");
        Role role = (roleStr == null || roleStr.isBlank()) ? Role.USER : Role.valueOf(roleStr);
        return new User(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("password_hash"),
                role
        );
    }
}