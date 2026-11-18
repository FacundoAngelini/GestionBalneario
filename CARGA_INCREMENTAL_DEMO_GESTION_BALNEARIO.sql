-- ============================================================
-- CARGA INCREMENTAL DE DATOS DEMO - GestionBalneario
-- Base esperada: MiBalneario (MySQL 8)
-- Agrega 47 clientes, 19 empleados (incluye 1 gerente),
-- 8 productos y 7 reservas. No elimina ni modifica registros existentes.
-- Reutiliza el hash BCrypt de la cuenta existente 'facu'.
-- Según la configuración de demo documentada, la clave es Demo1234!
-- Ejecutar una sola vez. Hacer backup antes de importar.
-- ============================================================

SET NAMES utf8mb4;
SET @hash_demo := (SELECT contrasenia FROM credenciales WHERE nombre_usuario = 'facu' LIMIT 1);

START TRANSACTION;

-- Cliente demo 4
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_4 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_04', NULL, @uid_cli_4);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_04';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Gómez', 30000068, 'cliente.demo04@example.com', b'1', CURDATE(), 'Lucía', UUID_TO_BIN(UUID()), '2235100004', @uid_cli_4);

-- Cliente demo 5
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_5 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_05', NULL, @uid_cli_5);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_05';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Fernández', 30000085, 'cliente.demo05@example.com', b'1', CURDATE(), 'Mateo', UUID_TO_BIN(UUID()), '2235100005', @uid_cli_5);

-- Cliente demo 6
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_6 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_06', NULL, @uid_cli_6);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_06';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('López', 30000102, 'cliente.demo06@example.com', b'1', CURDATE(), 'Valentina', UUID_TO_BIN(UUID()), '2235100006', @uid_cli_6);

-- Cliente demo 7
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_7 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_07', NULL, @uid_cli_7);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_07';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Martínez', 30000119, 'cliente.demo07@example.com', b'1', CURDATE(), 'Benjamín', UUID_TO_BIN(UUID()), '2235100007', @uid_cli_7);

-- Cliente demo 8
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_8 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_08', NULL, @uid_cli_8);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_08';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Pérez', 30000136, 'cliente.demo08@example.com', b'1', CURDATE(), 'Martina', UUID_TO_BIN(UUID()), '2235100008', @uid_cli_8);

-- Cliente demo 9
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_9 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_09', NULL, @uid_cli_9);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_09';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('García', 30000153, 'cliente.demo09@example.com', b'1', CURDATE(), 'Joaquín', UUID_TO_BIN(UUID()), '2235100009', @uid_cli_9);

-- Cliente demo 10
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_10 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_10', NULL, @uid_cli_10);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_10';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Rodríguez', 30000170, 'cliente.demo10@example.com', b'1', CURDATE(), 'Catalina', UUID_TO_BIN(UUID()), '2235100010', @uid_cli_10);

-- Cliente demo 11
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_11 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_11', NULL, @uid_cli_11);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_11';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Sánchez', 30000187, 'cliente.demo11@example.com', b'1', CURDATE(), 'Thiago', UUID_TO_BIN(UUID()), '2235100011', @uid_cli_11);

-- Cliente demo 12
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_12 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_12', NULL, @uid_cli_12);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_12';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Romero', 30000204, 'cliente.demo12@example.com', b'1', CURDATE(), 'Camila', UUID_TO_BIN(UUID()), '2235100012', @uid_cli_12);

-- Cliente demo 13
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_13 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_13', NULL, @uid_cli_13);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_13';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Sosa', 30000221, 'cliente.demo13@example.com', b'1', CURDATE(), 'Santiago', UUID_TO_BIN(UUID()), '2235100013', @uid_cli_13);

-- Cliente demo 14
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_14 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_14', NULL, @uid_cli_14);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_14';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Díaz', 30000238, 'cliente.demo14@example.com', b'1', CURDATE(), 'Agustina', UUID_TO_BIN(UUID()), '2235100014', @uid_cli_14);

-- Cliente demo 15
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_15 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_15', NULL, @uid_cli_15);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_15';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Álvarez', 30000255, 'cliente.demo15@example.com', b'1', CURDATE(), 'Tomás', UUID_TO_BIN(UUID()), '2235100015', @uid_cli_15);

-- Cliente demo 16
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_16 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_16', NULL, @uid_cli_16);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_16';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Torres', 30000272, 'cliente.demo16@example.com', b'1', CURDATE(), 'Julieta', UUID_TO_BIN(UUID()), '2235100016', @uid_cli_16);

