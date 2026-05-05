-- Script SQL para cargar datos realistas en la BBDD de esientradas
-- Motor objetivo: MySQL 8+
-- Inserta directamente en tablas: escenario, espectaculo, entrada, de_zona, precisa
-- Nota de dominio: el campo "artista" en espectaculo se usa como etiqueta/titulo del evento

START TRANSACTION;

-- =========================
-- 1) ESCENARIOS
-- =========================
INSERT INTO escenario (nombre, descripcion, tipo)
SELECT 'Movistar Arena Madrid', 'Recinto indoor para grandes conciertos y giras internacionales', 'CONCIERTO'
WHERE NOT EXISTS (
    SELECT 1 FROM escenario WHERE LOWER(nombre) = LOWER('Movistar Arena Madrid')
);

INSERT INTO escenario (nombre, descripcion, tipo)
SELECT 'Teatro Real', 'Teatro de referencia para opera, musicales y conciertos sinfonicos', 'TEATRO'
WHERE NOT EXISTS (
    SELECT 1 FROM escenario WHERE LOWER(nombre) = LOWER('Teatro Real')
);

INSERT INTO escenario (nombre, descripcion, tipo)
SELECT 'Riyadh Air Metropolitano', 'Estadio para grandes eventos deportivos y espectaculos masivos', 'ESTADIO'
WHERE NOT EXISTS (
    SELECT 1 FROM escenario WHERE LOWER(nombre) = LOWER('Riyadh Air Metropolitano')
);

-- =========================
-- 2) ESPECTACULOS
-- =========================
INSERT INTO espectaculo (artista, fecha, fecha_apertura_taquilla, escenario_id)
SELECT 'Dua Lipa - Radical Optimism Tour', '2026-06-12 21:30:00', '2026-03-20 10:00:00', s.id
FROM escenario s
WHERE s.nombre = 'Movistar Arena Madrid'
  AND NOT EXISTS (
      SELECT 1 FROM espectaculo e
      WHERE e.artista = 'Dua Lipa - Radical Optimism Tour'
        AND e.fecha = '2026-06-12 21:30:00'
        AND e.escenario_id = s.id
  );

INSERT INTO espectaculo (artista, fecha, fecha_apertura_taquilla, escenario_id)
SELECT 'Aitana - Alpha Encore', '2026-09-18 21:00:00', '2026-06-04 10:00:00', s.id
FROM escenario s
WHERE s.nombre = 'Movistar Arena Madrid'
  AND NOT EXISTS (
      SELECT 1 FROM espectaculo e
      WHERE e.artista = 'Aitana - Alpha Encore'
        AND e.fecha = '2026-09-18 21:00:00'
        AND e.escenario_id = s.id
  );

INSERT INTO espectaculo (artista, fecha, fecha_apertura_taquilla, escenario_id)
SELECT 'El Fantasma de la Opera', '2026-10-03 19:30:00', '2026-05-01 10:00:00', s.id
FROM escenario s
WHERE s.nombre = 'Teatro Real'
  AND NOT EXISTS (
      SELECT 1 FROM espectaculo e
      WHERE e.artista = 'El Fantasma de la Opera'
        AND e.fecha = '2026-10-03 19:30:00'
        AND e.escenario_id = s.id
  );

INSERT INTO espectaculo (artista, fecha, fecha_apertura_taquilla, escenario_id)
SELECT 'Hans Zimmer Symphonic Tribute', '2026-07-25 20:00:00', '2026-04-02 11:00:00', s.id
FROM escenario s
WHERE s.nombre = 'Teatro Real'
  AND NOT EXISTS (
      SELECT 1 FROM espectaculo e
      WHERE e.artista = 'Hans Zimmer Symphonic Tribute'
        AND e.fecha = '2026-07-25 20:00:00'
        AND e.escenario_id = s.id
  );

INSERT INTO espectaculo (artista, fecha, fecha_apertura_taquilla, escenario_id)
SELECT 'Atletico de Madrid vs FC Barcelona', '2026-05-30 21:00:00', '2026-02-05 10:00:00', s.id
FROM escenario s
WHERE s.nombre = 'Riyadh Air Metropolitano'
  AND NOT EXISTS (
      SELECT 1 FROM espectaculo e
      WHERE e.artista = 'Atletico de Madrid vs FC Barcelona'
        AND e.fecha = '2026-05-30 21:00:00'
        AND e.escenario_id = s.id
  );

