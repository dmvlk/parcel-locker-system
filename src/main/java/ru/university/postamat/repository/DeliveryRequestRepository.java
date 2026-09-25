package ru.university.postamat.repository;

import ru.university.postamat.exception.BusinessException;
import ru.university.postamat.model.DeliveryRequest;
import ru.university.postamat.model.DeliverySize;
import ru.university.postamat.model.DeliveryStatus;
import ru.university.postamat.util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DeliveryRequestRepository implements CrudRepository<DeliveryRequest> {

    // общий запрос с JOIN, чтобы сразу получать имя получателя и адрес постамата
    private static final String BASE_SELECT =
            "SELECT r.id, r.tracking_number, r.user_id, r.postamat_id, r.item_description, " +
                    "r.weight_kg, r.size, r.status, r.pickup_code, r.created_at, r.updated_at, r.delivered_at, " +
                    "u.full_name AS user_name, p.address AS postamat_address " +
                    "FROM delivery_requests r " +
                    "JOIN users u ON u.id = r.user_id " +
                    "JOIN postamats p ON p.id = r.postamat_id";

    // создание заявки в транзакции: сначала вставляем, получаем id, потом записываем трек-номер PM-XXXXXX
    public DeliveryRequest create(DeliveryRequest request) {
        String insertSql = "INSERT INTO delivery_requests (tracking_number, user_id, postamat_id, " +
                "item_description, weight_kg, size, status) VALUES ('', ?, ?, ?, ?, ?, ?) " +
                "RETURNING id, created_at, updated_at";
        String updateSql = "UPDATE delivery_requests SET tracking_number = ? WHERE id = ?";

        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false); // начало транзакции

            long newId;
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setLong(1, request.getUserId());
                ps.setLong(2, request.getPostamatId());
                ps.setString(3, request.getItemDescription());
                ps.setBigDecimal(4, request.getWeightKg());
                ps.setString(5, request.getSize().name());
                ps.setString(6, request.getStatus().name());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    newId = rs.getLong("id");
                    request.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    request.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                }
            }

            String trackingNumber = String.format("PM-%06d", newId);
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, trackingNumber);
                ps.setLong(2, newId);
                ps.executeUpdate();
            }

            conn.commit(); // фиксируем транзакцию

            request.setId(newId);
            request.setTrackingNumber(trackingNumber);
            return request;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) { }
            }
            throw new BusinessException("Ошибка базы данных при создании заявки: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) { }
            }
        }
    }

    public Optional<DeliveryRequest> findById(long id) {
        String sql = BASE_SELECT + " WHERE r.id = ?";
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
            throw new BusinessException("Ошибка базы данных при поиске заявки: " + e.getMessage());
        }
    }

    public Optional<DeliveryRequest> findByTrackingNumber(String trackingNumber) {
        String sql = BASE_SELECT + " WHERE r.tracking_number = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trackingNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при поиске заявки: " + e.getMessage());
        }
    }

    public List<DeliveryRequest> findAll() {
        String sql = BASE_SELECT + " ORDER BY r.id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            List<DeliveryRequest> requests = new ArrayList<>();
            while (rs.next()) {
                requests.add(mapRow(rs));
            }
            return requests;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при чтении заявок: " + e.getMessage());
        }
    }

    // поиск по трек-номеру (ILIKE - регистр не важен)
    public List<DeliveryRequest> searchByTracking(String query) {
        String sql = BASE_SELECT + " WHERE r.tracking_number ILIKE ? ORDER BY r.id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<DeliveryRequest> requests = new ArrayList<>();
                while (rs.next()) {
                    requests.add(mapRow(rs));
                }
                return requests;
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при поиске: " + e.getMessage());
        }
    }

    // поиск по описанию товара
    public List<DeliveryRequest> searchByItem(String query) {
        String sql = BASE_SELECT + " WHERE r.item_description ILIKE ? ORDER BY r.id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<DeliveryRequest> requests = new ArrayList<>();
                while (rs.next()) {
                    requests.add(mapRow(rs));
                }
                return requests;
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при поиске: " + e.getMessage());
        }
    }

    // поиск по ФИО получателя
    public List<DeliveryRequest> searchByUser(String query) {
        String sql = BASE_SELECT + " WHERE u.full_name ILIKE ? ORDER BY r.id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<DeliveryRequest> requests = new ArrayList<>();
                while (rs.next()) {
                    requests.add(mapRow(rs));
                }
                return requests;
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при поиске: " + e.getMessage());
        }
    }

    // поиск по адресу постамата
    public List<DeliveryRequest> searchByPostamat(String query) {
        String sql = BASE_SELECT + " WHERE p.address ILIKE ? ORDER BY r.id";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<DeliveryRequest> requests = new ArrayList<>();
                while (rs.next()) {
                    requests.add(mapRow(rs));
                }
                return requests;
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при поиске: " + e.getMessage());
        }
    }

    // редактирование содержимого заявки (статус меняется отдельным методом)
    public boolean update(DeliveryRequest request) {
        String sql = "UPDATE delivery_requests SET user_id = ?, postamat_id = ?, item_description = ?, " +
                "weight_kg = ?, size = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, request.getUserId());
            ps.setLong(2, request.getPostamatId());
            ps.setString(3, request.getItemDescription());
            ps.setBigDecimal(4, request.getWeightKg());
            ps.setString(5, request.getSize().name());
            ps.setLong(6, request.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при обновлении заявки: " + e.getMessage());
        }
    }

    // записываем код получения, когда посылка попала в постамат
    public boolean updatePickupCode(long id, String pickupCode) {
        String sql = "UPDATE delivery_requests SET pickup_code = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pickupCode);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при записи кода получения: " + e.getMessage());
        }
    }

    // смена статуса заявки и пересчет свободных ячеек постамата в ОДНОЙ транзакции (бизнес-правило 6)
    public void updateStatusWithCells(DeliveryRequest request, DeliveryStatus newStatus, String pickupCode) {
        Connection conn = null;
        try {
            conn = DatabaseManager.getConnection();
            conn.setAutoCommit(false);

            // блокируем строку постамата, чтобы никто не изменил ячейки параллельно
            int freeCells;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT free_cells FROM postamats WHERE id = ? FOR UPDATE")) {
                ps.setLong(1, request.getPostamatId());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    freeCells = rs.getInt("free_cells");
                }
            }

            // считаем, сколько ячеек занимает/освобождает переход
            boolean wasInPostamat = request.getStatus() == DeliveryStatus.IN_POSTAMAT;
            boolean willBeInPostamat = newStatus == DeliveryStatus.IN_POSTAMAT;
            int delta = 0;
            if (willBeInPostamat) delta -= 1;
            if (wasInPostamat) delta += 1;

            if (willBeInPostamat && freeCells == 0) {
                throw new BusinessException("В постамате нет свободных ячеек (бизнес-правило 2)");
            }

            // обновляем заявку: для каждого перехода свой запрос
            String updateRequestSql;
            if (newStatus == DeliveryStatus.IN_POSTAMAT) {
                updateRequestSql = "UPDATE delivery_requests SET status = ?, pickup_code = ?, " +
                        "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            } else if (newStatus == DeliveryStatus.DELIVERED) {
                updateRequestSql = "UPDATE delivery_requests SET status = ?, delivered_at = CURRENT_TIMESTAMP, " +
                        "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            } else {
                updateRequestSql = "UPDATE delivery_requests SET status = ?, " +
                        "updated_at = CURRENT_TIMESTAMP WHERE id = ?";
            }

            try (PreparedStatement ps = conn.prepareStatement(updateRequestSql)) {
                ps.setString(1, newStatus.name());
                if (newStatus == DeliveryStatus.IN_POSTAMAT) {
                    ps.setString(2, pickupCode);
                    ps.setLong(3, request.getId());
                } else {
                    ps.setLong(2, request.getId());
                }
                ps.executeUpdate();
            }

            // обновляем количество свободных ячеек
            if (delta != 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE postamats SET free_cells = ? WHERE id = ?")) {
                    ps.setInt(1, freeCells + delta);
                    ps.setLong(2, request.getPostamatId());
                    ps.executeUpdate();
                }
            }

            conn.commit();
            request.setStatus(newStatus);
            if (newStatus == DeliveryStatus.IN_POSTAMAT) {
                request.setPickupCode(pickupCode);
            }
        } catch (BusinessException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) { }
            }
            throw e;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) { }
            }
            throw new BusinessException("Ошибка базы данных при смене статуса: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) { }
            }
        }
    }

    // есть ли заявки у пользователя (чтобы нельзя было его удалить)
    public boolean existsByUserId(long userId) {
        String sql = "SELECT 1 FROM delivery_requests WHERE user_id = ? LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных: " + e.getMessage());
        }
    }

    // есть ли заявки у постамата
    public boolean existsByPostamatId(long postamatId) {
        String sql = "SELECT 1 FROM delivery_requests WHERE postamat_id = ? LIMIT 1";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, postamatId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных: " + e.getMessage());
        }
    }

    public boolean deleteById(long id) {
        String sql = "DELETE FROM delivery_requests WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BusinessException("Ошибка базы данных при удалении заявки: " + e.getMessage());
        }
    }

    private DeliveryRequest mapRow(ResultSet rs) throws SQLException {
        DeliveryRequest r = new DeliveryRequest();
        r.setId(rs.getLong("id"));
        r.setTrackingNumber(rs.getString("tracking_number"));
        r.setUserId(rs.getLong("user_id"));
        r.setPostamatId(rs.getLong("postamat_id"));
        r.setItemDescription(rs.getString("item_description"));
        r.setWeightKg(rs.getBigDecimal("weight_kg"));
        r.setSize(DeliverySize.valueOf(rs.getString("size")));
        r.setStatus(DeliveryStatus.valueOf(rs.getString("status")));
        r.setPickupCode(rs.getString("pickup_code"));
        r.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        r.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        Timestamp deliveredAt = rs.getTimestamp("delivered_at");
        if (deliveredAt != null) {
            r.setDeliveredAt(deliveredAt.toLocalDateTime());
        }
        r.setUserFullName(rs.getString("user_name"));
        r.setPostamatAddress(rs.getString("postamat_address"));
        return r;
    }
}