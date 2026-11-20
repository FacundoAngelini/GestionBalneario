-- 50 clientes de prueba. Ver README_INSERCION_DATOS.md antes de ejecutar.
-- Requiere MySQL 8 y el esquema creado por el backend.
-- Usuarios: demo_cliente01 a demo_cliente50; clave de prueba: Demo1234!
-- Si ya existen datos que colisionan, falla y revierte toda esta carga.
-- Usar "Run SQL Script" en Workbench o el cliente mysql, sin --force.

DROP PROCEDURE IF EXISTS seed_gestion_clientes_demo;
DELIMITER $$
CREATE PROCEDURE seed_gestion_clientes_demo()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE usuario_nuevo BIGINT;
    DECLARE credencial_nueva BIGINT;
    DECLARE rol_cliente BIGINT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;
    SELECT id INTO rol_cliente FROM roles WHERE role = 'ROLE_CLIENTE' LIMIT 1;
    IF rol_cliente IS NULL THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Falta ROLE_CLIENTE: iniciar primero el backend.';
    END IF;
    IF (SELECT IS_NULLABLE FROM information_schema.columns
        WHERE table_schema = DATABASE() AND table_name = 'credenciales'
          AND column_name = 'refresh_token') <> 'YES' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'credenciales.refresh_token debe permitir NULL; revisar esquema.';
    END IF;

    WHILE i <= 50 DO
        INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
        SET usuario_nuevo = LAST_INSERT_ID();
        INSERT INTO credenciales (nombre_usuario, contrasenia, usuario_id, enabled, refresh_token)
        VALUES (CONCAT('demo_cliente', LPAD(i, 2, '0')),
                '$2a$10$LFsmYlaOek5idz5nYlZxeO1zhQURzr3JZBY2l27a1W74nxTUUyyOe',
                usuario_nuevo, TRUE, NULL);
        SET credencial_nueva = LAST_INSERT_ID();
        INSERT INTO credencial_roles (credencial_id, roles_id) VALUES (credencial_nueva, rol_cliente);
        INSERT INTO clientes (public_id, dni, nombre, apellido, email, telefono, fecha_alta, estado, usuario_id)
        VALUES (UUID_TO_BIN(UUID()), 74000000 + i, CONCAT('Cliente ', i), 'Prueba',
                CONCAT('demo.cliente', LPAD(i, 2, '0'), '@example.com'),
                CONCAT('54223000', LPAD(i, 4, '0')), CURDATE(), TRUE, usuario_nuevo);
        SET i = i + 1;
    END WHILE;
    COMMIT;
END$$
DELIMITER ;

CALL seed_gestion_clientes_demo();
DROP PROCEDURE seed_gestion_clientes_demo;

SELECT COUNT(*) AS clientes_demo
FROM clientes WHERE dni BETWEEN 74000001 AND 74000050;
