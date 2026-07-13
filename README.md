# Fundamentos Spring Boot

## Autor

Domenica Uyunkar

## Descripción

Proyecto desarrollado con Spring Boot para implementar una API REST.  
Durante las prácticas se trabajó con controladores, servicios, DTOs, validaciones, persistencia con PostgreSQL y manejo global de errores.

## Requisitos

* Java 21
* Gradle
* Spring Boot
* PostgreSQL
* Docker
* Visual Studio Code
* Postman

## Ejecución

```bash
.\gradlew.bat bootRun
```

El servidor se ejecuta en el puerto 8080.

---

# Práctica 1: Configuración inicial del proyecto

## Descripción

En esta práctica se verificó la instalación y funcionamiento inicial del proyecto Spring Boot.

Se comprobó la versión de Java, el inicio correcto del servidor Tomcat y el funcionamiento de un endpoint de estado.

## Endpoint probado

```http
GET /api/status
```

URL utilizada:

```text
http://localhost:8080/api/status
```

## Evidencias

### Evidencia 1: Versión de Java

![Java Version](img/java-version.png)

### Evidencia 2: Inicio de Tomcat

![Tomcat](img/tomcat-started.png)

### Evidencia 3: Endpoint de estado funcionando

![API Status](img/api-status.png)

### Evidencia 4: Controlador de estado creado

![Status Controller](img/api-status-controller.png)

## Conclusión

La aplicación Spring Boot se ejecutó correctamente y el endpoint de estado permitió comprobar que el servicio estaba activo.

---

# Práctica 2: Endpoint de estudiantes

## Descripción

En esta práctica se creó un endpoint REST para consultar una lista de estudiantes de prueba.

Se implementó un modelo `Student` y un controlador encargado de devolver los datos en formato JSON.

## Endpoint probado

```http
GET /v1/students
```

URL utilizada:

```text
http://localhost:8080/v1/students
```

## Evidencias

### Evidencia 1: Endpoint de estudiantes funcionando

![Endpoint estudiantes](img/postman.png)

### Evidencia 2: Controlador de estudiantes

![StudentController](img/student_controller.png)

### Evidencia 3: Modelo Student

![Student](img/student.png)

## Explicación

El controlador recibe la petición HTTP y devuelve una lista de estudiantes.  
El modelo `Student` representa los datos que se muestran en la respuesta, como `id`, `name` y `age`.

## Conclusión

Se comprobó el funcionamiento de un endpoint REST básico que devuelve información en formato JSON.

---

# Práctica 3: API REST CRUD de productos

## Descripción

En esta práctica se implementó un CRUD REST para productos.

Los productos se almacenaban temporalmente en memoria, es decir, en una lista dentro de la aplicación. Cada producto tenía `id`, `name`, `price`, `stock` y `createdAt`.

## Endpoints principales

| Método | Endpoint | Descripción |
| ------ | -------- | ----------- |
| GET | `/api/products` | Listar productos |
| GET | `/api/products/{id}` | Buscar producto por ID |
| POST | `/api/products` | Crear producto |
| PUT | `/api/products/{id}` | Actualizar producto |
| PATCH | `/api/products/{id}` | Actualizar parcialmente |
| DELETE | `/api/products/{id}` | Eliminar producto |

## Evidencias

### Evidencia 1: Listado de productos

```http
GET /api/products
```

![Listado de productos](img/products-get.png)

### Evidencia 2: Consulta de producto existente

```http
GET /api/products/1
```

![Producto existente](img/products-2.png)

### Evidencia 3: Eliminación de producto existente

```http
DELETE /api/products/1
```

![Eliminar producto](img/products-3.png)

### Evidencia 4: Eliminación de producto inexistente

```http
DELETE /api/products/1
```

![Producto inexistente](img/products-4.png)

## Explicación

El CRUD permite crear, consultar, actualizar y eliminar productos mediante peticiones HTTP.  
En esta práctica los datos todavía no se guardaban en base de datos, por lo que se perdían al reiniciar la aplicación.

## Conclusión

Se implementó correctamente un CRUD REST básico para productos usando controladores, DTOs, modelos y mappers.

---

# Práctica 4: Servicios e inyección de dependencias

## Descripción

En esta práctica se separó la lógica de negocio del controlador.

