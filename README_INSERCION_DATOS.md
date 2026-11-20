# Datos de prueba MySQL

Estos scripts son solo para una base de desarrollo o prueba, no para producción.
El backend debe haberse iniciado para crear el esquema y cargar roles y permisos.
Seleccioná la base correcta en MySQL Workbench antes de ejecutar cada archivo
completo como script. El usuario MySQL necesita permisos para crear y eliminar
procedimientos almacenados. Los scripts requieren MySQL 8, tablas InnoDB y el
esquema actual de Hibernate (UUID en `BINARY(16)`).

1. `INSERT_DATA_CLIENTES_EMPLEADOS.sql`: 50 clientes.
2. `INSERT_DATA_EMPLEADOS_GERENTES.sql`: 20 empleados y 2 gerentes.
3. `INSERT_DATA_PRODUCTOS_RECURSOS.sql`: 10 productos y 10 recursos variados.

Cada script tiene su propia transacción: si falla, revierte su carga completa,
pero no revierte scripts ejecutados anteriormente. No usar `--force` ni ejecutar
solo fragmentos: ejecutar el archivo completo en una conexión. Los scripts no
son idempotentes: ante DNI, nombre de usuario, email, nombre de producto o
número de recurso existente fallan; no eliminan ni modifican los registros
existentes. Si fallan, puede quedar su procedimiento `seed_gestion_*_demo`,
que el siguiente intento elimina antes de recrearlo.

Las cuentas nuevas se llaman `demo_cliente01` a `demo_cliente50`,
`demo_empleado01` a `demo_empleado20` y `demo_gerente01` a `demo_gerente02`.
Todas usan la contraseña **Demo1234!**, solo para pruebas. Los emails son
ficticios. `refresh_token` queda en NULL y el backend lo genera en el login;
la columna debe admitir NULL, tal como declara la entidad actual.
Los registros se insertan directamente: no se envían correos ni se generan
eventos de auditoría.

Si un intento anterior dejó datos parciales, no lo vuelvas a ejecutar a ciegas
ni borres usuarios por rango: inspeccioná las filas y sus relaciones antes de
decidir qué hacer. Los nuevos nombres `demo_` son distintos de `cliente1`,
`empleado1` y `gerente1` de los scripts anteriores.

En PowerShell, una vez elegido el nombre de la base en Workbench, también se
puede ejecutar desde la raíz del proyecto con:

```powershell
Get-Content -Raw -Encoding UTF8 .\INSERT_DATA_CLIENTES_EMPLEADOS.sql |
  docker compose exec -T db sh -c 'MYSQL_PWD="$MYSQL_ROOT_PASSWORD" mysql --default-character-set=utf8mb4 -u root "$MYSQL_DATABASE"'
```

Repetí el comando cambiando el nombre del archivo. La base seleccionada es
`MYSQL_DATABASE` del contenedor; comprobá que sea la deseada antes de cargar.
