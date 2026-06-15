#  ArenaDesk

---
**ArenaDesk** es un sistema de administración integral para balnearios dedicado a la gestión de empleados, recursos y clientes. El proyecto se encuentra en desarrollo, presentando su primer version de backend con el uso de **Java y Spring Boot**. 
Enfoque MVC con el fin de crear una APIRest/Restish.

---

##  Propósito del Proyecto

Este sistema busca brindar un entorno simplificado e intuitivo para empleados y clientes de un balneario. Se pretende que la aplicacion conecte a los empleados con sus tareas diarias, facilitar al cliente la interacción con el balneario para aprovechar el uso del mismo (reservas,pedidos,notificaciones) y a los administradores con la recolección/manejo de datos útiles sobre otros empleados y/o recursos del balneario.

---

##  Funcionalidades Principales

- ABM, filtrado y persistencia de empleados y clientes.
- Servicio de reserva/pedidos/entregas en gastronomia y recursos disponibles(Carpas,Sombrillas, Canchas, etc), especializado para clientes.
- Servicios de pago, confirmación, con marco de seguridad e implementacion de Servicio/APIs externas(MercadoPago,SMTP).
- Registro y autenticación con manejo de roles.
- Alta y gestión extensible de recursos, productos y sectores.
- Envío notificaciones por e-mails (bienvenida,reservas y pagos).
- Documentación técnica automática de la API mediante Swagger / OpenAPI.

---

## Arquitectura interna

El proyecto sigue una arquitectura en capas:

- **Controller**: exposición de endpoints.
- **Service**: lógica de negocio.
- **Repository**: acceso a datos.
- **DTOs**: transferencia de información.
- **ModelMapper**: conversión entre entidades y DTOs.
- **Config**:
  - Configuración de Spring Security.
  - Gestión de autenticación y autorización mediante JWT.
  - Definición de Beans (ModelMapper, PasswordEncoder, etc.).
  - Configuración de Swagger/OpenAPI.

---

##  Tecnologías/Herramientas 

### Backend
- **Java 21**
- **Spring Boot 3.4.5**
  - Spring Web (REST)
  - Spring WebMVC Test y UI.
  - Spring Data JPA
  - Spring Security (JWT y roles)
  - Spring Mail
  - Spring Validation
- **JJWT** (manejo de tokens)
- **MySQL** (base de datos relacional)
- **SpringDoc OpenAPI** (Swagger)
- **Lombok**
- **ModelMapper**

### API/Servicio externo:
- MercadoPagoAPI.
- Servidor SMTP Gmail.

---

##  Dependencias en `pom.xml`

Entre las más relevantes se incluyen:

- `spring-boot-starter-web` → Exposición de endpoints REST.
- `spring-boot-starter-security` → Manejo de roles y autenticación JWT.
- `spring-boot-starter-data-jpa` → ORM con Hibernate.
- `spring-boot-starter-validation` → Validación de formularios.
- `spring-boot-starter-mail` → Envío de emails automáticos.
- `springdoc-openapi-starter-webmvc-ui` → Swagger UI para documentación.
- `jjwt-api`, `jjwt-impl`, `jjwt-jackson` → Seguridad JWT.
- `mysql-connector-j` → Conexión a base de datos.
- `lombok` → Reducción de boilerplate.
- `modelmapper` → Mapeo de entidades y DTOs.
- `sdk-java`, `com.mercadopago` → API MercadoPago.


---

##  Roles y Permisos

- **Admin:** ABM de sectores, reservas, recursos, empleados, clientes. Acceso total a las funciones desarrolladas.
- **Gerente:** ABM de empleados, pedidos, entregas y productos. Acceso a consultas de cliente/empleados y pagos.
- **Administración:** M de empleados, ABM de clientes. Acceso a consultas cliente/empleados/reservas/recursos/pagos.
- **Empleado:** Consulta de reservas, pedidos, entregas, productos, sectores y recursos.
- **Cajero:** ABM pedidos/entregas. Acceso a consultas de reservas,productos y pagos.
- **Mozo:** Consulta de reservas, pedidos y productos.
- **Repartidor:** Consulta de reservas, pedidos, entregas y productos.
- **Cliente:** ABM de reservas personales, AB de pedidos personales, consulta de historial de pagos/pedidos personales.

---
## Instalación y ejecución

### Requisitos

- Java 21
- Maven 3.9+
- MySQL 8+

### Configuración

Crear una base de datos:

CREATE DATABASE arenadesk;

Configurar las credenciales en:

[![application.yaml](https://img.shields.io/badge/application-yaml-blue)](https://github.com/FacundoAngelini/GestionBalneario/blob/main/src/main/resources/application.yaml)

src/main/resources/application.yaml

## Variables de entorno

| Variable | Descripción |
|-----------|-------------|

## Variables de Entorno

| Variable               | Descripción                                                                      |
| ---------------------- | -------------------------------------------------------------------------------- |
| DB_URL                 | URL de conexión a la base de datos MySQL.                                        |
| DB_USERNAME            | Usuario de la base de datos MySQL.                                               |
| DB_PASSWORD            | Contraseña de la base de datos MySQL.                                            |
| JWT_SECRET             | Clave utilizada para la firma de tokens JWT.                                     |
| JWT_EXPIRATION         | Tiempo de expiración del token JWT en milisegundos.                              |
| JWT_REFRESH_EXPIRATION | Tiempo de expiración del refresh token JWT en milisegundos.                      |
| MP_ACCESS_TOKEN        | Token de acceso de la API de Mercado Pago.                                       |
| MP_REDIRECT_URL        | URL de redirección utilizada durante las pruebas de pagos (Ngrok).               |
| MP_WEBHOOK_URL         | URL utilizada para recibir WebHooks de Mercado Pago durante las pruebas (Ngrok). |
| MAIL_USERNAME          | Cuenta pública del balneario : balnearioapiutn@gmail.com                         |
| GMAIL_APP              | Contraseña de aplicación generada por Gmail para SMTP.                           |


### Ejecutar

mvn spring-boot:run


---

## Modelo de Base de Datos

[Ver DER completo](DER.pdf)

---

##  Contexto Académico

Este proyecto fue desarrollado en el marco de la **Tecnicatura Universitaria en Programación** en la **Universidad Tecnológica Nacional (UTN) – Facultad Regional Mar del Plata**, como trabajo final integrador de las materias Programación III y Metodología de Sistemas I.  

Docentes: 
- **Eduardo Mango**
- **Lucrecia Bazán**

  
Fecha de entrega: **16 de Junio, 2026**

---

##  Autores

- **Facundo Ariel Angelini**
- **Francisco Carné**
  
---
