package ru.university.postamat.repository;

import ru.university.postamat.exception.BusinessException;
import ru.university.postamat.model.User;
import ru.university.postamat.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements CrudRepository<User> {

    public User create(User user) {
        String sql = "INSERT INTO users (full_name, phone, email) VALUES (?, ?, ?) RETURNING id, registered_at";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getEmail());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                    user.setRegisteredAt(rs.getTimestamp("registered_at").toLocalDateTime());
                }
                return user;
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при создании пользователя: " + e.getMessage());
        }
    }

    public Optional<User> findById(long id) {
        String sql = "SELECT id, full_name, phone, email, registered_at FROM users WHERE id = ?";
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
            throw new BusinessException("Ошибка базы данных при поиске пользователя: " + e.getMessage());
        }
    }

    public List<User> findAll() {
        String sql = "SELECT id, full_name, phone, email, registered_at FROM users ORDER BY id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при чтении пользователей: " + e.getMessage());
        }
    }

    public boolean update(User user) {
        String sql = "UPDATE users SET full_name = ?, phone = ?, email = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getEmail());
            ps.setLong(4, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при обновлении пользователя: " + e.getMessage());
        }
    }

    public boolean deleteById(long id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при удалении пользователя: " + e.getMessage());
        }
    }

    // проверяем, что телефон или email заняты другим пользователем (excludeId - чтобы при редактировании не сравнивать с самим собой)
    public boolean existsByPhoneOrEmail(String phone, String email, long excludeId) {
        String sql = "SELECT 1 FROM users WHERE (phone = ? OR email = ?) AND id <> ? LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setString(2, email);
            ps.setLong(3, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при проверке уникальности: " + e.getMessage());
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setFullName(rs.getString("full_name"));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setRegisteredAt(rs.getTimestamp("registered_at").toLocalDateTime());
        return user;
    }
}