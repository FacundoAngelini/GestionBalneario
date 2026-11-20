-- 20 empleados y 2 gerentes de prueba. Ver README_INSERCION_DATOS.md.
-- Usuarios: demo_empleado01 a demo_empleado20 y demo_gerente01 a demo_gerente02.
-- Clave de prueba: Demo1234!; requiere roles cargados por el backend.
-- Si ya existen datos que colisionan, falla y revierte toda esta carga.

DROP PROCEDURE IF EXISTS seed_gestion_empleados_demo;
DELIMITER $$
CREATE PROCEDURE seed_gestion_empleados_demo()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE candidato INT DEFAULT 75000000;
    DECLARE suma INT;
    DECLARE digito INT;
    DECLARE cuit_base VARCHAR(10);
    DECLARE cuit_nuevo VARCHAR(11);
    DECLARE usuario_nuevo BIGINT;
    DECLARE credencial_nueva BIGINT;
    DECLARE sector_demo BIGINT;
    DECLARE rol_negocio BIGINT;
    DECLARE rol_seguridad BIGINT;
    DECLARE funcion VARCHAR(30);
    DECLARE seguridad VARCHAR(30);
    DECLARE login_demo VARCHAR(30);
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;
    IF (SELECT IS_NULLABLE FROM information_schema.columns
        WHERE table_schema = DATABASE() AND table_name = 'credenciales'
          AND column_name = 'refresh_token') <> 'YES' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'credenciales.refresh_token debe permitir NULL; revisar esquema.';
    END IF;

    INSERT INTO sectores (public_id, nombre_sector)
    SELECT UUID_TO_BIN(UUID()), 'Sector de prueba'
    WHERE NOT EXISTS (SELECT 1 FROM sectores WHERE nombre_sector = 'Sector de prueba');
    SELECT id INTO sector_demo FROM sectores WHERE nombre_sector = 'Sector de prueba';

    WHILE i <= 22 DO
        SET funcion = CASE
            WHEN i > 20 THEN 'GERENTE'
            WHEN MOD(i, 5) = 1 THEN 'MOZO'
            WHEN MOD(i, 5) = 2 THEN 'CAJERO'
            WHEN MOD(i, 5) = 3 THEN 'REPARTIDOR'
            WHEN MOD(i, 5) = 4 THEN 'CARPERO'
            ELSE 'ADMINISTRATIVO' END;
        SET seguridad = CASE funcion
            WHEN 'GERENTE' THEN 'ROLE_GERENTE'
            WHEN 'ADMINISTRATIVO' THEN 'ROLE_ADMINISTRACION'
            WHEN 'CARPERO' THEN 'ROLE_CARPERO'
            WHEN 'CAJERO' THEN 'ROLE_CAJERO'
            WHEN 'MOZO' THEN 'ROLE_MOZO'
            ELSE 'ROLE_REPARTIDOR' END;
        SET rol_negocio = NULL;
        SET rol_seguridad = NULL;
        SELECT id INTO rol_negocio FROM rol WHERE tipo_rol = funcion ORDER BY id LIMIT 1;
        SELECT id INTO rol_seguridad FROM roles WHERE role = seguridad LIMIT 1;
        IF rol_negocio IS NULL OR rol_seguridad IS NULL THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Faltan roles: iniciar primero el backend.';
        END IF;

        -- CUIT 20 + DNI + dígito verificador según CuitValidator.
        SET digito = 10;
        WHILE digito = 10 DO
            SET candidato = candidato + 1;
            SET cuit_base = CONCAT('20', candidato);
            SET suma = SUBSTRING(cuit_base,1,1)*5 + SUBSTRING(cuit_base,2,1)*4
                     + SUBSTRING(cuit_base,3,1)*3 + SUBSTRING(cuit_base,4,1)*2
                     + SUBSTRING(cuit_base,5,1)*7 + SUBSTRING(cuit_base,6,1)*6
                     + SUBSTRING(cuit_base,7,1)*5 + SUBSTRING(cuit_base,8,1)*4
                     + SUBSTRING(cuit_base,9,1)*3 + SUBSTRING(cuit_base,10,1)*2;
            SET digito = MOD(11 - MOD(suma, 11), 11);
        END WHILE;
        SET cuit_nuevo = CONCAT(cuit_base, digito);
        IF EXISTS (SELECT 1 FROM empleados WHERE cuit = cuit_nuevo) THEN
            SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'CUIT de prueba ya existente; no se insertaron empleados.';
        END IF;
        SET login_demo = IF(i <= 20, CONCAT('demo_empleado', LPAD(i, 2, '0')),
                            CONCAT('demo_gerente', LPAD(i - 20, 2, '0')));
        INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
        SET usuario_nuevo = LAST_INSERT_ID();
        INSERT INTO credenciales (nombre_usuario, contrasenia, usuario_id, enabled, refresh_token)
        VALUES (login_demo, '$2a$10$LFsmYlaOek5idz5nYlZxeO1zhQURzr3JZBY2l27a1W74nxTUUyyOe',
                usuario_nuevo, TRUE, NULL);
        SET credencial_nueva = LAST_INSERT_ID();
        INSERT INTO credencial_roles (credencial_id, roles_id) VALUES (credencial_nueva, rol_seguridad);
        INSERT INTO empleados (public_id, dni, nombre, apellido, email, telefono, cuit, sueldo,
                               estado_empleado, calle, numero, ciudad, provincia, sector_id, rol_id, usuario_id)
        VALUES (UUID_TO_BIN(UUID()), candidato, CONCAT('Empleado ', i), 'Prueba',
                CONCAT(login_demo, '@example.com'), CONCAT('54223100', LPAD(i, 4, '0')),
                cuit_nuevo, IF(i > 20, 850000, 500000), 'ACTIVO', 'Calle Prueba',
                100 + i, 'Mar del Plata', 'Buenos Aires', sector_demo, rol_negocio, usuario_nuevo);
        SET i = i + 1;
    END WHILE;
    COMMIT;
END$$
DELIMITER ;

CALL seed_gestion_empleados_demo();
DROP PROCEDURE seed_gestion_empleados_demo;

SELECT r.tipo_rol, COUNT(*) AS total
FROM empleados e JOIN rol r ON r.id = e.rol_id
JOIN credenciales c ON c.usuario_id = e.usuario_id
WHERE c.nombre_usuario REGEXP '^demo_(empleado[0-9]{2}|gerente[0-9]{2})$'
GROUP BY r.tipo_rol ORDER BY r.tipo_rol;
