# Fundamentos Spring Boot

## Autor

Domenica Uyunkar

## Descripción

Proyecto básico desarrollado con Spring Boot para verificar la instalación, configuración y funcionamiento del framework. La aplicación expone endpoints REST que permiten comprobar el estado del servicio y consultar una lista de estudiantes de prueba.

## Requisitos

* Java 21
* Gradle
* Spring Boot

## Ejecución

```bash
.\gradlew.bat bootRun
```

Una vez iniciado el proyecto, el servidor se ejecutará en el puerto 8080.

## Endpoints disponibles

### 1. Estado del servicio

```http
GET /api/status
```

Ejemplo de respuesta:

```json
{
    "status":"running",
    "service":"Spring Boot API",
    "timestamp":"2026-06-12T10:51:45.882028"
}
```

URL de prueba:

```text
http://localhost:8080/api/status
```

---

### 2. Listado de estudiantes

```http
GET /v1/students
```

Ejemplo de respuesta:

```json
[
    {
        "id": 1,
        "name": "JUAN",
        "age": 20
    },
    {
        "id": 2,
        "name": "MARIA",
        "age": 22
    }
]
```

URL de prueba:

```text
http://localhost:8080/v1/students
```

## Herramientas utilizadas

* Java 21
* Spring Boot
* Gradle
* Visual Studio Code
* Postman

## Evidencias

### Evidencia 1: Versión de Java

![Java Version](img/java-version.png)

### Evidencia 2: Inicio de Tomcat

![Tomcat](img/tomcat-started.png)

### Evidencia 3: Endpoint de estado funcionando

![API Status](img/api-status.png)

### Evidencia 4: Controlador de estado creado

![Status Controller](img/status-controller.png)

### Evidencia 5: Endpoint de estudiantes funcionando

![Endpoint](img/postman.png)
http://localhost:8080/v1/students

### Evidencia 6: Controlador de estudiantes creado

![StudentController](img/student_controller.png)

### Evidencia 7: Modelo Student creado

![Student](img/student.png)


---

# Práctica 3: API REST CRUD de productos

## Descripción

En esta práctica se implementó un CRUD REST completo para el recurso de productos utilizando controladores, DTOs, modelos y mappers.

Los productos se almacenan temporalmente en una lista en memoria, sin utilizar una base de datos.

Cada producto contiene los siguientes atributos:

```java
private Long id;
private String name;
private Double price;
private Integer stock;
private LocalDateTime createdAt;
```

El identificador se genera automáticamente desde el backend y no debe ser enviado por el cliente.

## Endpoints de productos

| Método | Endpoint             | Descripción                          |
| ------ | -------------------- | ------------------------------------ |
| GET    | `/api/products`      | Obtener todos los productos          |
| GET    | `/api/products/{id}` | Obtener un producto por ID           |
| POST   | `/api/products`      | Crear un producto                    |
| PUT    | `/api/products/{id}` | Actualizar completamente un producto |
| PATCH  | `/api/products/{id}` | Actualizar parcialmente un producto  |
| DELETE | `/api/products/{id}` | Eliminar un producto                 |

## Evidencias del CRUD de productos

### Evidencia 8: Listado de tres productos creados

Se realizó la petición:

```http
GET /api/products
```

URL utilizada:

```text
http://localhost:8080/api/products
```

![Listado de productos](img/products-get.png)

### Evidencia 9: Consulta de un producto existente

Se realizó una consulta utilizando el identificador de un producto registrado:

```http
GET /api/products/1
```

URL utilizada:

```text
http://localhost:8080/api/products/1
```

![Producto existente](img/products-2.png)

### Evidencia 10: Eliminación de un producto existente

Se realizó la eliminación de un producto registrado mediante su identificador:

```http
DELETE /api/products/1
```

URL utilizada:

```text
http://localhost:8080/api/products/1
```

![Eliminación de producto existente](img/products-3.png)

### Evidencia 11: Eliminación de un producto inexistente

Se intentó eliminar nuevamente el producto con el mismo identificador. El sistema devolvió un mensaje indicando que el producto no existe:

```http
DELETE /api/products/1
```

URL utilizada:

```text
http://localhost:8080/api/products/1
```

![Eliminación de producto inexistente](img/products-4.png)

---

# Práctica 4: Servicios, lógica de negocio e inyección de dependencias

## Descripción

En esta práctica se implementó una capa de servicios para separar la lógica de negocio del controlador.

Anteriormente, `ProductsController` contenía la lista en memoria y toda la lógica necesaria para crear, consultar, actualizar y eliminar productos.

Ahora, la lógica del CRUD se encuentra en `ProductServiceImpl`, mientras que `ProductsController` solamente recibe las peticiones HTTP y delega las operaciones al servicio.