-- Cliente demo 17
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_17 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_17', NULL, @uid_cli_17);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_17';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Ruiz', 30000289, 'cliente.demo17@example.com', b'1', CURDATE(), 'Bautista', UUID_TO_BIN(UUID()), '2235100017', @uid_cli_17);

-- Cliente demo 18
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_18 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_18', NULL, @uid_cli_18);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_18';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Acosta', 30000306, 'cliente.demo18@example.com', b'1', CURDATE(), 'Mía', UUID_TO_BIN(UUID()), '2235100018', @uid_cli_18);

-- Cliente demo 19
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_19 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_19', NULL, @uid_cli_19);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_19';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Benítez', 30000323, 'cliente.demo19@example.com', b'1', CURDATE(), 'Nicolás', UUID_TO_BIN(UUID()), '2235100019', @uid_cli_19);

-- Cliente demo 20
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_20 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_20', NULL, @uid_cli_20);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_20';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Medina', 30000340, 'cliente.demo20@example.com', b'1', CURDATE(), 'Emilia', UUID_TO_BIN(UUID()), '2235100020', @uid_cli_20);

-- Cliente demo 21
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_21 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_21', NULL, @uid_cli_21);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_21';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Herrera', 30000357, 'cliente.demo21@example.com', b'1', CURDATE(), 'Franco', UUID_TO_BIN(UUID()), '2235100021', @uid_cli_21);

-- Cliente demo 22
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_22 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_22', NULL, @uid_cli_22);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_22';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Suárez', 30000374, 'cliente.demo22@example.com', b'1', CURDATE(), 'Renata', UUID_TO_BIN(UUID()), '2235100022', @uid_cli_22);

-- Cliente demo 23
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_23 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_23', NULL, @uid_cli_23);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_23';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Aguirre', 30000391, 'cliente.demo23@example.com', b'1', CURDATE(), 'Lautaro', UUID_TO_BIN(UUID()), '2235100023', @uid_cli_23);

-- Cliente demo 24
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_24 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_24', NULL, @uid_cli_24);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_24';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Molina', 30000408, 'cliente.demo24@example.com', b'1', CURDATE(), 'Abril', UUID_TO_BIN(UUID()), '2235100024', @uid_cli_24);

-- Cliente demo 25
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_25 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_25', NULL, @uid_cli_25);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_25';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Castro', 30000425, 'cliente.demo25@example.com', b'1', CURDATE(), 'Dante', UUID_TO_BIN(UUID()), '2235100025', @uid_cli_25);

-- Cliente demo 26
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_26 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_26', NULL, @uid_cli_26);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_26';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Ortiz', 30000442, 'cliente.demo26@example.com', b'1', CURDATE(), 'Josefina', UUID_TO_BIN(UUID()), '2235100026', @uid_cli_26);

-- Cliente demo 27
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_27 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_27', NULL, @uid_cli_27);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_27';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Silva', 30000459, 'cliente.demo27@example.com', b'1', CURDATE(), 'Máximo', UUID_TO_BIN(UUID()), '2235100027', @uid_cli_27);

-- Cliente demo 28
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_28 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_28', NULL, @uid_cli_28);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_28';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Rojas', 30000476, 'cliente.demo28@example.com', b'1', CURDATE(), 'Pilar', UUID_TO_BIN(UUID()), '2235100028', @uid_cli_28);

-- Cliente demo 29
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_29 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_29', NULL, @uid_cli_29);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_29';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Cabrera', 30000493, 'cliente.demo29@example.com', b'1', CURDATE(), 'Bruno', UUID_TO_BIN(UUID()), '2235100029', @uid_cli_29);

-- Cliente demo 30
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_30 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_30', NULL, @uid_cli_30);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_30';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Navarro', 30000510, 'cliente.demo30@example.com', b'1', CURDATE(), 'Malena', UUID_TO_BIN(UUID()), '2235100030', @uid_cli_30);

-- Cliente demo 31
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_31 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_31', NULL, @uid_cli_31);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_31';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Vega', 30000527, 'cliente.demo31@example.com', b'1', CURDATE(), 'Felipe', UUID_TO_BIN(UUID()), '2235100031', @uid_cli_31);