Antes, `ProductsController` tenía la lógica del CRUD.  
Después, esa lógica pasó a `ProductServiceImpl`, mientras que el controlador solamente recibe las peticiones y delega el trabajo al servicio.

## Componentes implementados

* `ProductService`
* `ProductServiceImpl`
* `ProductsController`
* `ProductMapper`
* DTOs de creación, actualización y respuesta

## Evidencias

### Evidencia 1: ProductServiceImpl

![ProductServiceImpl](img/ProductServiceImpl-1.png)

### Evidencia 2: ProductsController delegando al servicio

![ProductsController](img/products-controller-service.png)

## Explicación

La inyección de dependencias permite que Spring entregue automáticamente una instancia del servicio al controlador.

El controlador declara una dependencia de tipo `ProductService` y la recibe por constructor.  
Spring detecta que `ProductServiceImpl` implementa esa interfaz y lo usa como servicio.

## Diferencia con la práctica anterior

Antes, el controlador tenía la lógica del CRUD.  
Ahora, el controlador solo recibe la petición y llama al servicio.

Esto mejora la organización del proyecto y separa responsabilidades.

## Conclusión

La capa de servicios permitió organizar mejor el proyecto.  
`ProductsController` atiende las peticiones HTTP y `ProductServiceImpl` contiene la lógica de negocio.

---

# Práctica 5: Persistencia con PostgreSQL y JPA

## Descripción

En esta práctica se reemplazó el almacenamiento en memoria por una base de datos PostgreSQL.

Se implementó persistencia usando Spring Data JPA, entidades y repositorios.

## Configuración de PostgreSQL

La base de datos se ejecutó con Docker.

| Propiedad | Valor |
| --------- | ----- |
| Contenedor | `postgres-dev` |
| Base de datos | `devdb` |
| Usuario | `ups` |
| Contraseña | `ups123` |
| Puerto | `5432` |

## Componentes implementados

* `BaseEntity`
* `ProductEntity`
* `ProductRepository`
* Conexión a PostgreSQL en `application.yml`
* Eliminación lógica con el campo `deleted`

## Verificación en PostgreSQL

Para ingresar al contenedor se utilizó:

```bash
docker exec -it postgres-dev psql -U ups -d devdb
```

Para consultar productos:

```sql
SELECT id, name, price, stock, created_at, updated_at, deleted
FROM products
ORDER BY id;
```

## Evidencias

### Evidencia 1: Productos almacenados en PostgreSQL

![Productos PostgreSQL](img/products-postgresql.png)

### Evidencia 2: Tabla products creada

![Tabla products](img/products-table.png)

## Explicación

Con JPA, los productos se guardan en PostgreSQL y ya no desaparecen al reiniciar la aplicación.

`ProductEntity` representa la tabla `products`, mientras que `ProductRepository` permite guardar, buscar y actualizar registros sin escribir SQL manualmente.

La eliminación lógica permite marcar un producto como eliminado usando `deleted = true`, sin borrarlo físicamente de la base de datos.

## Conclusión

La aplicación pasó de usar datos en memoria a guardar información de forma permanente en PostgreSQL usando Spring Data JPA.

---

# Práctica 6: Validación de DTOs

## Descripción

En esta práctica se agregaron validaciones para controlar los datos recibidos por la API antes de guardarlos en PostgreSQL.

Las validaciones se aplicaron en los DTOs usando Jakarta Validation.

## Validaciones implementadas

| Campo | Validación |
| ----- | ---------- |
| `name` | Obligatorio, mínimo 3 y máximo 150 caracteres |
| `price` | Obligatorio y mayor o igual a 0 |
| `stock` | Obligatorio y mayor o igual a 0 |

Las validaciones se activan en el controlador usando:

```java
@Valid @RequestBody
```

## Evidencias

### Evidencia 1: Error al crear producto inválido

```http
POST /api/products
```

Cuerpo enviado:

```json
{
  "name": "",
  "price": -5,
  "stock": -1
}
```

La API respondió con:

```text
400 Bad Request
```

![POST inválido](img/post-invalido-pro.png)

### Evidencia 2: Error al actualizar producto eliminado

```http
PUT /api/products/{id}
```

![Actualizar eliminado](img/act-pro-actualizado.png)

### Evidencia 3: Consulta de productos activos

```http
GET /api/products
```

![Productos activos](img/findAll.png)

