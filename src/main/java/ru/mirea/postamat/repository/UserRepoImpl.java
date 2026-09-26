package ru.mirea.postamat.repository;

import ru.mirea.postamat.model.User;
import ru.mirea.postamat.util.Db;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepoImpl implements UserRepo {

    // SELECT всех пользователей
    @Override
    public List<User> get_all() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, full_name, phone, email FROM users ORDER BY id";

        // try-with-resources: соединение, statement и result set закроются сами
        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(map_row(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при получении пользователей: " + e.getMessage(), e);
        }
        return users;
    }

    // SELECT одного пользователя по ID
    @Override
    public User get_by_id(int id) {
        String sql = "SELECT id, full_name, phone, email FROM users WHERE id = ?";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id); // подставляем id вместо первого ?

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map_row(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при поиске пользователя: " + e.getMessage(), e);
        }
        return null; // не нашли
    }

    // INSERT нового пользователя
    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (full_name, phone, email) VALUES (?, ?, ?)";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.get_full_name());
            ps.setString(2, user.get_phone());
            ps.setString(3, user.get_email());

            ps.executeUpdate();

            // Забираем id, который присвоила БД
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.set_id(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при сохранении пользователя: " + e.getMessage(), e);
        }
    }

    // UPDATE
    @Override
    public void update(User user) {
        String sql = "UPDATE users SET full_name = ?, phone = ?, email = ? WHERE id = ?";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.get_full_name());
            ps.setString(2, user.get_phone());
            ps.setString(3, user.get_email());
            ps.setInt(4, user.get_id());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении пользователя: " + e.getMessage(), e);
        }
    }

    // DELETE
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении пользователя: " + e.getMessage(), e);
        }
    }

    // Поиск по телефону (частичное совпадение)
    @Override
    public List<User> get_by_phone(String phone) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, full_name, phone, email FROM users WHERE phone LIKE ? ORDER BY id";

        try (Connection conn = Db.get_conn();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + phone + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(map_row(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка поиска по телефону: " + e.getMessage(), e);
        }
        return users;
    }

    // Вспомогательный метод: ResultSet -> User
    private User map_row(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("full_name"),
                rs.getString("phone"),
                rs.getString("email")
        );
    }
}