-- Usuarios (evitamos 'user' por ser reservado)
CREATE TABLE app_user (
  id            BIGSERIAL PRIMARY KEY,
  name          VARCHAR(100) NOT NULL,
  email         VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role          VARCHAR(20)  NOT NULL DEFAULT 'USER',
  created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Habitaciones
CREATE TABLE room (
  id        BIGSERIAL PRIMARY KEY,
  code      VARCHAR(20) NOT NULL UNIQUE,
  type      VARCHAR(50) NOT NULL,           -- single, double, suite...
  capacity  INT         NOT NULL,
  amenities JSONB       NOT NULL DEFAULT '{}'::jsonb,
  status    VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
);

-- Planes de tarifa
CREATE TABLE rate_plan (
  id                  BIGSERIAL PRIMARY KEY,
  name                VARCHAR(80) NOT NULL,
  currency            VARCHAR(3)  NOT NULL DEFAULT 'USD',
  tax_percent         NUMERIC(5,2) NOT NULL DEFAULT 0.00,
  cancellation_policy TEXT
);

-- Inventario por fecha
CREATE TABLE room_inventory (
  id         BIGSERIAL PRIMARY KEY,
  room_id    BIGINT     NOT NULL REFERENCES room(id),
  date       DATE       NOT NULL,
  base_price NUMERIC(10,2) NOT NULL,
  is_blocked BOOLEAN    NOT NULL DEFAULT FALSE,
  CONSTRAINT uq_room_date UNIQUE(room_id, date)
);

-- Reservas
CREATE TABLE booking (
  id          BIGSERIAL PRIMARY KEY,
  user_id     BIGINT     NOT NULL REFERENCES app_user(id),
  check_in    DATE       NOT NULL,
  check_out   DATE       NOT NULL,
  guests      INT        NOT NULL,
  total_price NUMERIC(12,2) NOT NULL DEFAULT 0.00,
  status      VARCHAR(20)   NOT NULL DEFAULT 'CREATED',
  created_at  TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- Noches reservadas
CREATE TABLE booking_item (
  id               BIGSERIAL PRIMARY KEY,
  booking_id       BIGINT NOT NULL REFERENCES booking(id) ON DELETE CASCADE,
  room_id          BIGINT NOT NULL REFERENCES room(id),
  date             DATE   NOT NULL,
  price_per_night  NUMERIC(10,2) NOT NULL,
  CONSTRAINT uq_booking_room_date UNIQUE(booking_id, room_id, date)
);

-- Índices útiles
CREATE INDEX idx_room_inventory_room_date ON room_inventory(room_id, date);
CREATE INDEX idx_booking_user ON booking(user_id);
CREATE INDEX idx_booking_item_booking ON booking_item(booking_id);