-- Cliente demo 32
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_32 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_32', NULL, @uid_cli_32);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_32';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Méndez', 30000544, 'cliente.demo32@example.com', b'1', CURDATE(), 'Bianca', UUID_TO_BIN(UUID()), '2235100032', @uid_cli_32);

-- Cliente demo 33
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_33 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_33', NULL, @uid_cli_33);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_33';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Ponce', 30000561, 'cliente.demo33@example.com', b'1', CURDATE(), 'Simón', UUID_TO_BIN(UUID()), '2235100033', @uid_cli_33);

-- Cliente demo 34
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_34 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_34', NULL, @uid_cli_34);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_34';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Ibarra', 30000578, 'cliente.demo34@example.com', b'1', CURDATE(), 'Victoria', UUID_TO_BIN(UUID()), '2235100034', @uid_cli_34);

-- Cliente demo 35
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_35 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_35', NULL, @uid_cli_35);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_35';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Correa', 30000595, 'cliente.demo35@example.com', b'1', CURDATE(), 'Tobías', UUID_TO_BIN(UUID()), '2235100035', @uid_cli_35);

-- Cliente demo 36
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_36 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_36', NULL, @uid_cli_36);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_36';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Figueroa', 30000612, 'cliente.demo36@example.com', b'1', CURDATE(), 'Lola', UUID_TO_BIN(UUID()), '2235100036', @uid_cli_36);

-- Cliente demo 37
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_37 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_37', NULL, @uid_cli_37);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_37';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Carrizo', 30000629, 'cliente.demo37@example.com', b'1', CURDATE(), 'Benicio', UUID_TO_BIN(UUID()), '2235100037', @uid_cli_37);

-- Cliente demo 38
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_38 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_38', NULL, @uid_cli_38);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_38';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Peralta', 30000646, 'cliente.demo38@example.com', b'1', CURDATE(), 'Alma', UUID_TO_BIN(UUID()), '2235100038', @uid_cli_38);

-- Cliente demo 39
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_39 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_39', NULL, @uid_cli_39);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_39';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Maldonado', 30000663, 'cliente.demo39@example.com', b'1', CURDATE(), 'Gael', UUID_TO_BIN(UUID()), '2235100039', @uid_cli_39);

-- Cliente demo 40
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_40 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_40', NULL, @uid_cli_40);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_40';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Bravo', 30000680, 'cliente.demo40@example.com', b'1', CURDATE(), 'Olivia', UUID_TO_BIN(UUID()), '2235100040', @uid_cli_40);

-- Cliente demo 41
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_41 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_41', NULL, @uid_cli_41);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_41';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Ríos', 30000697, 'cliente.demo41@example.com', b'1', CURDATE(), 'Facundo', UUID_TO_BIN(UUID()), '2235100041', @uid_cli_41);

-- Cliente demo 42
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_42 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_42', NULL, @uid_cli_42);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_42';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Arias', 30000714, 'cliente.demo42@example.com', b'1', CURDATE(), 'Milagros', UUID_TO_BIN(UUID()), '2235100042', @uid_cli_42);

-- Cliente demo 43
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_43 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_43', NULL, @uid_cli_43);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_43';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Godoy', 30000731, 'cliente.demo43@example.com', b'1', CURDATE(), 'Pedro', UUID_TO_BIN(UUID()), '2235100043', @uid_cli_43);

-- Cliente demo 44
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_44 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_44', NULL, @uid_cli_44);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_44';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Cáceres', 30000748, 'cliente.demo44@example.com', b'1', CURDATE(), 'Antonella', UUID_TO_BIN(UUID()), '2235100044', @uid_cli_44);

-- Cliente demo 45
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_45 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_45', NULL, @uid_cli_45);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_45';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Domínguez', 30000765, 'cliente.demo45@example.com', b'1', CURDATE(), 'Manuel', UUID_TO_BIN(UUID()), '2235100045', @uid_cli_45);

-- Cliente demo 46
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_46 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_46', NULL, @uid_cli_46);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_46';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Vázquez', 30000782, 'cliente.demo46@example.com', b'1', CURDATE(), 'Clara', UUID_TO_BIN(UUID()), '2235100046', @uid_cli_46);

-- Cliente demo 47
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_47 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_47', NULL, @uid_cli_47);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_47';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Miranda', 30000799, 'cliente.demo47@example.com', b'1', CURDATE(), 'León', UUID_TO_BIN(UUID()), '2235100047', @uid_cli_47);