-- =========================
-- 3) ENTRADAS DE ZONA
-- =========================
-- Evento 1: Dua Lipa (CONCIERTO)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 240
)
INSERT INTO entrada (precio, espectaculo_id, estado, email_comprador, dtype)
SELECT 11900, e.id, 'DISPONIBLE', NULL, 'DeZona'
FROM seq
CROSS JOIN espectaculo e
JOIN escenario s ON s.id = e.escenario_id
WHERE e.artista = 'Dua Lipa - Radical Optimism Tour'
  AND s.nombre = 'Movistar Arena Madrid'
  AND NOT EXISTS (SELECT 1 FROM entrada x WHERE x.espectaculo_id = e.id);

WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 180
)
INSERT INTO entrada (precio, espectaculo_id, estado, email_comprador, dtype)
SELECT 9900, e.id, 'DISPONIBLE', NULL, 'DeZona'
FROM seq
CROSS JOIN espectaculo e
JOIN escenario s ON s.id = e.escenario_id
WHERE e.artista = 'Dua Lipa - Radical Optimism Tour'
  AND s.nombre = 'Movistar Arena Madrid'
  AND EXISTS (SELECT 1 FROM entrada x WHERE x.espectaculo_id = e.id AND x.dtype = 'DeZona')
  AND (SELECT COUNT(*) FROM entrada x WHERE x.espectaculo_id = e.id) = 240;

WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 160
)
INSERT INTO entrada (precio, espectaculo_id, estado, email_comprador, dtype)
SELECT 6900, e.id, 'DISPONIBLE', NULL, 'DeZona'
FROM seq
CROSS JOIN espectaculo e
JOIN escenario s ON s.id = e.escenario_id
WHERE e.artista = 'Dua Lipa - Radical Optimism Tour'
  AND s.nombre = 'Movistar Arena Madrid'
  AND (SELECT COUNT(*) FROM entrada x WHERE x.espectaculo_id = e.id) = 420;

INSERT INTO de_zona (id, zona)
SELECT en.id, 'Pista'
FROM entrada en
JOIN espectaculo e ON e.id = en.espectaculo_id
JOIN escenario s ON s.id = e.escenario_id
LEFT JOIN de_zona dz ON dz.id = en.id
WHERE e.artista = 'Dua Lipa - Radical Optimism Tour'
  AND s.nombre = 'Movistar Arena Madrid'
  AND en.dtype = 'DeZona'
  AND en.precio = 11900
  AND dz.id IS NULL;

INSERT INTO de_zona (id, zona)
SELECT en.id, 'Grada Baja'
FROM entrada en
JOIN espectaculo e ON e.id = en.espectaculo_id
JOIN escenario s ON s.id = e.escenario_id
LEFT JOIN de_zona dz ON dz.id = en.id
WHERE e.artista = 'Dua Lipa - Radical Optimism Tour'
  AND s.nombre = 'Movistar Arena Madrid'
  AND en.dtype = 'DeZona'
  AND en.precio = 9900
  AND dz.id IS NULL;

INSERT INTO de_zona (id, zona)
SELECT en.id, 'Grada Alta'
FROM entrada en
JOIN espectaculo e ON e.id = en.espectaculo_id
JOIN escenario s ON s.id = e.escenario_id
LEFT JOIN de_zona dz ON dz.id = en.id
WHERE e.artista = 'Dua Lipa - Radical Optimism Tour'
  AND s.nombre = 'Movistar Arena Madrid'
  AND en.dtype = 'DeZona'
  AND en.precio = 6900
  AND dz.id IS NULL;

-- Evento 2: Atletico vs Barcelona (ESTADIO)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 280
)
INSERT INTO entrada (precio, espectaculo_id, estado, email_comprador, dtype)
SELECT 5600, e.id, 'DISPONIBLE', NULL, 'DeZona'
FROM seq
CROSS JOIN espectaculo e
JOIN escenario s ON s.id = e.escenario_id
WHERE e.artista = 'Atletico de Madrid vs FC Barcelona'
  AND s.nombre = 'Riyadh Air Metropolitano'
  AND NOT EXISTS (SELECT 1 FROM entrada x WHERE x.espectaculo_id = e.id);

WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 220
)
INSERT INTO entrada (precio, espectaculo_id, estado, email_comprador, dtype)
SELECT 9200, e.id, 'DISPONIBLE', NULL, 'DeZona'
FROM seq
CROSS JOIN espectaculo e
JOIN escenario s ON s.id = e.escenario_id
WHERE e.artista = 'Atletico de Madrid vs FC Barcelona'
  AND s.nombre = 'Riyadh Air Metropolitano'
  AND (SELECT COUNT(*) FROM entrada x WHERE x.espectaculo_id = e.id) = 280;

WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 160
)
INSERT INTO entrada (precio, espectaculo_id, estado, email_comprador, dtype)
SELECT 12500, e.id, 'DISPONIBLE', NULL, 'DeZona'
FROM seq
CROSS JOIN espectaculo e
JOIN escenario s ON s.id = e.escenario_id
WHERE e.artista = 'Atletico de Madrid vs FC Barcelona'
  AND s.nombre = 'Riyadh Air Metropolitano'
  AND (SELECT COUNT(*) FROM entrada x WHERE x.espectaculo_id = e.id) = 500;

INSERT INTO de_zona (id, zona)
SELECT en.id, 'Fondo'
FROM entrada en
JOIN espectaculo e ON e.id = en.espectaculo_id
JOIN escenario s ON s.id = e.escenario_id
LEFT JOIN de_zona dz ON dz.id = en.id
WHERE e.artista = 'Atletico de Madrid vs FC Barcelona'
  AND s.nombre = 'Riyadh Air Metropolitano'
  AND en.dtype = 'DeZona'
  AND en.precio = 5600
  AND dz.id IS NULL;

INSERT INTO de_zona (id, zona)
SELECT en.id, 'Lateral'
FROM entrada en
JOIN espectaculo e ON e.id = en.espectaculo_id
JOIN escenario s ON s.id = e.escenario_id
LEFT JOIN de_zona dz ON dz.id = en.id
WHERE e.artista = 'Atletico de Madrid vs FC Barcelona'
  AND s.nombre = 'Riyadh Air Metropolitano'
  AND en.dtype = 'DeZona'
  AND en.precio = 9200
  AND dz.id IS NULL;

INSERT INTO de_zona (id, zona)
SELECT en.id, 'Tribuna'
FROM entrada en
JOIN espectaculo e ON e.id = en.espectaculo_id
JOIN escenario s ON s.id = e.escenario_id
LEFT JOIN de_zona dz ON dz.id = en.id
WHERE e.artista = 'Atletico de Madrid vs FC Barcelona'
  AND s.nombre = 'Riyadh Air Metropolitano'
  AND en.dtype = 'DeZona'
  AND en.precio = 12500
  AND dz.id IS NULL;

-- =========================
-- 4) ENTRADAS PRECISAS (BUTACAS)
-- =========================
-- Evento 3: El Fantasma de la Opera (TEATRO)
-- Planta 1: 8 filas x 14 columnas = 112 butacas (precio 13500)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 112
)
INSERT INTO entrada (precio, espectaculo_id, estado, email_comprador, dtype)
SELECT 13500, e.id, 'DISPONIBLE', NULL, 'Precisa'
FROM seq
CROSS JOIN espectaculo e
JOIN escenario s ON s.id = e.escenario_id
WHERE e.artista = 'El Fantasma de la Opera'
  AND s.nombre = 'Teatro Real'
  AND NOT EXISTS (SELECT 1 FROM entrada x WHERE x.espectaculo_id = e.id);

-- Planta 2: 8 filas x 14 columnas = 112 butacas (precio 9200)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 112
)
INSERT INTO entrada (precio, espectaculo_id, estado, email_comprador, dtype)
SELECT 9200, e.id, 'DISPONIBLE', NULL, 'Precisa'
FROM seq
CROSS JOIN espectaculo e
JOIN escenario s ON s.id = e.escenario_id
WHERE e.artista = 'El Fantasma de la Opera'
  AND s.nombre = 'Teatro Real'
  AND (SELECT COUNT(*) FROM entrada x WHERE x.espectaculo_id = e.id) = 112;

