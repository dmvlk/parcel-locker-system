-- ============================================================
--  Сеть постаматов. Схема БД (PostgreSQL)
--  Запускать на пустой базе postamat_db
-- ============================================================

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    full_name     VARCHAR(150) NOT NULL,
    phone         VARCHAR(20)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    registered_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE postamats (
    id         BIGSERIAL PRIMARY KEY,
    address    VARCHAR(200) NOT NULL UNIQUE,
    capacity   INT          NOT NULL CHECK (capacity > 0),
    free_cells INT          NOT NULL CHECK (free_cells >= 0 AND free_cells <= capacity),
    status     VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE'
               CHECK (status IN ('ACTIVE', 'MAINTENANCE', 'OFFLINE'))
);

CREATE TABLE delivery_requests (
    id               BIGSERIAL PRIMARY KEY,
    tracking_number  VARCHAR(20)   NOT NULL UNIQUE,
    user_id          BIGINT        NOT NULL REFERENCES users(id),
    postamat_id      BIGINT        NOT NULL REFERENCES postamats(id),
    item_description VARCHAR(300)  NOT NULL,
    weight_kg        NUMERIC(5,2)  NOT NULL CHECK (weight_kg > 0 AND weight_kg <= 30),
    size             VARCHAR(1)    NOT NULL CHECK (size IN ('S', 'M', 'L')),
    status           VARCHAR(20)   NOT NULL DEFAULT 'CREATED'
                     CHECK (status IN ('CREATED', 'IN_TRANSIT', 'IN_POSTAMAT',
                                       'DELIVERED', 'RETURNED', 'CANCELED')),
    pickup_code      VARCHAR(6),          -- выдаётся при попадании посылки в постамат
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delivered_at     TIMESTAMP              -- NULL, пока посылка не выдана
);

-- Полезные индексы для поиска/фильтрации
CREATE INDEX idx_requests_user     ON delivery_requests(user_id);
CREATE INDEX idx_requests_postamat ON delivery_requests(postamat_id);
CREATE INDEX idx_requests_status   ON delivery_requests(status);
CREATE INDEX idx_requests_created  ON delivery_requests(created_at);