-- Cliente demo 48
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_48 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_48', NULL, @uid_cli_48);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_48';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Cruz', 30000816, 'cliente.demo48@example.com', b'1', CURDATE(), 'Aitana', UUID_TO_BIN(UUID()), '2235100048', @uid_cli_48);

-- Cliente demo 49
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_49 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_49', NULL, @uid_cli_49);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_49';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Luna', 30000833, 'cliente.demo49@example.com', b'1', CURDATE(), 'Ramiro', UUID_TO_BIN(UUID()), '2235100049', @uid_cli_49);

-- Cliente demo 50
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_cli_50 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_cliente_50', NULL, @uid_cli_50);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 4 FROM credenciales WHERE nombre_usuario = 'demo_cliente_50';
INSERT INTO clientes (apellido, dni, email, estado, fecha_alta, nombre, public_id, telefono, usuario_id)
VALUES ('Ferreyra', 30000850, 'cliente.demo50@example.com', b'1', CURDATE(), 'Emma', UUID_TO_BIN(UUID()), '2235100050', @uid_cli_50);

-- Empleado demo 4: GERENTE / Administracion
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_4 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_04', NULL, @uid_emp_4);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_04';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 2 FROM credenciales WHERE nombre_usuario = 'demo_empleado_04';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Pereyra', '20330000768', 'Av. Independencia', 'Mar del Plata', 128, 'Buenos Aires', 33000076, 'empleado.demo04@example.com', 'ACTIVO', 'Martín', UUID_TO_BIN(UUID()), 650000, '2234200004', 1, 1, @uid_emp_4);

-- Empleado demo 5: MOZO / Salon
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_5 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_05', NULL, @uid_emp_5);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_05';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 3 FROM credenciales WHERE nombre_usuario = 'demo_empleado_05';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Benítez', '20330000954', 'Av. Constitución', 'Mar del Plata', 135, 'Buenos Aires', 33000095, 'empleado.demo05@example.com', 'ACTIVO', 'Sofía', UUID_TO_BIN(UUID()), 675000, '2234200005', 2, 2, @uid_emp_5);

-- Empleado demo 6: MOZO / Salon
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_6 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_06', NULL, @uid_emp_6);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_06';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 3 FROM credenciales WHERE nombre_usuario = 'demo_empleado_06';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Roldán', '20330001144', 'Calle 12 de Octubre', 'Mar del Plata', 142, 'Buenos Aires', 33000114, 'empleado.demo06@example.com', 'ACTIVO', 'Diego', UUID_TO_BIN(UUID()), 700000, '2234200006', 2, 2, @uid_emp_6);

-- Empleado demo 7: CAJERO / Administracion
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_7 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_07', NULL, @uid_emp_7);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_07';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 5 FROM credenciales WHERE nombre_usuario = 'demo_empleado_07';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Méndez', '20330001330', 'Av. Colón', 'Mar del Plata', 149, 'Buenos Aires', 33000133, 'empleado.demo07@example.com', 'ACTIVO', 'Carolina', UUID_TO_BIN(UUID()), 725000, '2234200007', 3, 1, @uid_emp_7);

-- Empleado demo 8: CARPERO / Playa
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_8 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_08', NULL, @uid_emp_8);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_08';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 9 FROM credenciales WHERE nombre_usuario = 'demo_empleado_08';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Quiroga', '20330001527', 'Calle San Martín', 'Mar del Plata', 156, 'Buenos Aires', 33000152, 'empleado.demo08@example.com', 'ACTIVO', 'Pablo', UUID_TO_BIN(UUID()), 750000, '2234200008', 4, 4, @uid_emp_8);

-- Empleado demo 9: CARPERO / Playa
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_9 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_09', NULL, @uid_emp_9);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_09';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 9 FROM credenciales WHERE nombre_usuario = 'demo_empleado_09';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Vera', '20330001713', 'Av. Independencia', 'Mar del Plata', 163, 'Buenos Aires', 33000171, 'empleado.demo09@example.com', 'ACTIVO', 'Florencia', UUID_TO_BIN(UUID()), 775000, '2234200009', 4, 4, @uid_emp_9);

-- Empleado demo 10: COCINERO / Cocina
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_10 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_10', NULL, @uid_emp_10);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_10';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Acosta', '23330001909', 'Av. Constitución', 'Mar del Plata', 170, 'Buenos Aires', 33000190, 'empleado.demo10@example.com', 'ACTIVO', 'Gonzalo', UUID_TO_BIN(UUID()), 800000, '2234200010', 5, 3, @uid_emp_10);