El nuevo flujo de la aplicación es:

```text
Cliente
   ↓
ProductsController
   ↓
ProductService
   ↓
ProductServiceImpl
   ↓
List<ProductModel>
   ↓
ProductMapper
   ↓
ProductResponseDto
   ↓
Cliente
```

## Estructura del módulo de productos

```text
products/
├── controllers/
│   └── ProductsController.java
├── dtos/
│   ├── CreateProductDto.java
│   ├── PartialUpdateProductDto.java
│   ├── ProductResponseDto.java
│   └── UpdateProductDto.java
├── mappers/
│   └── ProductMapper.java
├── models/
│   └── ProductModel.java
└── services/
    ├── ProductService.java
    └── ProductServiceImpl.java
```

## ProductService

La interfaz `ProductService` declara las operaciones disponibles para la gestión de productos.

```java
public interface ProductService {

    List<ProductResponseDto> findAll();

    Object findOne(Long id);

    ProductResponseDto create(CreateProductDto dto);

    Object update(Long id, UpdateProductDto dto);

    Object partialUpdate(Long id, PartialUpdateProductDto dto);

    Object delete(Long id);
}
```

La interfaz define el contrato del servicio, pero no contiene la implementación de la lógica.

## ProductServiceImpl

La clase `ProductServiceImpl` implementa la interfaz `ProductService` y contiene la lógica del CRUD de productos.

```java
@Service
public class ProductServiceImpl implements ProductService {

    private final List<ProductModel> products = new ArrayList<>();
    private Long currentId = 1L;

    @Override
    public List<ProductResponseDto> findAll() {
        return products.stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public Object findOne(Long id) {
        return products.stream()
                .filter(product -> product.getId().equals(id))
                .findFirst()
                .map(product -> (Object) ProductMapper.toResponse(product))
                .orElseGet(() ->
                        new ErrorResponseDto("Product not found"));
    }

    @Override
    public ProductResponseDto create(CreateProductDto dto) {

        ProductModel product = ProductMapper.toModel(dto);

        product.setId(currentId);
        currentId++;

        products.add(product);

        return ProductMapper.toResponse(product);
    }

    @Override
    public Object update(Long id, UpdateProductDto dto) {

        ProductModel product = products.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (product == null) {
            return new ErrorResponseDto("Product not found");
        }

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        return ProductMapper.toResponse(product);
    }

    @Override
    public Object partialUpdate(
            Long id,
            PartialUpdateProductDto dto) {

        ProductModel product = products.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (product == null) {
            return new ErrorResponseDto("Product not found");
        }

        if (dto.getName() != null) {
            product.setName(dto.getName());
        }

        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }

        if (dto.getStock() != null) {
            product.setStock(dto.getStock());
        }

        return ProductMapper.toResponse(product);
    }

    @Override
    public Object delete(Long id) {

        boolean removed = products.removeIf(
                product -> product.getId().equals(id));

        if (!removed) {
            return new ErrorResponseDto("Product not found");
        }

        return new Object() {
            public String message = "Deleted successfully";
        };
    }
}
```

En esta implementación se evidencia:

* El uso de la anotación `@Service`.
* La lista de productos almacenada en memoria.
* La generación automática de identificadores.
* El uso de `ProductMapper`.
* La implementación de los seis métodos del CRUD.
* El manejo de productos inexistentes con `ErrorResponseDto`.

### Evidencia adicional de ProductServiceImpl

![ProductServiceImpl](img/ProductServiceImpl-1.png)

## ProductsController

El controlador ya no contiene la lista de productos ni la lógica interna del CRUD.

```java
@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService service;

    public ProductsController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponseDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Object findOne(@PathVariable Long id) {
        return service.findOne(id);
    }

    @PostMapping
    public ProductResponseDto create(
            @RequestBody CreateProductDto dto) {

        return service.create(dto);
    }

    @PutMapping("/{id}")
    public Object update(
            @PathVariable Long id,
            @RequestBody UpdateProductDto dto) {

        return service.update(id, dto);
    }

    @PatchMapping("/{id}")
    public Object partialUpdate(
            @PathVariable Long id,
            @RequestBody PartialUpdateProductDto dto) {

        return service.partialUpdate(id, dto);
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
```

En el controlador se evidencia:

* La declaración de una dependencia de tipo `ProductService`.
* La inyección del servicio mediante el constructor.
* La existencia de los seis endpoints REST.
* La delegación de las operaciones al servicio.
* La ausencia de la lista en memoria.
* La ausencia de lógica CRUD dentro del controlador.

## ¿Cómo se inyecta el servicio en el controlador?

El servicio se inyecta en `ProductsController` mediante inyección de dependencias por constructor.

Primero, el controlador declara una variable de tipo `ProductService`:

```java
private final ProductService service;
```

Después, la dependencia se recibe en el constructor:

```java
public ProductsController(ProductService service) {
    this.service = service;
}
```

Spring Boot detecta que `ProductServiceImpl` implementa la interfaz `ProductService` y está anotada con `@Service`.

Por esta razón, Spring crea automáticamente una instancia de `ProductServiceImpl` y la entrega al constructor de `ProductsController`.

De esta manera, el controlador solamente recibe las peticiones HTTP y delega la lógica del CRUD al servicio.

## Diferencia entre la práctica anterior y la práctica actual

### Antes

```text
Postman
   ↓
ProductsController
   ↓
Lista de productos en memoria
```

El controlador recibía las peticiones HTTP y también ejecutaba directamente la lógica del CRUD.

### Ahora

```text
Postman
   ↓
ProductsController
   ↓
ProductService
   ↓
ProductServiceImpl
   ↓
Lista de productos en memoria
```

El controlador recibe las peticiones y delega cada operación al servicio. La lista y la lógica del CRUD ahora se encuentran en `ProductServiceImpl`.

## Conclusión

La implementación de la capa de servicios permite separar las responsabilidades de la aplicación.

`ProductsController` se encarga de recibir las peticiones HTTP, mientras que `ProductServiceImpl` administra la lógica de negocio, la generación de identificadores y el almacenamiento temporal de los productos.

Esta organización mejora la estructura del proyecto y facilita la incorporación futura de repositorios y una base de datos.

---

# Práctica 5: Persistencia con PostgreSQL, JPA y repositorios

## Descripción

En esta práctica se reemplazó el almacenamiento temporal en memoria por una base de datos PostgreSQL.

En las prácticas anteriores, los productos se guardaban en una lista dentro de `ProductServiceImpl`, por lo que la información se perdía cada vez que se reiniciaba la aplicación.

Ahora, los productos se almacenan permanentemente en PostgreSQL utilizando:

* Spring Data JPA
* Hibernate
* PostgreSQL
* Docker
* Entidades JPA
* Repositorios

La aplicación mantiene la separación entre controladores, servicios, modelos, DTOs, mappers, entidades y repositorios.

## Configuración de PostgreSQL

La base de datos se ejecuta mediante un contenedor de Docker con la siguiente configuración:

| Propiedad     | Valor          |
| ------------- | -------------- |
| Contenedor    | `postgres-dev` |
| Servidor      | `localhost`    |
| Puerto        | `5432`         |
| Base de datos | `devdb`        |
| Usuario       | `ups`          |
| Contraseña    | `ups123`       |

La conexión desde Spring Boot se configuró en `application.yml`:

```yaml
server:
  servlet:
    context-path: /api

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/devdb
    username: ups
    password: ups123

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

La opción:

```yaml
ddl-auto: update
```

permite que Hibernate cree o actualice automáticamente las tablas sin eliminar los datos existentes.

## Estructura del módulo de productos

```text
products/
├── controllers/
│   └── ProductsController.java
├── dtos/
│   ├── CreateProductDto.java
│   ├── PartialUpdateProductDto.java
│   ├── ProductResponseDto.java
│   └── UpdateProductDto.java
├── entities/
│   └── ProductEntity.java
├── mappers/
│   └── ProductMapper.java
├── models/
│   └── ProductModel.java
├── repositories/
│   └── ProductRepository.java
└── services/
    ├── ProductService.java
    └── ProductServiceImpl.java
```

Además, se creó la clase base:

```text
core/
└── entities/
    └── BaseEntity.java
```

## BaseEntity

`BaseEntity` es una superclase que contiene los campos comunes de persistencia que pueden ser heredados por diferentes entidades.

```java
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean deleted;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.deleted = false;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

La anotación `@MappedSuperclass` indica que esta clase no genera una tabla propia. Sus atributos son heredados por las entidades que extienden de ella.

Los campos heredados son:

* `id`: identificador generado automáticamente por PostgreSQL.
* `createdAt`: fecha y hora de creación del registro.
* `updatedAt`: fecha y hora de la última actualización.
* `deleted`: indica si el registro fue eliminado lógicamente.

## ProductEntity

`ProductEntity` representa la tabla `products` dentro de PostgreSQL.

```java
@Entity
@Table(name = "products")
public class ProductEntity extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock;
}
```

La entidad extiende de `BaseEntity`, por lo que también contiene los campos `id`, `createdAt`, `updatedAt` y `deleted`.

## ProductRepository

El repositorio permite realizar operaciones de persistencia mediante Spring Data JPA.

```java
@Repository
public interface ProductRepository
        extends JpaRepository<ProductEntity, Long> {
}
```

Al extender de `JpaRepository`, se obtienen métodos como:

```java
save()
findAll()
findById()
deleteById()
existsById()
count()
```

Por esta razón, ya no es necesario mantener una lista de productos en memoria.

## Flujo de datos hacia PostgreSQL

Cuando se crea un producto, el flujo es el siguiente:

```text
Postman
   ↓
ProductsController
   ↓
ProductService
   ↓
ProductServiceImpl
   ↓
ProductMapper
   ↓
ProductEntity
   ↓
ProductRepository
   ↓
Spring Data JPA / Hibernate
   ↓
PostgreSQL
```

El cliente envía una petición HTTP desde Postman hacia `ProductsController`.

El controlador delega la operación a `ProductService`. La implementación `ProductServiceImpl` convierte el DTO recibido en un modelo mediante `ProductMapper`.

Posteriormente, el modelo se convierte en una entidad y se guarda mediante `ProductRepository`.

Spring Data JPA e Hibernate generan la sentencia SQL necesaria para insertar el producto en la tabla `products` de PostgreSQL.

## Flujo de datos desde PostgreSQL hacia la API

Cuando se consulta un producto, el flujo es:

```text
PostgreSQL
   ↓
ProductRepository
   ↓
ProductEntity
   ↓
ProductMapper
   ↓
ProductModel
   ↓
ProductResponseDto
   ↓
ProductsController
   ↓
Postman
```

El repositorio recupera la entidad almacenada en PostgreSQL.

Después, `ProductMapper` convierte la entidad en un modelo y posteriormente en un `ProductResponseDto`.

Finalmente, el controlador devuelve al cliente solamente los datos públicos del producto.

## Endpoints persistentes de productos

| Método | Endpoint             | Descripción                          |
| ------ | -------------------- | ------------------------------------ |
| GET    | `/api/products`      | Obtener todos los productos          |
| GET    | `/api/products/{id}` | Obtener un producto por ID           |
| POST   | `/api/products`      | Crear un producto en PostgreSQL      |
| PUT    | `/api/products/{id}` | Actualizar completamente un producto |
| PATCH  | `/api/products/{id}` | Actualizar parcialmente un producto  |
| DELETE | `/api/products/{id}` | Realizar eliminación lógica          |

## Eliminación lógica

El endpoint `DELETE` no elimina físicamente el registro de la base de datos.

En su lugar, modifica el campo:

```java
deleted = true;
```

Esto permite conservar el registro en PostgreSQL y mantener un historial de la información.

## Verificación en PostgreSQL

Para ingresar al contenedor se utilizó:

```bash
docker exec -it postgres-dev psql -U ups -d devdb
```

Para consultar las tablas:

```sql
\dt
```

Para consultar los productos:

```sql
SELECT id, name, price, stock, created_at, updated_at, deleted
FROM products
ORDER BY id;
```

## Evidencia: cinco productos almacenados en PostgreSQL

La siguiente evidencia muestra los cinco productos creados mediante la API REST y almacenados correctamente en PostgreSQL.

![Cinco productos en PostgreSQL](img/products-postgresql.png)

Los productos almacenados fueron:

| ID | Producto  | Precio | Stock |
| -: | --------- | -----: | ----: |
|  1 | Laptop    | 850.50 |    10 |
|  2 | Teclado   |  35.99 |    20 |
|  3 | Audífonos |  49.90 |    15 |
|  4 | Mouse     |  18.75 |    30 |
|  5 | Monitor   | 225.00 |     8 |

La columna `created_at` demuestra que `BaseEntity` asignó automáticamente la fecha de creación.

La columna `deleted` aparece con el valor `f`, que en PostgreSQL significa `false`. Esto indica que los productos todavía no han sido eliminados lógicamente.

## Persistencia de la información

A diferencia del almacenamiento en memoria utilizado en las prácticas anteriores, los productos no desaparecen cuando se reinicia Spring Boot.

Los registros permanecen almacenados dentro de la base de datos PostgreSQL y pueden volver a consultarse después de reiniciar la aplicación.

## Conclusión

La implementación de Spring Data JPA y PostgreSQL permite almacenar los productos de manera permanente.

`ProductsController` recibe las peticiones HTTP, `ProductServiceImpl` administra la lógica de negocio y `ProductRepository` ejecuta las operaciones de persistencia.

`ProductEntity` representa la tabla `products`, mientras que `BaseEntity` proporciona los campos comunes de auditoría y eliminación lógica.

Esta arquitectura mantiene separadas las responsabilidades de cada capa y permite que la aplicación pueda ampliarse de manera organizada.



---
# Práctica 6: Validación de DTOs y control de datos de entrada

## Descripción

En esta práctica se implementaron validaciones para controlar los datos recibidos por la API antes de enviarlos al servicio y almacenarlos en PostgreSQL.

