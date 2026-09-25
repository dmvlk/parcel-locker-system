package ru.university.postamat.util;

import ru.university.postamat.exception.BusinessException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Очистка базы и заливка демонстрационных данных (пункт главного меню 6). */
public class DemoDataManager {

    private static final String[] SCRIPT = {
            "TRUNCATE delivery_requests, postamats, users RESTART IDENTITY CASCADE",

            "INSERT INTO users (full_name, phone, email) VALUES " +
                    "('Иванов Иван Иванович',       '+7-913-111-22-33', 'ivanov@mail.ru'), " +
                    "('Петрова Анна Сергеевна',     '+7-913-222-33-44', 'petrova@mail.ru'), " +
                    "('Сидоров Пётр Николаевич',    '+7-913-333-44-55', 'sidorov@mail.ru'), " +
                    "('Кузнецова Мария Олеговна',   '+7-913-444-55-66', 'kuznetsova@mail.ru'), " +
                    "('Смирнов Алексей Дмитриевич', '+7-913-555-66-77', 'smirnov@mail.ru'), " +
                    "('Волков Дмитрий Андреевич',   '+7-913-666-77-88', 'volkov@mail.ru')",

            "INSERT INTO postamats (address, capacity, free_cells, status) VALUES " +
                    "('г. Новосибирск, ул. Ленина, 12, ТЦ «Роял Парк»', 40, 39, 'ACTIVE'), " +
                    "('г. Новосибирск, пр-т Мира, 25',                  60, 58, 'ACTIVE'), " +
                    "('г. Новосибирск, ул. Садовая, 7',                 20, 20, 'ACTIVE'), " +
                    "('г. Новосибирск, ул. Школьная, 3',                30, 30, 'MAINTENANCE')",

            "INSERT INTO delivery_requests (tracking_number, user_id, postamat_id, item_description, " +
                    "weight_kg, size, status, pickup_code, created_at, updated_at, delivered_at) VALUES " +
                    "('PM-000001', 1, 1, 'Ноутбук Lenovo IdeaPad 15',       2.40, 'M', 'DELIVERED',   '481923', NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days'), " +
                    "('PM-000002', 2, 2, 'Кроссовки Nike Air',              1.10, 'S', 'DELIVERED',   '735102', NOW() - INTERVAL '28 days', NOW() - INTERVAL '24 days', NOW() - INTERVAL '24 days'), " +
                    "('PM-000003', 3, 1, 'Пылесос Bosch Serie 4',           6.50, 'L', 'IN_POSTAMAT', '530271', NOW() - INTERVAL '3 days',  NOW() - INTERVAL '3 days',  NULL), " +
                    "('PM-000004', 4, 2, 'Книга: Java. Полное руководство', 1.30, 'S', 'IN_POSTAMAT', '204817', NOW() - INTERVAL '2 days',  NOW() - INTERVAL '2 days',  NULL), " +
                    "('PM-000005', 5, 1, 'Микроволновая печь Samsung',     14.00, 'L', 'CREATED',     NULL,     NOW() - INTERVAL '1 days',  NOW() - INTERVAL '1 days',  NULL), " +
                    "('PM-000006', 6, 2, 'Наушники Sony WH-1000XM5',        0.90, 'S', 'CREATED',     NULL,     NOW() - INTERVAL '3 hours', NOW() - INTERVAL '3 hours', NULL), " +
                    "('PM-000007', 1, 3, 'Футболка мужская, хлопок',        0.30, 'S', 'IN_TRANSIT',  NULL,     NOW() - INTERVAL '2 days',  NOW() - INTERVAL '2 days',  NULL), " +
                    "('PM-000008', 2, 3, 'Термос стальной 1 л',             0.80, 'S', 'IN_TRANSIT',  NULL,     NOW() - INTERVAL '1 days',  NOW() - INTERVAL '1 days',  NULL), " +
                    "('PM-000009', 3, 2, 'Монитор Samsung 27 дюймов',       5.80, 'M', 'IN_POSTAMAT', '918264', NOW() - INTERVAL '4 days',  NOW() - INTERVAL '4 days',  NULL), " +
                    "('PM-000010', 4, 1, 'Рюкзак школьный',                 1.00, 'S', 'DELIVERED',   '662305', NOW() - INTERVAL '20 days', NOW() - INTERVAL '16 days', NOW() - INTERVAL '16 days'), " +
                    "('PM-000011', 5, 2, 'Кофемашина DeLonghi ECAM',        9.20, 'L', 'DELIVERED',   '394712', NOW() - INTERVAL '15 days', NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'), " +
                    "('PM-000012', 6, 3, 'Планшет Samsung Galaxy Tab A9',   1.50, 'S', 'RETURNED',    '771548', NOW() - INTERVAL '12 days', NOW() - INTERVAL '5 days',  NULL), " +
                    "('PM-000013', 1, 4, 'Зимняя куртка',                   2.20, 'M', 'CANCELED',    NULL,     NOW() - INTERVAL '10 days', NOW() - INTERVAL '9 days',  NULL), " +
                    "('PM-000014', 2, 4, 'Конструктор LEGO Technic',        3.50, 'M', 'CANCELED',    NULL,     NOW() - INTERVAL '8 days',  NOW() - INTERVAL '7 days',  NULL), " +
                    "('PM-000015', 3, 1, 'Смартфон Xiaomi 13',              0.70, 'S', 'DELIVERED',   '559013', NOW() - INTERVAL '6 days',  NOW() - INTERVAL '2 days',  NOW() - INTERVAL '2 days')"
    };

    public static void restore() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement st = conn.createStatement()) {
            for (String sql : SCRIPT) {
                st.executeUpdate(sql);
            }
        } catch (SQLException e) {
            throw new BusinessException("Не удалось восстановить демо-данные: " + e.getMessage());
        }
    }
}