-- Empleado demo 11: COCINERO / Cocina
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_11 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_11', NULL, @uid_emp_11);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_11';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Ledesma', '20330002094', 'Calle 12 de Octubre', 'Mar del Plata', 177, 'Buenos Aires', 33000209, 'empleado.demo11@example.com', 'ACTIVO', 'Mariana', UUID_TO_BIN(UUID()), 825000, '2234200011', 5, 3, @uid_emp_11);

-- Empleado demo 12: REPARTIDOR / Cocina
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_12 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_12', NULL, @uid_emp_12);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_12';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 7 FROM credenciales WHERE nombre_usuario = 'demo_empleado_12';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Navarro', '20330002280', 'Av. Colón', 'Mar del Plata', 184, 'Buenos Aires', 33000228, 'empleado.demo12@example.com', 'ACTIVO', 'Ezequiel', UUID_TO_BIN(UUID()), 850000, '2234200012', 6, 3, @uid_emp_12);

-- Empleado demo 13: ADMINISTRATIVO / Administracion
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_13 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_13', NULL, @uid_emp_13);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_13';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 8 FROM credenciales WHERE nombre_usuario = 'demo_empleado_13';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Campos', '20330002477', 'Calle San Martín', 'Mar del Plata', 191, 'Buenos Aires', 33000247, 'empleado.demo13@example.com', 'ACTIVO', 'Rocío', UUID_TO_BIN(UUID()), 875000, '2234200013', 7, 1, @uid_emp_13);

-- Empleado demo 14: EMPLEADO / Salon
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_14 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_14', NULL, @uid_emp_14);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_14';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Soria', '20330002663', 'Av. Independencia', 'Mar del Plata', 198, 'Buenos Aires', 33000266, 'empleado.demo14@example.com', 'ACTIVO', 'Iván', UUID_TO_BIN(UUID()), 900000, '2234200014', 8, 2, @uid_emp_14);

-- Empleado demo 15: EMPLEADO / Cocina
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_15 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_15', NULL, @uid_emp_15);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_15';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Arias', '23330002859', 'Av. Constitución', 'Mar del Plata', 205, 'Buenos Aires', 33000285, 'empleado.demo15@example.com', 'ACTIVO', 'Paula', UUID_TO_BIN(UUID()), 925000, '2234200015', 8, 3, @uid_emp_15);

-- Empleado demo 16: CARPERO / Playa
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_16 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_16', NULL, @uid_emp_16);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_16';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 9 FROM credenciales WHERE nombre_usuario = 'demo_empleado_16';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Funes', '23330003049', 'Calle 12 de Octubre', 'Mar del Plata', 212, 'Buenos Aires', 33000304, 'empleado.demo16@example.com', 'ACTIVO', 'Agustín', UUID_TO_BIN(UUID()), 950000, '2234200016', 4, 4, @uid_emp_16);

-- Empleado demo 17: MOZO / Salon
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_17 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_17', NULL, @uid_emp_17);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_17';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 3 FROM credenciales WHERE nombre_usuario = 'demo_empleado_17';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Coronel', '20330003236', 'Av. Colón', 'Mar del Plata', 219, 'Buenos Aires', 33000323, 'empleado.demo17@example.com', 'ACTIVO', 'Micaela', UUID_TO_BIN(UUID()), 975000, '2234200017', 2, 2, @uid_emp_17);

-- Empleado demo 18: CAJERO / Administracion
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_18 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_18', NULL, @uid_emp_18);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_18';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 5 FROM credenciales WHERE nombre_usuario = 'demo_empleado_18';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Paz', '20330003422', 'Calle San Martín', 'Mar del Plata', 226, 'Buenos Aires', 33000342, 'empleado.demo18@example.com', 'ACTIVO', 'Leandro', UUID_TO_BIN(UUID()), 1000000, '2234200018', 3, 1, @uid_emp_18);

-- Empleado demo 19: COCINERO / Cocina
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_19 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_19', NULL, @uid_emp_19);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_19';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Córdoba', '20330003619', 'Av. Independencia', 'Mar del Plata', 233, 'Buenos Aires', 33000361, 'empleado.demo19@example.com', 'ACTIVO', 'Noelia', UUID_TO_BIN(UUID()), 1025000, '2234200019', 5, 3, @uid_emp_19);