Las validaciones se aplicaron mediante Jakarta Validation en los DTOs de creación, actualización completa y actualización parcial de productos.

También se reemplazó `ProductMapper` por métodos de conversión dentro del modelo de dominio `Product`.

## Dependencia de validación

Para utilizar Jakarta Validation se agregó la siguiente dependencia en `build.gradle`:

```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

## Validaciones implementadas

| Campo   | Validación                                    |
| ------- | --------------------------------------------- |
| `name`  | Obligatorio, mínimo 3 y máximo 150 caracteres |
| `price` | Obligatorio y mayor o igual a 0               |
| `stock` | Obligatorio y mayor o igual a 0               |

Las validaciones se activaron en `ProductsController` mediante:

```java
@Valid @RequestBody
```

Ejemplo del endpoint de creación:

```java
@PostMapping
public ProductResponseDto create(
        @Valid @RequestBody CreateProductDto dto) {

    return service.create(dto);
}
```

Cuando los datos no cumplen las reglas establecidas, Spring Boot detiene la petición antes de ejecutar el servicio y devuelve una respuesta:

```text
400 Bad Request
```

## Validación de CreateProductDto

El DTO utilizado para crear productos contiene las siguientes reglas:

```java
public class CreateProductDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(
            min = 3,
            max = 150,
            message = "El nombre debe tener entre 3 y 150 caracteres"
    )
    private String name;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(
            value = "0.0",
            inclusive = true,
            message = "El precio debe ser mayor o igual a 0"
    )
    private Double price;

    @NotNull(message = "El stock es obligatorio")
    @Min(
            value = 0,
            message = "El stock debe ser mayor o igual a 0"
    )
    private Integer stock;
}
```

## Validación de UpdateProductDto

El DTO de actualización completa exige que todos los campos sean enviados y que cumplan las reglas de validación.

```java
@NotBlank
@Size(min = 3, max = 150)
private String name;

@NotNull
@DecimalMin(value = "0.0", inclusive = true)
private Double price;

@NotNull
@Min(0)
private Integer stock;
```

## Validación de PartialUpdateProductDto

En la actualización parcial los campos pueden ser nulos, porque solamente se modifica la información enviada por el cliente.

```java
@Size(min = 3, max = 150)
private String name;

@DecimalMin(value = "0.0", inclusive = true)
private Double price;

@Min(0)
private Integer stock;
```

## Métodos factory del dominio Product

El nuevo modelo de dominio `Product` se encarga de realizar sus propias conversiones.

Los métodos implementados son:

```java
Product.fromDto()
Product.fromEntity()
product.toEntity()
product.toResponseDto()
product.update()
product.partialUpdate()
```

### Conversión desde DTO

```java
public static Product fromDto(CreateProductDto dto) {

    Product product = new Product();

    product.setName(dto.getName());
    product.setPrice(dto.getPrice());
    product.setStock(dto.getStock());
    product.setDeleted(false);

    return product;
}
```

### Conversión desde entidad

```java
public static Product fromEntity(ProductEntity entity) {

    Product product = new Product();

    product.setId(entity.getId());
    product.setName(entity.getName());
    product.setPrice(entity.getPrice());
    product.setStock(entity.getStock());
    product.setCreatedAt(entity.getCreatedAt());
    product.setUpdatedAt(entity.getUpdatedAt());
    product.setDeleted(entity.isDeleted());

    return product;
}
```

### Conversión hacia entidad

```java
public ProductEntity toEntity() {

    ProductEntity entity = new ProductEntity();

    entity.setId(this.id);
    entity.setName(this.name);
    entity.setPrice(this.price);
    entity.setStock(this.stock);
    entity.setCreatedAt(this.createdAt);
    entity.setUpdatedAt(this.updatedAt);
    entity.setDeleted(this.deleted);

    return entity;
}
```

### Conversión hacia DTO de respuesta

```java
public ProductResponseDto toResponseDto() {

    ProductResponseDto response = new ProductResponseDto();

    response.setId(this.id);
    response.setName(this.name);
    response.setPrice(this.price);
    response.setStock(this.stock);

    return response;
}
```

Debido a esta modificación, el módulo de productos ya no utiliza:

```text
ProductMapper.java
ProductModel.java
```

Las conversiones se encuentran centralizadas en el dominio `Product`.

## Reglas de negocio implementadas

En `ProductServiceImpl` se agregaron las siguientes reglas:

* Los productos eliminados no aparecen en `findAll()`.
* No se permite consultar un producto eliminado.
* No se permite actualizar un producto eliminado.
* No se permite realizar una actualización parcial sobre un producto eliminado.
* No se permite eliminar dos veces el mismo producto.
* La eliminación continúa siendo lógica mediante el atributo `deleted`.

## Exclusión de productos eliminados

El método `findAll()` filtra las entidades eliminadas:

```java
@Override
public List<ProductResponseDto> findAll() {

    return productRepository.findAll()
            .stream()
            .filter(entity -> !entity.isDeleted())
            .map(Product::fromEntity)
            .map(Product::toResponseDto)
            .toList();
}
```

La siguiente condición permite devolver únicamente los productos activos:

```java
.filter(entity -> !entity.isDeleted())
```

## Validación de producto activo

Antes de consultar o actualizar un producto, se verifica que exista y que no esté eliminado:

```java
private ProductEntity findActiveEntity(Long id) {

    ProductEntity entity = productRepository.findById(id)
            .orElseThrow(() ->
                    new IllegalStateException(
                            "Product not found"));

    if (entity.isDeleted()) {
        throw new IllegalStateException(
                "Product is deleted");
    }

    return entity;
}
```

## Control de eliminación duplicada

Antes de eliminar un producto se comprueba si ya estaba eliminado:

```java
if (entity.isDeleted()) {
    throw new IllegalStateException(
            "Product already deleted");
}
```

## Flujo de validación

```text
Postman
   ↓
