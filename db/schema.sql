DROP TABLE IF EXISTS parcels CASCADE;
DROP TABLE IF EXISTS cells CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255) NOT NULL DEFAULT '',
    role VARCHAR(10) NOT NULL DEFAULT 'USER' CHECK (role IN ('ADMIN', 'USER'))
);

CREATE TABLE cells (
    id              SERIAL PRIMARY KEY,
    locker_address  VARCHAR(200) NOT NULL,
    cell_number     INT          NOT NULL,
    size_type       VARCHAR(10)  NOT NULL CHECK (size_type IN ('SMALL', 'MEDIUM', 'LARGE')),
    status          VARCHAR(15)  NOT NULL CHECK (status IN ('FREE', 'OCCUPIED', 'NOT_WORKING')),

    UNIQUE (locker_address, cell_number)
);

CREATE TABLE parcels (
    id SERIAL PRIMARY KEY,
    code VARCHAR(5) UNIQUE NOT NULL,
    user_id INT NOT NULL REFERENCES users(id),
    cell_id INT REFERENCES cells(id),
    status VARCHAR(15) NOT NULL CHECK (status IN ('WAITING', 'PICKED_UP')),
    created_at TIMESTAMP DEFAULT NOW()
);

-- Изначальные дынные

INSERT INTO users (full_name, phone, email, password_hash, role) VALUES
    ('Администратор', '+79123456789', 'admin@postamat.ru','pbkdf2$120000$0X3L8FWGtCCDntL+STFdwQ==$1cJqRLyrbu9Kj+usSqaOk0SU4MhUDT4m2euhSoPCLOo=', 'ADMIN'),
    ('Пользователь', '+79987654321', 'user@postamat.ru','pbkdf2$120000$0X3L8FWGtCCDntL+STFdwQ==$1cJqRLyrbu9Kj+usSqaOk0SU4MhUDT4m2euhSoPCLOo=', 'USER');

INSERT INTO cells (locker_address, cell_number, size_type, status) VALUES
    ('ул. Первая, 12', 1, 'SMALL', 'FREE'),
    ('ул. Первая, 12', 2, 'MEDIUM', 'FREE'),
    ('ул. Первая, 12', 3, 'MEDIUM', 'FREE'),
    ('ул. Первая, 12', 4, 'LARGE', 'FREE'),
    ('ул. Пятая, 16', 1, 'SMALL', 'FREE'),
    ('ул. Пятая, 16', 2, 'MEDIUM', 'FREE'),
    ('ул. Пятая, 16', 3, 'MEDIUM', 'NOT_WORKING'),
    ('ул. Пятая, 16', 4, 'LARGE', 'FREE'),
    ('ул. Десятая, 10', 1, 'SMALL', 'FREE'),
    ('ул. Десятая, 10', 2, 'MEDIUM', 'FREE'),
    ('ул. Десятая, 10', 3, 'MEDIUM', 'FREE'),
    ('ул. Десятая, 10', 4, 'LARGE', 'NOT_WORKING');