-- Empleado demo 20: REPARTIDOR / Cocina
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_20 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_20', NULL, @uid_emp_20);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_20';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 7 FROM credenciales WHERE nombre_usuario = 'demo_empleado_20';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Mansilla', '20330003805', 'Av. Constitución', 'Mar del Plata', 240, 'Buenos Aires', 33000380, 'empleado.demo20@example.com', 'ACTIVO', 'Federico', UUID_TO_BIN(UUID()), 1050000, '2234200020', 6, 3, @uid_emp_20);

-- Empleado demo 21: ADMINISTRATIVO / Administracion
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_21 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_21', NULL, @uid_emp_21);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_21';
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 8 FROM credenciales WHERE nombre_usuario = 'demo_empleado_21';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Rivas', '20330003996', 'Calle 12 de Octubre', 'Mar del Plata', 247, 'Buenos Aires', 33000399, 'empleado.demo21@example.com', 'ACTIVO', 'Belén', UUID_TO_BIN(UUID()), 1075000, '2234200021', 7, 1, @uid_emp_21);

-- Empleado demo 22: EMPLEADO / Salon
INSERT INTO usuarios (public_id) VALUES (UUID_TO_BIN(UUID()));
SET @uid_emp_22 := LAST_INSERT_ID();
INSERT INTO credenciales (contrasenia, enabled, nombre_usuario, refresh_token, usuario_id)
VALUES (@hash_demo, 1, 'demo_empleado_22', NULL, @uid_emp_22);
INSERT INTO credencial_roles (credencial_id, roles_id)
SELECT id, 6 FROM credenciales WHERE nombre_usuario = 'demo_empleado_22';
INSERT INTO empleados
(apellido, cuit, calle, ciudad, numero, provincia, dni, email, estado_empleado, nombre, public_id, sueldo, telefono, rol_id, sector_id, usuario_id)
VALUES
('Ojeda', '20330004186', 'Av. Colón', 'Mar del Plata', 254, 'Buenos Aires', 33000418, 'empleado.demo22@example.com', 'ACTIVO', 'Nicolás', UUID_TO_BIN(UUID()), 1100000, '2234200022', 8, 2, @uid_emp_22);

INSERT INTO productos (categoria, nombre, precio, producto_disponible, public_id)
VALUES ('DESAYUNO', 'Medialunas', 1800, b'1', UUID_TO_BIN(UUID()));
INSERT INTO productos (categoria, nombre, precio, producto_disponible, public_id)
VALUES ('DESAYUNO', 'Tostado de jamón y queso', 6500, b'1', UUID_TO_BIN(UUID()));
INSERT INTO productos (categoria, nombre, precio, producto_disponible, public_id)
VALUES ('BEBIDAS_FRIAS', 'Jugo de naranja', 3200, b'1', UUID_TO_BIN(UUID()));
INSERT INTO productos (categoria, nombre, precio, producto_disponible, public_id)
VALUES ('BEBIDAS_FRIAS', 'Agua mineral', 2200, b'1', UUID_TO_BIN(UUID()));
INSERT INTO productos (categoria, nombre, precio, producto_disponible, public_id)
VALUES ('BEBIDAS_FRIAS', 'Gaseosa línea cola', 2800, b'1', UUID_TO_BIN(UUID()));
INSERT INTO productos (categoria, nombre, precio, producto_disponible, public_id)
VALUES ('BEBIDAS_CALIENTES', 'Café espresso', 2500, b'1', UUID_TO_BIN(UUID()));
INSERT INTO productos (categoria, nombre, precio, producto_disponible, public_id)
VALUES ('ENSALADAS', 'Ensalada completa', 8500, b'1', UUID_TO_BIN(UUID()));
INSERT INTO productos (categoria, nombre, precio, producto_disponible, public_id)
VALUES ('ALMUERZO', 'Hamburguesa completa', 10500, b'1', UUID_TO_BIN(UUID()));

INSERT INTO reservas
(disponible_desde, disponible_hasta, estado_reserva, fecha_creacion, fecha_fin, fecha_inicio, modalidad, monto_total, public_id, reservado, cliente_id)
VALUES
(NULL, NULL, 'CONFIRMADA', NOW(6), '2027-12-12', '2027-12-10', 'DIA', 306030, UUID_TO_BIN(UUID()), b'1', (SELECT id FROM clientes WHERE email='cliente.demo04@example.com'));
SET @reserva_demo := LAST_INSERT_ID();
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES (@reserva_demo, 2);

