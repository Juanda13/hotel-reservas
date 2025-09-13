-- =====================================================================
-- V2__seed.sql  |  Datos de ejemplo para desarrollo
-- - Usuarios demo
-- - Habitaciones y plan de tarifas
-- - Inventario por 30 días, con recargo fin de semana y algunos bloqueos
-- =====================================================================

-- Usuarios demo (hash ficticio por ahora; auth vendrá luego)
INSERT INTO app_user (name, email, password_hash, role, created_at)
SELECT 'Admin', 'admin@example.com', '$2a$10$dummyhashdummyhashdummyhashdummyhashdummyhashdummyh', 'ADMIN', NOW()
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE email = 'admin@example.com');

INSERT INTO app_user (name, email, password_hash, role, created_at)
SELECT 'Juan', 'juan@example.com', '$2a$10$dummyhashdummyhashdummyhashdummyhashdummyhashdummyh', 'USER', NOW()
WHERE NOT EXISTS (SELECT 1 FROM app_user WHERE email = 'juan@example.com');

-- Planes de tarifa (uno básico)
INSERT INTO rate_plan (name, currency, tax_percent, cancellation_policy)
SELECT 'Standard', 'USD', 12.00, 'Cancelación gratuita hasta 24h antes'
WHERE NOT EXISTS (SELECT 1 FROM rate_plan WHERE name = 'Standard');

-- Habitaciones (códigos únicos)
INSERT INTO room (code, type, capacity, amenities, status)
SELECT 'R101', 'single', 1, '{"wifi":true,"ac":true,"tv":true}'::jsonb, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM room WHERE code = 'R101');

INSERT INTO room (code, type, capacity, amenities, status)
SELECT 'R102', 'double', 2, '{"wifi":true,"ac":true,"tv":true}'::jsonb, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM room WHERE code = 'R102');

INSERT INTO room (code, type, capacity, amenities, status)
SELECT 'R201', 'double', 2, '{"wifi":true,"ac":true,"tv":true,"minibar":true}'::jsonb, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM room WHERE code = 'R201');

INSERT INTO room (code, type, capacity, amenities, status)
SELECT 'R202', 'triple', 3, '{"wifi":true,"ac":true,"tv":true,"balcony":true}'::jsonb, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM room WHERE code = 'R202');

INSERT INTO room (code, type, capacity, amenities, status)
SELECT 'S301', 'suite', 3, '{"wifi":true,"ac":true,"tv":true,"minibar":true,"balcony":true}'::jsonb, 'ACTIVE'
WHERE NOT EXISTS (SELECT 1 FROM room WHERE code = 'S301');

-- Inventario por 30 días (hoy -> hoy+29)
-- Precios base según tipo + 15% fines de semana (viernes/sábado)
WITH days AS (
  SELECT (CURRENT_DATE + offs) AS d
  FROM generate_series(0, 29) AS offs
)
INSERT INTO room_inventory (room_id, date, base_price, is_blocked)
SELECT r.id,
       d.d::date,
       ROUND((
         CASE r.type
           WHEN 'single' THEN 80
           WHEN 'double' THEN 120
           WHEN 'triple' THEN 150
           WHEN 'suite'  THEN 220
           ELSE 100
         END
         *
         CASE WHEN EXTRACT(DOW FROM d.d) IN (5, 6) THEN 1.15 ELSE 1.00 END
       )::numeric, 2) AS base_price,
       FALSE
FROM room r
CROSS JOIN days d
-- evita duplicar si ya existiera algún inventario
WHERE NOT EXISTS (
  SELECT 1 FROM room_inventory ri
  WHERE ri.room_id = r.id AND ri.date = d.d::date
);

-- Bloqueos deterministas (para probar "no disponible")
-- Bloquea cada 7mo día a la R101 y dos fechas concretas a R201
UPDATE room_inventory ri
SET is_blocked = TRUE
WHERE ri.room_id = (SELECT id FROM room WHERE code = 'R101')
  AND (EXTRACT(DAY FROM ri.date)::int % 7) = 0;

UPDATE room_inventory ri
SET is_blocked = TRUE
WHERE ri.room_id = (SELECT id FROM room WHERE code = 'R201')
  AND ri.date IN (CURRENT_DATE + 3, CURRENT_DATE + 10);
