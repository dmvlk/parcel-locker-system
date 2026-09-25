-- ============================================================
--  Демо-данные: 6 пользователей, 4 постамата, 15 заявок
--  Занятость ячеек согласована со статусами IN_POSTAMAT:
--    постамат 1: 1 посылка в ячейке -> free_cells = 40 - 1 = 39
--    постамат 2: 2 посылки          -> free_cells = 60 - 2 = 58
-- ============================================================

INSERT INTO users (id, full_name, phone, email) VALUES
(1, 'Иванов Иван Иванович',        '+7-913-111-22-33', 'ivanov@mail.ru'),
(2, 'Петрова Анна Сергеевна',      '+7-913-222-33-44', 'petrova@mail.ru'),
(3, 'Сидоров Пётр Николаевич',     '+7-913-333-44-55', 'sidorov@mail.ru'),
(4, 'Кузнецова Мария Олеговна',    '+7-913-444-55-66', 'kuznetsova@mail.ru'),
(5, 'Смирнов Алексей Дмитриевич',  '+7-913-555-66-77', 'smirnov@mail.ru'),
(6, 'Волков Дмитрий Андреевич',    '+7-913-666-77-88', 'volkov@mail.ru');

INSERT INTO postamats (id, address, capacity, free_cells, status) VALUES
(1, 'г. Новосибирск, ул. Ленина, 12, ТЦ «Роял Парк»', 40, 39, 'ACTIVE'),
(2, 'г. Новосибирск, пр-т Мира, 25',                  60, 58, 'ACTIVE'),
(3, 'г. Новосибирск, ул. Садовая, 7',                 20, 20, 'ACTIVE'),
(4, 'г. Новосибирск, ул. Школьная, 3',                30, 30, 'MAINTENANCE');

INSERT INTO delivery_requests
(id, tracking_number, user_id, postamat_id, item_description, weight_kg, size, status, pickup_code, created_at, updated_at, delivered_at) VALUES
(1,  'PM-000001', 1, 1, 'Ноутбук Lenovo IdeaPad 15',              2.40, 'M', 'DELIVERED',   '481923', NOW() - INTERVAL '30 days', NOW() - INTERVAL '25 days', NOW() - INTERVAL '25 days'),
(2,  'PM-000002', 2, 2, 'Кроссовки Nike Air',                     1.10, 'S', 'DELIVERED',   '735102', NOW() - INTERVAL '28 days', NOW() - INTERVAL '24 days', NOW() - INTERVAL '24 days'),
(3,  'PM-000003', 3, 1, 'Пылесос Bosch Serie 4',                  6.50, 'L', 'IN_POSTAMAT', '530271', NOW() - INTERVAL '3 days',  NOW() - INTERVAL '3 days',  NULL),
(4,  'PM-000004', 4, 2, 'Книга: Java. Полное руководство',        1.30, 'S', 'IN_POSTAMAT', '204817', NOW() - INTERVAL '2 days',  NOW() - INTERVAL '2 days',  NULL),
(5,  'PM-000005', 5, 1, 'Микроволновая печь Samsung',            14.00, 'L', 'CREATED',     NULL,     NOW() - INTERVAL '1 days',  NOW() - INTERVAL '1 days',  NULL),
(6,  'PM-000006', 6, 2, 'Наушники Sony WH-1000XM5',               0.90, 'S', 'CREATED',     NULL,     NOW() - INTERVAL '3 hours', NOW() - INTERVAL '3 hours', NULL),
(7,  'PM-000007', 1, 3, 'Футболка мужская, хлопок',               0.30, 'S', 'IN_TRANSIT',  NULL,     NOW() - INTERVAL '2 days',  NOW() - INTERVAL '2 days',  NULL),
(8,  'PM-000008', 2, 3, 'Термос стальной 1 л',                    0.80, 'S', 'IN_TRANSIT',  NULL,     NOW() - INTERVAL '1 days',  NOW() - INTERVAL '1 days',  NULL),
(9,  'PM-000009', 3, 2, 'Монитор Samsung 27 дюймов',              5.80, 'M', 'IN_POSTAMAT', '918264', NOW() - INTERVAL '4 days',  NOW() - INTERVAL '4 days',  NULL),
(10, 'PM-000010', 4, 1, 'Рюкзак школьный',                        1.00, 'S', 'DELIVERED',   '662305', NOW() - INTERVAL '20 days', NOW() - INTERVAL '16 days', NOW() - INTERVAL '16 days'),
(11, 'PM-000011', 5, 2, 'Кофемашина DeLonghi ECAM',               9.20, 'L', 'DELIVERED',   '394712', NOW() - INTERVAL '15 days', NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'),
(12, 'PM-000012', 6, 3, 'Планшет Samsung Galaxy Tab A9',          1.50, 'S', 'RETURNED',    '771548', NOW() - INTERVAL '12 days', NOW() - INTERVAL '5 days',  NULL),
(13, 'PM-000013', 1, 4, 'Зимняя куртка',                          2.20, 'M', 'CANCELED',    NULL,     NOW() - INTERVAL '10 days', NOW() - INTERVAL '9 days',  NULL),
(14, 'PM-000014', 2, 4, 'Конструктор LEGO Technic',               3.50, 'M', 'CANCELED',    NULL,     NOW() - INTERVAL '8 days',  NOW() - INTERVAL '7 days',  NULL),
(15, 'PM-000015', 3, 1, 'Смартфон Xiaomi 13',                     0.70, 'S', 'DELIVERED',   '559013', NOW() - INTERVAL '6 days',  NOW() - INTERVAL '2 days',  NOW() - INTERVAL '2 days');

-- Сдвигаем счётчики id, т.к. вставляли значения явно
SELECT setval('users_id_seq',             (SELECT MAX(id) FROM users));
SELECT setval('postamats_id_seq',         (SELECT MAX(id) FROM postamats));
SELECT setval('delivery_requests_id_seq', (SELECT MAX(id) FROM delivery_requests));