ProductsController
   ↓
@Valid
   ↓
DTO con anotaciones de validación
   ↓
ProductService
   ↓
ProductServiceImpl
   ↓
ProductRepository
   ↓
PostgreSQL
```

Si el DTO contiene datos inválidos, la petición se detiene antes de llegar a `ProductServiceImpl`.

## Evidencias

### Evidencia 1: Error al crear un producto inválido

Se realizó una petición:

```http
POST /api/products
```

Con el siguiente cuerpo inválido:

```json
{
  "name": "",
  "price": -5,
  "stock": -1
}
```

El nombre se encuentra vacío, el precio es negativo y el stock también es negativo.

Spring Boot rechazó la petición y devolvió:

```text
400 Bad Request
```

![POST inválido de producto](img/post-invalido-pro.png)

Esta evidencia demuestra que las anotaciones `@NotBlank`, `@Size`, `@DecimalMin` y `@Min` se ejecutan correctamente mediante `@Valid`.

### Evidencia 2: Error al actualizar un producto eliminado

Primero se realizó la eliminación lógica del producto mediante:

```http
DELETE /api/products/{id}
```

Después se intentó actualizar el mismo producto utilizando:

```http
PUT /api/products/{id}
```

El servicio rechazó la operación porque el producto tenía el campo:

```text
deleted = true
```

![Actualizar producto eliminado](img/act-pro-actualizado.png)

La operación fue rechazada mediante la siguiente regla de negocio:

```java
if (entity.isDeleted()) {
    throw new IllegalStateException(
            "Product is deleted");
}
```

En esta práctica todavía no se utiliza un manejador global de excepciones, por lo que la respuesta puede mostrarse como un error técnico del servidor.

### Evidencia 3: Consulta de productos activos

Se realizó la petición:

```http
GET /api/products
```

La respuesta solamente muestra los productos activos.

![Listado de productos activos](img/findAll.png)

El producto eliminado lógicamente no aparece en la respuesta debido al filtro:

```java
.filter(entity -> !entity.isDeleted())
```

## Resultados obtenidos

Las pruebas realizadas permitieron comprobar que:

* Un nombre vacío genera `400 Bad Request`.
* Un nombre con menos de tres caracteres genera `400 Bad Request`.
* Un precio negativo genera `400 Bad Request`.
* Un stock negativo genera `400 Bad Request`.
* Un producto válido se almacena correctamente en PostgreSQL.
* No se permite actualizar un producto eliminado.
* No se permite eliminar dos veces el mismo producto.
* `findAll()` no devuelve productos eliminados.
* Las conversiones del módulo se realizan mediante el dominio `Product`.

## Conclusión

La implementación de Jakarta Validation permite impedir que datos incorrectos lleguen a la lógica de negocio y sean almacenados en PostgreSQL.

Las reglas de los DTOs validan el formato y los valores recibidos, mientras que `ProductServiceImpl` valida reglas de negocio relacionadas con la eliminación lógica.

La incorporación de métodos factory en el dominio `Product` permite centralizar las conversiones entre DTOs, entidades y respuestas, eliminando la dependencia de `ProductMapper`.

Finalmente, la aplicación garantiza que los productos eliminados no puedan consultarse, actualizarse ni eliminarse nuevamente.

---

# Práctica 7: Manejo global de errores y excepciones

## Descripción

En esta práctica se implementó un sistema global para manejar los errores y excepciones producidos en la API REST.

Anteriormente, los servicios lanzaban excepciones genéricas como:

```java
throw new IllegalStateException("Product not found");
```

Esto provocaba respuestas técnicas e incorrectas, normalmente con estado:

```text
500 Internal Server Error
```

Ahora se utilizan excepciones propias de la aplicación, asociadas a códigos HTTP específicos:

| Excepción             | Código HTTP | Uso                                      |
| --------------------- | ----------: | ---------------------------------------- |
| `NotFoundException`   |         404 | Recurso inexistente o eliminado          |
| `ConflictException`   |         409 | Conflicto con datos ya registrados       |
| `BadRequestException` |         400 | Solicitud inválida por reglas de negocio |

Todas las excepciones son capturadas por `GlobalExceptionHandler`, que genera una respuesta uniforme mediante `ErrorResponse`.

## Estructura implementada

```text
core/
└── exceptions/
    ├── base/
    │   └── ApplicationException.java
    ├── domain/
    │   ├── NotFoundException.java
    │   ├── ConflictException.java
    │   └── BadRequestException.java
    ├── handler/
    │   └── GlobalExceptionHandler.java
    └── response/
        └── ErrorResponse.java