## Explicación

Las validaciones evitan que datos incorrectos lleguen al servicio o a la base de datos.

Por ejemplo, no se permite guardar productos con nombre vacío, precio negativo o stock negativo.

También se aplicaron reglas de negocio para evitar consultar, actualizar o eliminar nuevamente productos marcados como eliminados.

## Conclusión

Las validaciones permiten proteger la API frente a datos incorrectos y mejorar la calidad de la información almacenada.

---

# Práctica 7: Manejo global de errores y excepciones

## Descripción

En esta práctica se implementó un manejo global de errores para que la API devuelva respuestas uniformes y códigos HTTP correctos.

Antes, algunos errores devolvían `500 Internal Server Error`.  
Ahora se utilizan excepciones propias y `GlobalExceptionHandler`.

## Excepciones implementadas

| Excepción | Código HTTP | Uso |
| --------- | ----------- | --- |
| `NotFoundException` | 404 | Recurso no encontrado |
| `ConflictException` | 409 | Conflicto con datos existentes |
| `BadRequestException` | 400 | Solicitud inválida |

## Formato estándar de error

La API responde los errores con una estructura similar a:

```json
{
  "timestamp": "2026-06-25T05:09:04",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found",
  "path": "/api/products/999"
}
```

## Evidencias

### Evidencia 1: Producto inexistente

```http
GET /api/products/999
```

La API respondió:

```text
404 Not Found
```

![Producto inexistente](img/producto-inexistente.png)

### Evidencia 2: Producto con nombre duplicado

```http
POST /api/products
```

La API respondió:

```text
409 Conflict
```

![Producto duplicado](img/producto-duplicado.png)

### Evidencia 3: Validación de DTO

```http
POST /api/products
```

La API respondió:

```text
400 Bad Request
```

![Validación DTO](img/validacion-dto.png)

### Evidencia 4: Consulta de producto eliminado

```http
GET /api/products/{id}
```

La API respondió:

```text
404 Not Found
```

![Producto eliminado](img/producto-eliminado.png)

## Explicación

`GlobalExceptionHandler` centraliza el manejo de errores de toda la aplicación.

Las excepciones de dominio indican qué problema ocurrió, y el manejador global las convierte en respuestas HTTP claras, como `400`, `404` o `409`.

Esto evita mostrar errores técnicos al cliente y mantiene un formato uniforme en las respuestas.

## Conclusión

El manejo global de errores permitió mejorar la calidad de las respuestas de la API, usar códigos HTTP correctos y evitar mostrar información técnica interna.


# Práctica 8: Relaciones entre entidades JPA

## Descripción

En esta práctica se implementaron relaciones entre entidades usando Spring Data JPA y PostgreSQL.

Se relacionó `ProductEntity` con `UserEntity` y `CategoryEntity`, permitiendo que cada producto tenga un usuario propietario y una categoría asociada.

## Evidencias

### Evidencia 1: Descripción de la tabla products en PostgreSQL

Se verificó la estructura de la tabla `products` en PostgreSQL.

```sql
\d products
```

![Descripción tabla products](img/p8-products-descripcion.png)

### Evidencia 2: Creación de producto con relaciones

Se realizó una petición para crear un producto relacionado con un usuario y una categoría.

```http
POST /api/products
```

La respuesta evidencia el objeto anidado `owner`, el objeto anidado `category` y los campos de fecha.

![Producto con relaciones](img/p8-producto-relaciones.png)

### Evidencia 3: Consulta de productos por categoría

Se realizó la consulta de productos asociados a una categoría.

```http
GET /api/products/category/1
```

![Productos por categoría](img/p8-products-category.png)

## Explicación

`ProductEntity` se relaciona con `UserEntity` usando `@ManyToOne`, porque un usuario puede tener varios productos, pero cada producto pertenece a un solo usuario. Esta relación se guarda con `@JoinColumn`, que crea la columna de referencia hacia el usuario.

También se relaciona `ProductEntity` con `CategoryEntity` usando `@ManyToOne`, porque en esta práctica cada producto pertenece a una categoría. La anotación `@JoinColumn` permite guardar en la tabla `products` el identificador de la categoría relacionada.

## Conclusión

Las relaciones JPA permiten conectar las entidades del sistema y devolver respuestas más completas. Ahora un producto puede mostrar información relacionada, como su usuario propietario y su categoría.

