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