```

## ApplicationException

`ApplicationException` es la excepción base de la aplicación.

Todas las excepciones propias heredan de esta clase y tienen asociado un estado HTTP.

```java
public abstract class ApplicationException
        extends RuntimeException {

    private final HttpStatus status;

    protected ApplicationException(
            HttpStatus status,
            String message) {

        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
```

## Excepciones de dominio

### NotFoundException

Se utiliza cuando un producto no existe o fue eliminado lógicamente.

```java
public class NotFoundException
        extends ApplicationException {

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
```

Ejemplo de uso:

```java
throw new NotFoundException("Product not found");
```

### ConflictException

Se utiliza cuando existe un conflicto con información ya almacenada.

En el módulo de productos se utiliza para impedir nombres duplicados.

```java
public class ConflictException
        extends ApplicationException {

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
```

Ejemplo:

```java
throw new ConflictException(
        "Product name already registered");
```

### BadRequestException

Se utiliza cuando una solicitud incumple una regla de negocio.

```java
public class BadRequestException
        extends ApplicationException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
```

## Formato estándar ErrorResponse

Todos los errores de la API utilizan la misma estructura:

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> details;
}
```

Los campos utilizados son:

| Campo       | Descripción                       |
| ----------- | --------------------------------- |
| `timestamp` | Fecha y hora del error            |
| `status`    | Código HTTP                       |
| `error`     | Nombre del error HTTP             |
| `message`   | Mensaje general                   |
| `path`      | Endpoint donde ocurrió el error   |
| `details`   | Errores específicos de validación |

El campo `details` solo aparece cuando existen errores específicos en los campos del DTO.

## GlobalExceptionHandler

El manejador global utiliza:

```java
@RestControllerAdvice
```

Esta anotación permite capturar excepciones producidas desde cualquier controlador o servicio.

### Manejo de excepciones de la aplicación

```java
@ExceptionHandler(ApplicationException.class)
public ResponseEntity<ErrorResponse>
        handleApplicationException(
                ApplicationException exception,
                HttpServletRequest request) {

    ErrorResponse response = new ErrorResponse(
            exception.getStatus(),
            exception.getMessage(),
            request.getRequestURI()
    );

    return ResponseEntity
            .status(exception.getStatus())
            .body(response);
}
```

Este método captura:

```text
NotFoundException
ConflictException
BadRequestException
```

### Manejo de errores de validación

```java
@ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse>
        handleValidationException(
                MethodArgumentNotValidException exception,
                HttpServletRequest request) {

    Map<String, String> errors =
            new LinkedHashMap<>();

    exception.getBindingResult()
            .getFieldErrors()
            .forEach(error ->
                    errors.put(
                            error.getField(),
                            error.getDefaultMessage()
                    )
            );

    ErrorResponse response = new ErrorResponse(
            HttpStatus.BAD_REQUEST,
            "Datos de entrada inválidos",
            request.getRequestURI(),
            errors
    );

    return ResponseEntity
            .badRequest()
            .body(response);
}
```

Este método captura los errores generados por:

```java
@Valid @RequestBody
```

### Manejo de errores inesperados

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse>
        handleUnexpectedException(
                Exception exception,
                HttpServletRequest request) {

    ErrorResponse response = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Error interno del servidor",
            request.getRequestURI()
    );

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
}
```

Este método evita que el cliente reciba stack traces o información técnica interna.

## Validación de nombres duplicados

En `ProductRepository` se agregó:

```java
Optional<ProductEntity> findByName(String name);
```

Antes de crear un producto, el servicio verifica si ya existe otro producto activo con el mismo nombre:

```java
Optional<ProductEntity> existingProduct =
        productRepository.findByName(
                dto.getName());

if (existingProduct.isPresent()
        && !existingProduct.get().isDeleted()) {

    throw new ConflictException(
            "Product name already registered");
}
```

## Validación de productos inexistentes o eliminados

Para buscar un producto activo se utiliza:

```java
private ProductEntity findActiveEntity(Long id) {

    ProductEntity entity =
            productRepository.findById(id)
                    .orElseThrow(() ->
                            new NotFoundException(
                                    "Product not found"));

    if (entity.isDeleted()) {
        throw new NotFoundException(
                "Product not found");
    }

    return entity;
}
```

Este método es utilizado por:

```text
findOne()
update()
partialUpdate()
```

De esta forma, un producto inexistente o eliminado responde con:

```text
404 Not Found
```

## Flujo del manejo global de errores

```text
Postman
   ↓
ProductsController
   ↓
ProductServiceImpl
   ↓
Lanza una excepción de dominio
   ↓
GlobalExceptionHandler
   ↓
ErrorResponse
   ↓
Respuesta HTTP uniforme
```

En los errores de validación, el flujo es:

```text
Postman
   ↓
ProductsController
   ↓
@Valid
   ↓
MethodArgumentNotValidException
   ↓
GlobalExceptionHandler
   ↓
ErrorResponse con details
   ↓
400 Bad Request
```

# Evidencias

## Evidencia 1: Producto inexistente

Se realizó la petición:

```http
GET /api/products/999
```

El producto solicitado no existe en PostgreSQL.

La API respondió con:

```text
404 Not Found
```

y con un formato uniforme parecido a:

```json
{
  "timestamp": "2026-06-25T05:09:04",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found",
  "path": "/api/products/999"
}
```

![Producto inexistente](img/producto-inexistente.png)

Esta evidencia demuestra que `NotFoundException` es capturada correctamente por `GlobalExceptionHandler`.

## Evidencia 2: Producto con nombre duplicado

Primero se creó correctamente un producto mediante:

```http
POST /api/products
```

Después se intentó crear otro producto activo utilizando exactamente el mismo nombre.

La API respondió con:

```text
409 Conflict
```

y con el mensaje:

```text
Product name already registered
```

![Producto duplicado](img/producto-duplicado.png)

Esta evidencia demuestra que `ConflictException` evita registrar dos productos activos con el mismo nombre.

## Evidencia 3: Validación de DTO

Se realizó una petición:

```http
POST /api/products
```

con el siguiente cuerpo inválido:

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

La respuesta incluye el campo `details`, con los errores correspondientes a cada atributo:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Datos de entrada inválidos",
  "path": "/api/products",
  "details": {
    "name": "El nombre es obligatorio",
    "price": "El precio debe ser mayor o igual a 0",
    "stock": "El stock debe ser mayor o igual a 0"
  }
}
```

![Validación del DTO](img/validacion-dto.png)

Esta evidencia demuestra que `MethodArgumentNotValidException` es capturada por el manejador global.

## Evidencia 4: Consulta de producto eliminado

Primero se eliminó lógicamente un producto mediante:

```http
DELETE /api/products/{id}
```

Después se intentó consultar nuevamente el mismo producto:

```http
GET /api/products/{id}
```

La API respondió con:

```text
404 Not Found
```

![Producto eliminado](img/producto-eliminado.png)

Aunque el registro todavía existe en PostgreSQL, el campo:

```text
deleted = true
```

hace que el producto ya no esté disponible para consultas o actualizaciones.

## Resultados obtenidos

Las pruebas realizadas permitieron comprobar que:

* Un producto inexistente responde con `404 Not Found`.
* Un producto eliminado lógicamente responde con `404 Not Found`.
* Un nombre de producto duplicado responde con `409 Conflict`.
* Los datos inválidos responden con `400 Bad Request`.
* Los errores de validación incluyen el campo `details`.
* Todos los errores mantienen el mismo formato.
* No se muestran stack traces al cliente.
* Los controladores no utilizan bloques `try/catch`.
* Los servicios no construyen respuestas HTTP manualmente.
* Las excepciones expresan claramente el error ocurrido.

## Comparación antes y después

### Antes

```text
ProductServiceImpl
   ↓
IllegalStateException
   ↓
500 Internal Server Error
```

### Ahora

```text
ProductServiceImpl
   ↓
NotFoundException / ConflictException
   ↓
GlobalExceptionHandler
   ↓
ErrorResponse
   ↓
404 / 409
```

## Conclusión

La implementación de un manejador global de excepciones permite centralizar y estandarizar todos los errores producidos por la API.

Las excepciones de dominio representan claramente el problema ocurrido, mientras que `GlobalExceptionHandler` se encarga de convertirlas en respuestas HTTP apropiadas.

La aplicación ahora devuelve códigos correctos como `400`, `404` y `409`, evita exponer información técnica y proporciona respuestas útiles para clientes frontend.