---

# Práctica 9: Relación ManyToMany y consultas con filtros

## Descripción

En esta práctica se actualizó la relación entre productos y categorías. Antes, un producto pertenecía a una sola categoría. Ahora, un producto puede tener varias categorías y una categoría puede estar asociada a varios productos.

También se agregaron consultas con filtros dentro de los contextos de usuarios y categorías.

## Evidencias

### Evidencia 1: Producto creado con varias categorías

Se creó un producto enviando varias categorías en el campo `categoryIds`.

```http
POST /api/products
```

Ejemplo de cuerpo:

```json
{
  "name": "Laptop Gaming",
  "price": 1200.0,
  "stock": 5,
  "userId": 1,
  "categoryIds": [1, 2, 3]
}
```

![Producto varias categorías](img/p9-producto-varias-categorias.png)

### Evidencia 2: Consulta con filtros por usuario

Se realizó una consulta de productos de un usuario aplicando filtros.

```http
GET /api/users/1/products?name=laptop&minPrice=500
```

![Filtros por usuario](img/p9-filtros-usuari.png)

### Evidencia 3: Consulta con filtros por categoría

Se realizó una consulta de productos de una categoría aplicando filtros.

```http
GET /api/categories/2/products?userId=1
```

![Filtros por categoría](img/p9-filtros-categori.png)

## Explicación

Aunque el endpoint esté dentro del contexto `/users/{id}/products` o `/categories/{id}/products`, se sigue usando `ProductService` y `ProductRepository` porque el recurso principal consultado sigue siendo el producto. El controlador recibe la petición, pero la lógica de búsqueda y filtrado pertenece al módulo de productos.

Al pasar de `Product N ──── 1 Category` a `Product N ──── N Category`, un producto dejó de pertenecer a una sola categoría y ahora puede estar asociado a varias. Para esto se usa una tabla intermedia, normalmente llamada `product_categories`, que guarda las relaciones entre productos y categorías.

## Conclusión

La relación ManyToMany permite que los productos tengan varias categorías. Además, los filtros por usuario y categoría permiten realizar consultas más específicas desde la API.

---

# Práctica 10: Paginación con Page y Slice

## Descripción

En esta práctica se implementó paginación para evitar traer todos los registros al mismo tiempo.

Se usaron `Page`, `Slice` y parámetros como `page` y `size`. Esto permite consultar los productos en bloques pequeños y mejorar el rendimiento de la API.

## Evidencias

### Evidencia 1: Respuesta con Page

Se realizó una consulta paginada usando `Page`.

```http
GET /api/products/page?page=0&size=5
```

La respuesta evidencia `content`, `totalElements`, `totalPages`, `number`, `size`, `first` y `last`.

![Page productos](img/p10-products-page.png)

### Evidencia 2: Respuesta con Slice

Se realizó una consulta paginada usando `Slice`.

```http
GET /api/products/slice?page=0&size=5
```

En la respuesta no aparecen `totalElements` ni `totalPages`.

![Slice productos](img/p10-products-slice.png)

### Evidencia 3: Error por paginación inválida

Se realizó una petición con parámetros inválidos.

```http
GET /api/products/page?page=-1&size=0
```

La API respondió con `400 Bad Request` usando el formato estándar de `ErrorResponse`.

![Paginación inválida](img/p10-pagination-error.png)

### Evidencia 4: Categoría paginada con Page

Se realizó una consulta de productos filtrados por categoría usando `Page`.

```http
GET /api/categories/2/products/page?page=110&size=5
```

La respuesta evidencia productos filtrados por categoría, paginación aplicada y metadatos de `Page`.

![Categoría Page](img/p10-category-page.png)

### Evidencia 5: Categoría paginada con Slice

Se realizó una consulta de productos filtrados por categoría usando `Slice`.

```http
GET /api/categories/2/products/slice?page=10&size=5
```

La respuesta evidencia productos filtrados por categoría, paginación aplicada y metadatos de `Slice`.

![Categoría Slice](img/p10-category-slice.png)

## Explicación

`Page` devuelve los datos de la página y también calcula información total, como `totalElements` y `totalPages`. Es útil cuando se necesita saber cuántas páginas existen.

`Slice` devuelve los datos de la página, pero no calcula el total general. Por eso es más eficiente cuando solo se necesita saber si existe una página siguiente.

