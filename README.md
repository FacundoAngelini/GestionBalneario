# ArenaDesk — Sistema de Gestión de Balneario (MVP)

**ArenaDesk** es una aplicación web full-stack diseñada para administrar las operaciones principales de un balneario. Esta versión (MVP) centraliza la gestión de recursos —carpas, sombrillas y cocheras—, tarifas, reservas, cobros presenciales, clientes, empleados y visualización de catálogo de productos.

> **Nota de alcance:** Esta documentación describe exclusivamente el **Producto Mínimo Viable (MVP)**. No incluye funcionalidades descartadas o postergadas, aunque existan clases o código parcial relacionado en el repositorio.

---

## Propósito del Proyecto

Brindar un entorno centralizado e intuitivo para conectar la operación diaria del personal del balneario con la experiencia de los clientes. Permite gestionar reservas de espacio, controlar la disponibilidad en tiempo real, administrar tarifas por temporada/período y registrar cobros presenciales con auditoría completa de movimientos.

---

## Alcance del MVP

### Funcionalidades Incluidas

| Módulo | Funcionalidades |
| :--- | :--- |
| **Cuentas y Sesión** | Registro de clientes, inicio y cierre de sesión con JWT, revocación de token en logout y gestión de datos de cuenta propia. |
| **Roles y Permisos** | Control de acceso diferenciado para *Administración*, *Gerente*, *Administrador*, *Carpero* y *Cliente*. |
| **Recursos** | Catálogo de carpas, sombrillas y cocheras; consulta de disponibilidad; ABM, baja lógica, carga masiva y validaciones de datos. |
| **Tarifas** | Asignación de precios por recurso y modalidad (*día, quincena, mes y temporada*), consulta y ajuste masivo con vista previa. |
| **Reservas** | Solicitud web por clientes, carga presencial, confirmación, rechazo, cancelación, vencimiento automático y finalización. |
| **Cobros** | Registro presencial en efectivo, descuento configurable, anulación con motivo obligatorio y emisión de comprobante interno. |
| **Auditoría** | Trazabilidad histórica de operaciones críticas (quién ejecutó la acción, cuándo y detalle). |
| **Clientes** | Listado, búsqueda por DNI, ficha e historial, alta, modificación, baja lógica y reactivación según permisos. |
| **Empleados** | ABM de personal, asignación de roles, cambios de estado, baja lógica y jerarquía de permisos. |
| **Catálogo de Productos** | Registro y visualización informativa de productos disponibles en el sistema (sin carrito ni pedidos). |

### Funcionalidades Fuera del Alcance (MVP)
La presencia de dependencias o clases parciales en el código no implica que estas características estén activas o disponibles:
* **Pedidos gastronómicos y restaurante:** Sin carrito de compras, flujos de cocina, despacho, mozos ni repartidores.
* **Pagos online (Mercado Pago):** Sin integración de pasarela de pagos ni checkout web; todos los cobros son presenciales y en efectivo.
* **Notificaciones por correo (SMTP):** Sin envío de emails de bienvenida, comprobantes ni recuperación de contraseña.
* **Aplicaciones móviles nativas:** Entrega exclusivamente como aplicación web responsive.

---

## Arquitectura General

ArenaDesk implementa una arquitectura **Cliente-Servidor** desacoplada:

* **Frontend (Angular 22):** Presenta la interfaz de usuario, gestiona la navegación, valida formularios y consume la API mediante HTTP/JSON.
* **Backend (Spring Boot):** Expone endpoints REST bajo `/api/v1`, aplica reglas de negocio, valida entradas y controla seguridad mediante JWT (`Authorization: Bearer <token>`).
* **Base de Datos (MySQL):** Persistencia relacional administrada vía Spring Data JPA / Hibernate.

### Estructura del Repositorio

```text
GestionBalneario/
├── Backend/    # Código fuente API REST (Java / Spring Boot) y configuración de BD
├── Frontend/   # Aplicación web SPA (Angular 22)
└── docs/       # Documentación técnica, diagramas y planes de prueba
```

---

## Tecnologías y Herramientas

### Frontend
* **Angular 22** (Componentes, Signals y Signal Forms)
* **TypeScript, HTML5 y CSS3**
* **Angular Material**

### Backend
* **Java 21** & **Spring Boot**
* **Spring Web** (API REST)
* **Spring Security & JWT** (Autenticación y autorización por roles)
* **Spring Data JPA / Hibernate** (ORM)
* **Spring Validation**
* **SpringDoc OpenAPI / Swagger** (Documentación interactiva)

### Base de Datos e Infraestructura
* **MySQL** (Motor relacional)
* **Docker & Docker Compose**
* **Git & GitHub**

---

## Roles y Permisos

Los permisos efectivos se validan en el backend para cada operación:

| Perfil | Permisos y Alcance |
| :--- | :--- |
| **Visitante** | Acceso al inicio, visualización de catálogo y registro/login. |
| **Cliente** | Consulta de disponibilidad, solicitud y gestión de reservas propias, cancelación (hasta 48 h antes), consulta de comprobantes y perfil. |
| **Carpero** | Consulta de reservas operativas (solo lectura, sin modificación). |
| **Administración** | Gestión operativa de reservas, carga presencial, cobros en efectivo, consulta de inventario y ABM de clientes. |
| **Gerente** | Funciones administrativas y gestión de empleados de jerarquía inferior. |
| **Administrador** | Acceso ampliado: gestión de recursos, tarifas, carga masiva, catálogo de productos y configuraciones del sistema. |

---

## Reglas de Negocio Principales

* **Recursos y Tarifas:** Las cocheras solo pueden reservarse asociadas a una carpa o sombrilla (no de forma independiente). Los ajustes masivos de tarifas requieren vista previa antes de confirmarse.
* **Reservas:** Deben estar dentro de la temporada vigente y no se permiten fechas pasadas ni superposición de períodos para un mismo recurso. Las solicitudes web quedan pendientes de confirmación y pasan a `VENCIDA` si no se confirman en plazo.
* **Cobros:** El cobro en efectivo y la confirmación de reserva son pasos separados. Para cancelar una reserva ya cobrada, primero se debe anular el cobro registrando un motivo. El comprobante emitido es interno y no fiscal.
* **Bajas Lógicas:** Clientes, empleados y recursos utilizan baja lógica para preservar la integridad histórica y auditoría.

---

## API REST & Documentación

El backend expone sus endpoints bajo el prefijo `/api/v1`. Con el servidor activo, la documentación completa y ejecutable está disponible en:

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`

### Endpoints Principales de Referencia

| Módulo | Método y Ruta | Descripción |
| :--- | :--- | :--- |
| **Auth** | `POST /api/v1/auth/login` | Inicio de sesión y obtención de token JWT. |
| **Auth** | `POST /api/v1/auth/register` | Registro de nuevo cliente. |
| **Auth** | `POST /api/v1/auth/refresh` | Renovación de sesión mediante refresh token. |
| **Auth** | `POST /api/v1/auth/logout` | Cierre de sesión y revocación del token activo. |
| **Recursos** | `GET /api/v1/recursos/catalogo` | Consulta general del catálogo de recursos. |
| **Recursos** | `GET /api/v1/recursos/disponibles` | Consulta de disponibilidad por rango de fechas. |
| **Recursos** | `POST /api/v1/recursos/lote` | Carga masiva de recursos. |
| **Reservas** | `POST /api/v1/reservas/sin-pago` | Solicitud de reserva web por cliente. |
| **Reservas** | `POST /api/v1/reservas/presencial` | Registro directo de reserva en mostrador. |
| **Cobros** | `POST /api/v1/reservas/{id}/cobro` | Registro de cobro presencial en efectivo. |
| **Cobros** | `PUT /api/v1/reservas/{id}/cobro/anular` | Anulación de cobro con motivo obligatorio. |
| **Clientes** | `GET /api/v1/clientes/buscar?dni=` | Búsqueda de cliente por documento. |

---

## Instalación y Ejecución

### Requisitos Previos
* **JDK 21**
* **Node.js y npm** (compatibles con Angular 22)
* **MySQL** (si se ejecuta de forma local sin contenedores)
* **Docker y Docker Compose** (opcional, para despliegue contenerizado)

### Configuración Inicial
1. Crear una base de datos MySQL vacía (ej. `balneario`).
2. Configurar las credenciales de base de datos y parámetros JWT en `Backend/src/main/resources/application.properties` o mediante variables de entorno.
3. En el primer arranque, el `DataLoader` inicializa roles, permisos, sectores, temporada y usuarios de prueba *(cambiar las credenciales por defecto fuera de desarrollo)*.

### Opción 1: Ejecución con Docker Compose
Desde la raíz del repositorio (`GestionBalneario/`):

```bash
docker compose up --build
```
*(Para detener los servicios: `docker compose down`)*

### Opción 2: Ejecución Manual por Separado

**1. Backend (Puerto `8080`):**
```bash
cd Backend
# Linux / macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run
```

**2. Frontend (Puerto `4200`):**
```bash
cd Frontend
npm install
npm start
```

---

## Pruebas y Limitaciones Conocidas

* **Verificación:** El sistema cuenta con un plan de pruebas funcionales manuales (validaciones, roles, autenticación y alta de recursos con evidencia documentada), sin suite de pruebas automatizadas en esta entrega.
* **Esquema de BD:** Gestionado mediante `ddl-auto` de Hibernate (sin migraciones versionadas como Flyway/Liquibase).
* **Sesiones y Paginación:** La revocación de permisos o cambio de estado de un empleado aplica al expirar su token actual. Algunos listados aplican filtrado del lado del cliente.

---

## Contexto Académico

Este proyecto fue desarrollado en el marco de la **Tecnicatura Universitaria en Programación (TUP)** en la **Universidad Tecnológica Nacional (UTN) – Facultad Regional Mar del Plata**, correspondiente a la materia **Programación IV**.

### Docentes
* **Quimey D'Agostino**
* **Nicanor Dondero**

### Autores
* **Facundo Ariel Angelini**
* **Francisco Carné**