-- Asignar coordenadas (planta, fila, columna) en la tabla precisa
INSERT INTO precisa (id, planta, fila, columna)
SELECT q.id, q.planta, q.fila, q.columna
FROM (
    SELECT
        e2.id,
        CASE WHEN rn <= 112 THEN 1 ELSE 2 END AS planta,
        ((CASE WHEN rn <= 112 THEN rn ELSE rn - 112 END - 1) DIV 14) + 1 AS fila,
        ((CASE WHEN rn <= 112 THEN rn ELSE rn - 112 END - 1) MOD 14) + 1 AS columna
    FROM (
        SELECT en.id,
               ROW_NUMBER() OVER (ORDER BY en.id) AS rn
        FROM entrada en
        JOIN espectaculo e ON e.id = en.espectaculo_id
        JOIN escenario s ON s.id = e.escenario_id
        LEFT JOIN precisa p ON p.id = en.id
        WHERE e.artista = 'El Fantasma de la Opera'
          AND s.nombre = 'Teatro Real'
          AND en.dtype = 'Precisa'
          AND p.id IS NULL
    ) e2
) q;

-- Evento 4: Hans Zimmer Symphonic Tribute (TEATRO)
-- Planta 1: 7x12 = 84 (15000), Planta 2: 7x12 = 84 (11500), Planta 3: 7x12 = 84 (7900)
WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 84
)
INSERT INTO entrada (precio, espectaculo_id, estado, email_comprador, dtype)
SELECT 15000, e.id, 'DISPONIBLE', NULL, 'Precisa'
FROM seq
CROSS JOIN espectaculo e
JOIN escenario s ON s.id = e.escenario_id
WHERE e.artista = 'Hans Zimmer Symphonic Tribute'
  AND s.nombre = 'Teatro Real'
  AND NOT EXISTS (SELECT 1 FROM entrada x WHERE x.espectaculo_id = e.id);

WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 84
)
INSERT INTO entrada (precio, espectaculo_id, estado, email_comprador, dtype)
SELECT 11500, e.id, 'DISPONIBLE', NULL, 'Precisa'
FROM seq
CROSS JOIN espectaculo e
JOIN escenario s ON s.id = e.escenario_id
WHERE e.artista = 'Hans Zimmer Symphonic Tribute'
  AND s.nombre = 'Teatro Real'
  AND (SELECT COUNT(*) FROM entrada x WHERE x.espectaculo_id = e.id) = 84;

WITH RECURSIVE seq AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM seq WHERE n < 84
)
INSERT INTO entrada (precio, espectaculo_id, estado, email_comprador, dtype)
SELECT 7900, e.id, 'DISPONIBLE', NULL, 'Precisa'
FROM seq
CROSS JOIN espectaculo e
JOIN escenario s ON s.id = e.escenario_id
WHERE e.artista = 'Hans Zimmer Symphonic Tribute'
  AND s.nombre = 'Teatro Real'
  AND (SELECT COUNT(*) FROM entrada x WHERE x.espectaculo_id = e.id) = 168;

INSERT INTO precisa (id, planta, fila, columna)
SELECT q.id, q.planta, q.fila, q.columna
FROM (
    SELECT
        e2.id,
        CASE
            WHEN rn <= 84 THEN 1
            WHEN rn <= 168 THEN 2
            ELSE 3
        END AS planta,
        ((CASE
            WHEN rn <= 84 THEN rn
            WHEN rn <= 168 THEN rn - 84
            ELSE rn - 168
          END - 1) DIV 12) + 1 AS fila,
        ((CASE
            WHEN rn <= 84 THEN rn
            WHEN rn <= 168 THEN rn - 84
            ELSE rn - 168
          END - 1) MOD 12) + 1 AS columna
    FROM (
        SELECT en.id,
               ROW_NUMBER() OVER (ORDER BY en.id) AS rn
        FROM entrada en
        JOIN espectaculo e ON e.id = en.espectaculo_id
        JOIN escenario s ON s.id = e.escenario_id
        LEFT JOIN precisa p ON p.id = en.id
        WHERE e.artista = 'Hans Zimmer Symphonic Tribute'
          AND s.nombre = 'Teatro Real'
          AND en.dtype = 'Precisa'
          AND p.id IS NULL
    ) e2
) q;

COMMIT;

-- Resumen rapido
SELECT COUNT(*) AS escenarios_totales FROM escenario;
SELECT COUNT(*) AS espectaculos_totales FROM espectaculo;
SELECT COUNT(*) AS entradas_totales FROM entrada;
SELECT COUNT(*) AS entradas_zona FROM de_zona;
SELECT COUNT(*) AS entradas_butaca FROM precisa;
