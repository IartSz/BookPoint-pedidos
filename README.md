# BookPoint · Microservicio de Pedidos (`ms-pedido`)

Microservicio encargado de la gestión de pedidos dentro del sistema **BookPoint**. Expone una API REST con soporte **HATEOAS** y se comunica con otros microservicios del ecosistema para construir y procesar las órdenes de compra.

---

## 🛠️ Tecnologías

- **Java 17+**
- **Spring Boot 4**
- **Spring Web (MVC)**
- **Spring HATEOAS** — respuestas con `EntityModel` / `CollectionModel`
- **Spring Data JPA**
- **MySQL** (entorno de producción/desarrollo)
- **H2** (base de datos en memoria para tests)
- **RestTemplate** — comunicación con otros microservicios
- **SpringDoc OpenAPI / Swagger** — documentación de la API
- **JaCoCo** — reportes de cobertura de tests
- **JUnit 5 + Mockito** — pruebas unitarias e integración

---

## 🏗️ Rol en la arquitectura

`ms-pedido` orquesta la creación de un pedido consultando a otros microservicios. **`⚠️ Verificar`** según tus llamadas reales:

| Microservicio | Para qué se consulta |
|---------------|----------------------|
| `ms-usuario`    | Validar que el usuario exista |
| `ms-carro`      | Obtener los ítems del carrito |
| `ms-direccion`  | Resolver la dirección de despacho |
| `ms-cupones`    | Aplicar descuentos al total |

```
Cliente → Gateway (8080) → ms-pedido (8081)
                                │
                                ├── RestTemplate → ms-usuario
                                ├── RestTemplate → ms-carro
                                ├── RestTemplate → ms-direccion
                                └── RestTemplate → ms-cupones
```

---

## ✅ Requisitos previos

- JDK 17 o superior
- Maven 3.8+
- MySQL en ejecución (para el perfil por defecto)
- Los microservicios dependientes corriendo, si vas a probar el flujo completo

---

## ⚙️ Configuración

### `src/main/resources/application.properties`

```properties
spring.application.name=ms-pedido
server.port=8081

# Base de datos (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/bookpoint_pedidos
spring.datasource.username=root
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

### `src/test/resources/application.properties` (H2 en memoria)

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## 🌐 Acceso vía API Gateway

En producción no se accede directo al puerto `8093`, sino a través del gateway en el puerto `8080`:

```
GET http://localhost:8080/api/v1/pedidos
```

Ruta configurada en el gateway:

```yaml
- id: ms-pedido
  uri: http://localhost:8081
  predicates:
    - Path=/api/v1/pedidos,/api/v1/pedidos/**
```

---

## 📡 Endpoints

> **`⚠️ Verificar`** — ajusta esta tabla a los métodos reales de tu `PedidoController`.

Base: `/api/v1/pedidos`

| Método | Endpoint                  | Descripción                          | Código éxito |
|--------|---------------------------|--------------------------------------|--------------|
| `GET`    | `/api/v1/pedidos`         | Listar todos los pedidos             | `200 OK`     |
| `GET`    | `/api/v1/pedidos/{id}`    | Obtener un pedido por su ID          | `200 OK`     |
| `POST`   | `/api/v1/pedidos`         | Crear un nuevo pedido                | `201 Created`|
| `PUT`    | `/api/v1/pedidos/{id}`    | Actualizar un pedido existente       | `200 OK`     |
| `DELETE` | `/api/v1/pedidos/{id}`    | Eliminar un pedido                   | `204 No Content` |

### Ejemplo de respuesta (HATEOAS)

```json
{
  "id": 1,
  "usuarioId": 42,
  "fecha": "2026-06-17",
  "estado": "PENDIENTE",
  "total": 25990,
  "_links": {
    "self": { "href": "http://localhost:8093/api/v1/pedidos/1" },
    "pedidos": { "href": "http://localhost:8093/api/v1/pedidos" }
  }
}
```

---

## 🗂️ Modelo de datos — `Pedido`

> **`⚠️ Verificar`** — campos de ejemplo, ajústalos a tu entidad real.

| Campo        | Tipo        | Descripción                          |
|--------------|-------------|--------------------------------------|
| `id`         | `Long`      | Identificador único (PK)             |
| `usuarioId`  | `Long`      | ID del usuario que realiza el pedido |
| `fecha`      | `LocalDate` | Fecha de creación del pedido         |
| `estado`     | `String`    | Estado (PENDIENTE, PAGADO, ENVIADO…) |
| `total`      | `Integer`   | Monto total del pedido               |
| `direccionId`| `Long`      | ID de la dirección de despacho       |

---

- **Pruebas unitarias:** `@ExtendWith(MockitoExtension.class)` con `@Mock` / `@InjectMocks`.
- **Pruebas de integración:** `@SpringBootTest` + `@AutoConfigureMockMvc` + `@ActiveProfiles("test")`.
- Las llamadas a otros microservicios (`RestTemplate`) se mockean con Mockito.

---

## 📁 Estructura del proyecto

```
ms-pedido/
├── src/
│   ├── main/
│   │   ├── java/com/bookpoint/pedido/
│   │   │   ├── controller/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── PedidoApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/bookpoint/pedido/
│       └── resources/
│           └── application.properties
└── pom.xml
```

---

## 👤 Autor

Proyecto **BookPoint** — Microservicio de Pedidos.
