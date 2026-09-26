DROP TABLE IF EXISTS parcels CASCADE;
DROP TABLE IF EXISTS cells CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id         SERIAL PRIMARY KEY,
    full_name  VARCHAR(100) NOT NULL,
    phone      VARCHAR(20)  UNIQUE NOT NULL,
    email      VARCHAR(100) UNIQUE
);

CREATE TABLE cells (
    id              SERIAL PRIMARY KEY,
    locker_address  VARCHAR(200) NOT NULL,
    cell_number     INT          NOT NULL,
    size_type       VARCHAR(10)  NOT NULL
        CHECK (size_type IN ('SMALL', 'MEDIUM', 'LARGE')),
    status          VARCHAR(15)  NOT NULL
        CHECK (status IN ('FREE', 'OCCUPIED', 'NOT_WORKING')),

    UNIQUE (locker_address, cell_number)
);

CREATE TABLE parcels (
    id            SERIAL PRIMARY KEY,
    code VARCHAR(5)   UNIQUE NOT NULL,
    user_id       INT          NOT NULL REFERENCES users(id),
    cell_id       INT          REFERENCES cells(id),
    status        VARCHAR(15)  NOT NULL
        CHECK (status IN ('WAITING', 'PICKED_UP')),
    data    TIMESTAMP DEFAULT NOW()
);

-- тестовые данные
INSERT INTO users (full_name, phone, email) VALUES
    ('Иванов Иван', '+79001234567', 'ivanov@gmail.com'),
    ('Романов Роман', '+79000001234', 'romanov@gmail.com'),
    ('Данилов Виктор', '+79123456789', 'danilov@gmail.com'),
    ('Воронов Александр', '+79987654321', 'voronov@gmail.com'),
    ('Конев Максим', '+79001001010', 'konev@gmail.com');

INSERT INTO cells (locker_address, cell_number, size_type, status, user_id) VALUES
    ('ул. Первая, 12', 1, 'SMALL', 'FREE', NULL),
    ('ул. Первая, 12', 2, 'MEDIUM', 'FREE', 1),
    ('ул. Первая, 12', 3, 'MEDIUM', 'FREE', 2),
    ('ул. Первая, 12', 4, 'LARGE', 'FREE', NULL),
    ('ул. Пятая, 16', 1, 'SMALL', 'FREE', 3),
    ('ул. Пятая, 16', 2, 'MEDIUM', 'FREE', NULL),
    ('ул. Пятая, 16', 3, 'MEDIUM', 'NOT_WORKING', NULL),
    ('ул. Пятая, 16', 4, 'LARGE', 'FREE', 4),
    ('ул. Десятая, 10', 1, 'SMALL', 'FREE', 5),
    ('ул. Десятая, 10', 2, 'MEDIUM', 'FREE', NULL),
    ('ул. Десятая, 10', 3, 'MEDIUM', 'FREE', NULL),
    ('ул. Десятая, 10', 4, 'LARGE', 'NOT_WORKING', NULL);