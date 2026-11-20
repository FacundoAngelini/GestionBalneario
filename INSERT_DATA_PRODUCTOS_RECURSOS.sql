-- 10 productos y 10 recursos de prueba. Ver README_INSERCION_DATOS.md.
-- Si ya existen datos que colisionan, falla y revierte toda esta carga.
-- Requiere MySQL 8 y el esquema creado por el backend.

DROP PROCEDURE IF EXISTS seed_gestion_recursos_demo;
DELIMITER $$
CREATE PROCEDURE seed_gestion_recursos_demo()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE sector_demo BIGINT;
    DECLARE recurso_nuevo BIGINT;
    DECLARE nombre_demo VARCHAR(25);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;
    INSERT INTO sectores (public_id, nombre_sector)
    SELECT UUID_TO_BIN(UUID()), 'Sector de prueba'
    WHERE NOT EXISTS (SELECT 1 FROM sectores WHERE nombre_sector = 'Sector de prueba');
    SELECT id INTO sector_demo FROM sectores WHERE nombre_sector = 'Sector de prueba';

    INSERT INTO productos (public_id, nombre, precio, categoria, producto_disponible) VALUES
    (UUID_TO_BIN(UUID()), 'DEMO - Agua mineral', 1800, 'BEBIDAS_FRIAS', TRUE),
    (UUID_TO_BIN(UUID()), 'DEMO - Gaseosa cola', 2500, 'BEBIDAS_FRIAS', TRUE),
    (UUID_TO_BIN(UUID()), 'DEMO - Limonada', 2200, 'BEBIDAS_FRIAS', TRUE),
    (UUID_TO_BIN(UUID()), 'DEMO - Cafe', 1600, 'BEBIDAS_CALIENTES', TRUE),
    (UUID_TO_BIN(UUID()), 'DEMO - Te', 1400, 'BEBIDAS_CALIENTES', TRUE),
    (UUID_TO_BIN(UUID()), 'DEMO - Sandwich tostado', 4200, 'COMIDA_CALIENTE', TRUE),
    (UUID_TO_BIN(UUID()), 'DEMO - Ensalada mixta', 3900, 'ENSALADAS', TRUE),
    (UUID_TO_BIN(UUID()), 'DEMO - Porcion de papas', 2800, 'GUARNICION', TRUE),
    (UUID_TO_BIN(UUID()), 'DEMO - Helado', 2100, 'POSTRES', TRUE),
    (UUID_TO_BIN(UUID()), 'DEMO - Jugo natural', 2600, 'MERIENDA', TRUE);

    WHILE i <= 10 DO
        SET nombre_demo = CASE i
            WHEN 1 THEN 'DEMO Carpa familiar 01' WHEN 2 THEN 'DEMO Carpa familiar 02'
            WHEN 3 THEN 'DEMO Sombrilla grande 01' WHEN 4 THEN 'DEMO Sombrilla mediana 02'
            WHEN 5 THEN 'DEMO Cancha futbol 01' WHEN 6 THEN 'DEMO Cancha voley 02'
            WHEN 7 THEN 'DEMO Mesa exterior 01' WHEN 8 THEN 'DEMO Mesa exterior 02'
            WHEN 9 THEN 'DEMO Pileta infantil 01' ELSE 'DEMO Pileta olimpica 02' END;
        IF EXISTS (SELECT 1 FROM recurso WHERE nombre_recurso = nombre_demo) THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Nombre de recurso de prueba ya existente.';
        END IF;
        INSERT INTO recurso (public_id, nombre_recurso, recurso_activo, sector_id)
        VALUES (UUID_TO_BIN(UUID()), nombre_demo, TRUE, sector_demo);
        SET recurso_nuevo = LAST_INSERT_ID();
        IF i <= 2 THEN
            INSERT INTO carpas (id, numero_carpa, numero_pasillo, capacidad_carpa)
            VALUES (recurso_nuevo, 900 + i, i, IF(i = 1, 6, 8));
        ELSEIF i <= 4 THEN
            INSERT INTO sombrillas (id, numero_carpa, tamanio_carpa)
            VALUES (recurso_nuevo, 908 + i, IF(i = 3, 'GRANDE', 'MEDIANO'));
        ELSEIF i <= 6 THEN
            INSERT INTO canchas (id, tipo_cancha, capacidad, iluminacion)
            VALUES (recurso_nuevo, IF(i = 5, 'FUTBOL', 'VOLEY'), IF(i = 5, 10, 12), TRUE);
        ELSEIF i <= 8 THEN
            INSERT INTO mesas (id, numero_mesa, capacidad_mesa)
            VALUES (recurso_nuevo, 914 + i, IF(i = 7, 4, 6));
        ELSE
            INSERT INTO piletas (id, es_climatizada, tamanio)
            VALUES (recurso_nuevo, i = 10, IF(i = 9, 15, 50));
        END IF;
        INSERT INTO precio_recursos (public_id, precio, modalidad, fecha_vigencia, fecha_caducada, recurso_id)
        VALUES (UUID_TO_BIN(UUID()), 5000 + i * 1000, 'DIA', CURDATE(),
                DATE_ADD(CURDATE(), INTERVAL 1 YEAR), recurso_nuevo);
        SET i = i + 1;
    END WHILE;
    COMMIT;
END$$
DELIMITER ;

CALL seed_gestion_recursos_demo();
DROP PROCEDURE seed_gestion_recursos_demo;

SELECT COUNT(*) AS productos_demo FROM productos WHERE nombre LIKE 'DEMO - %';
SELECT COUNT(*) AS recursos_demo FROM recurso WHERE nombre_recurso LIKE 'DEMO %';