La paginación debe aplicarse en el repositorio porque así la base de datos devuelve solo los registros necesarios. Si primero se traen todos los datos a memoria y luego se pagina, se consume más memoria y se reduce el rendimiento.

## Conclusión

La paginación permite manejar grandes cantidades de datos de forma eficiente. `Page` ofrece más metadatos, mientras que `Slice` es más ligero y útil para navegación simple.

---

# Práctica 11: Autenticación JWT

## Descripción

En esta práctica se implementó autenticación con JWT usando Spring Security.

Ahora los usuarios pueden registrarse, iniciar sesión y recibir un token. Ese token se usa para acceder a endpoints protegidos.

## Evidencias

### Evidencia 1: Registro exitoso

Se realizó el registro de un usuario.

```http
POST /api/auth/register
```

Debe evidenciar `201 Created`, token generado y rol `ROLE_USER`.

![Registro exitoso](img/p11-register.png)

### Evidencia 2: Login exitoso

Se realizó el inicio de sesión.

```http
POST /api/auth/login
```

Debe evidenciar `200 OK`, token generado y roles devueltos.

![Login exitoso](img/p11-login.png)

### Evidencia 3: Endpoint protegido sin token

Se intentó acceder a un endpoint protegido sin enviar token.

```http
GET /api/products/page?page=0&size=5
```

Debe evidenciar `401 Unauthorized`.

![Endpoint sin token](img/p11-sin-token.png)

### Evidencia 4: Endpoint protegido con token

Se accedió al endpoint protegido enviando el token JWT.

```http
GET /api/products/page?page=0&size=5
Authorization: Bearer <token>
```

Debe evidenciar `200 OK`.

![Endpoint con token](img/p11-con-token.png)

## Explicación

JWT permite autenticar al usuario sin mantener sesiones en el servidor. Cuando el usuario inicia sesión, recibe un token firmado. Luego, en cada petición protegida, debe enviar ese token en el header `Authorization`.

Si el token es válido, Spring Security permite el acceso. Si no existe o es inválido, la API responde con `401 Unauthorized`.

## Conclusión

La autenticación JWT protege los endpoints de la API y permite identificar al usuario autenticado en cada petición.

---

# Práctica 12: Roles y autorización con @PreAuthorize

## Descripción

En esta práctica se implementó autorización por roles usando `@PreAuthorize`.

La autenticación verifica quién es el usuario mediante JWT, mientras que la autorización verifica qué puede hacer ese usuario según sus roles.

## Evidencias

### Evidencia 1: Usuario autenticado

Se consultó la información del usuario autenticado.

```http
GET /api/users/me
Authorization: Bearer <token>
```

Debe evidenciar `id`, `name`, `email` y `roles`.

![Usuario autenticado](img/p12-users-me.png)

### Evidencia 2: Acceso denegado por rol

Se intentó acceder al listado completo de productos con un usuario que solo tiene `ROLE_USER`.

```http
GET /api/products
Authorization: Bearer <token-role-user>
```

Debe evidenciar `403 Forbidden`.

![Acceso denegado](img/p12-products-user-403.png)

### Evidencia 3: Acceso permitido por rol ADMIN

Se accedió al listado completo de productos con un usuario que tiene `ROLE_ADMIN`.

```http
GET /api/products
Authorization: Bearer <token-role-admin>
```

Debe evidenciar `200 OK`.

![Acceso admin](img/p12-products-admin-200.png)

## Explicación

La autenticación responde a la pregunta: ¿quién eres? En este proyecto se realiza mediante un token JWT válido.

La autorización responde a la pregunta: ¿qué puedes hacer? En esta práctica se realiza mediante roles, por ejemplo `ROLE_USER` y `ROLE_ADMIN`.

`GET /api/products` debe ser solo para ADMIN porque devuelve todos los productos sin paginación. Esto puede exponer demasiada información y afectar el rendimiento.

En cambio, `GET /api/products/page` puede ser consumido por cualquier usuario autenticado porque devuelve los datos paginados y limita la cantidad de registros por respuesta.

## Conclusión

La autorización por roles permite restringir acciones según el tipo de usuario. Con `@PreAuthorize("hasRole('ADMIN')")`, solo los administradores pueden consultar el listado completo de productos.   