INSERT INTO reservas
(disponible_desde, disponible_hasta, estado_reserva, fecha_creacion, fecha_fin, fecha_inicio, modalidad, monto_total, public_id, reservado, cliente_id)
VALUES
(NULL, NULL, 'CONFIRMADA', NOW(6), '2027-12-25', '2027-12-20', 'DIA', 612060, UUID_TO_BIN(UUID()), b'1', (SELECT id FROM clientes WHERE email='cliente.demo05@example.com'));
SET @reserva_demo := LAST_INSERT_ID();
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES (@reserva_demo, 5);

INSERT INTO reservas
(disponible_desde, disponible_hasta, estado_reserva, fecha_creacion, fecha_fin, fecha_inicio, modalidad, monto_total, public_id, reservado, cliente_id)
VALUES
(NULL, NULL, 'PENDIENTE', NOW(6), '2028-01-15', '2028-01-02', 'QUINCENA', 1224120, UUID_TO_BIN(UUID()), b'1', (SELECT id FROM clientes WHERE email='cliente.demo06@example.com'));
SET @reserva_demo := LAST_INSERT_ID();
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES (@reserva_demo, 6);

INSERT INTO reservas
(disponible_desde, disponible_hasta, estado_reserva, fecha_creacion, fecha_fin, fecha_inicio, modalidad, monto_total, public_id, reservado, cliente_id)
VALUES
(NULL, NULL, 'RECHAZADA', NOW(6), '2028-01-10', '2028-01-05', 'DIA', 612060, UUID_TO_BIN(UUID()), b'0', (SELECT id FROM clientes WHERE email='cliente.demo07@example.com'));
SET @reserva_demo := LAST_INSERT_ID();
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES (@reserva_demo, 7);

INSERT INTO reservas
(disponible_desde, disponible_hasta, estado_reserva, fecha_creacion, fecha_fin, fecha_inicio, modalidad, monto_total, public_id, reservado, cliente_id)
VALUES
(NULL, NULL, 'CONFIRMADA', NOW(6), '2028-02-03', '2028-01-20', 'QUINCENA', 1224120, UUID_TO_BIN(UUID()), b'1', (SELECT id FROM clientes WHERE email='cliente.demo08@example.com'));
SET @reserva_demo := LAST_INSERT_ID();
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES (@reserva_demo, 10);

INSERT INTO reservas
(disponible_desde, disponible_hasta, estado_reserva, fecha_creacion, fecha_fin, fecha_inicio, modalidad, monto_total, public_id, reservado, cliente_id)
VALUES
(NULL, NULL, 'CONFIRMADA', NOW(6), '2027-12-20', '2027-12-15', 'DIA', 65693.46, UUID_TO_BIN(UUID()), b'1', (SELECT id FROM clientes WHERE email='cliente.demo09@example.com'));
SET @reserva_demo := LAST_INSERT_ID();
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES (@reserva_demo, 12);

INSERT INTO reservas
(disponible_desde, disponible_hasta, estado_reserva, fecha_creacion, fecha_fin, fecha_inicio, modalidad, monto_total, public_id, reservado, cliente_id)
VALUES
(NULL, NULL, 'PENDIENTE', NOW(6), '2028-02-12', '2028-02-05', 'DIA', 87591.28, UUID_TO_BIN(UUID()), b'1', (SELECT id FROM clientes WHERE email='cliente.demo10@example.com'));
SET @reserva_demo := LAST_INSERT_ID();
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES (@reserva_demo, 13);

-- Verificaciones finales: cantidades agregadas y totales.
SELECT 'clientes' AS tabla, COUNT(*) AS total FROM clientes
UNION ALL SELECT 'empleados', COUNT(*) FROM empleados
UNION ALL SELECT 'productos', COUNT(*) FROM productos
UNION ALL SELECT 'reservas', COUNT(*) FROM reservas;

SELECT r.id, r.estado_reserva, r.fecha_inicio, r.fecha_fin, r.modalidad,
       c.nombre, c.apellido, rec.nombre_recurso
FROM reservas r
JOIN clientes c ON c.id = r.cliente_id
LEFT JOIN reserva_recurso rr ON rr.reserva_id = r.id
LEFT JOIN recurso rec ON rec.id = rr.recurso_id
ORDER BY r.id;

COMMIT;
