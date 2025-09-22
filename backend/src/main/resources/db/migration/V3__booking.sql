-- 1) Marcar noches reservadas a nivel de inventario
ALTER TABLE room_inventory
    ADD COLUMN IF NOT EXISTS is_booked BOOLEAN NOT NULL DEFAULT FALSE;

-- Índice útil para rangos por habitación
CREATE INDEX IF NOT EXISTS idx_room_inventory_room_date
    ON room_inventory (room_id, date);

-- 2) Tabla de reservas
CREATE TABLE IF NOT EXISTS reservation (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(16) NOT NULL UNIQUE,
    room_id BIGINT NOT NULL REFERENCES room(id),
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    guests INT NOT NULL,
    total NUMERIC(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    customer_name VARCHAR(100) NOT NULL,
    customer_email VARCHAR(255),
    customer_phone VARCHAR(30),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Por si hay reports
CREATE INDEX IF NOT EXISTS idx_reservation_room_dates
    ON reservation (room_id, check_in, check_out);
