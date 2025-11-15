# Alojamiento API – Backend

Módulo base (Commit 1): inicialización del proyecto Spring Boot con capas y dependencias mínimas estables.

## Estructura de capas
- `config/`: configuración de la aplicación.
- `domain/`: lógica de negocio, DTOs, mappers (incluye MapStruct ya presente).
- `persistence/`: entidades JPA y repositorios.
- `security/`: configuración y clases de seguridad (se completará en commits posteriores).
- `web/`: controladores REST. Incluye `web/health/HealthController` para un chequeo simple.

## Perfiles y properties
- `src/main/resources/application.properties`: configuración común (Hibernate, Hikari, logs).
- Perfiles: `dev`, `test`, `prod` con sus respectivos `application-<profile>.properties`.
- Variables requeridas por perfil (ejemplos):
  - `DB_DEV_URL`, `DB_DEV_USER`, `DB_DEV_PASSWORD`
  - `DB_TEST_URL`, `DB_TEST_USER`, `DB_TEST_PASSWORD`
  - `DB_PROD_URL`, `DB_PROD_USER`, `DB_PROD_PASSWORD`

> Nota: Para este commit 1 no se incluye Flyway ni JWT. Spring Security está agregado pero se configurará más adelante (Commit 4). Por defecto, Spring Security protege los endpoints.

## Dependencias clave (mínimas)
- Spring Boot starters: `web`, `data-jpa`, `security`, `validation`.
- Base de datos: `postgresql` (scope runtime).
- Utilidades: `lombok`, `mapstruct` (+ processor).
- Documentación/anotaciones: `springdoc-openapi-starter-webmvc-ui` (requerido por anotaciones en controladores ya presentes).
- Test: `spring-boot-starter-test`, `spring-security-test`.

## Build y ejecución

Requisitos: Java 21 y Maven Wrapper incluido.

Compilar sin tests:

```
cmd /c "mvnw.cmd -DskipTests clean package"
```

Ejecutar con perfil `dev`:

```
cmd /c "set SPRING_PROFILES_ACTIVE=dev && mvnw.cmd spring-boot:run"
```

Chequeo rápido de salud (si el endpoint no está protegido por seguridad):
- GET http://localhost:8080/api/health

## Git: rama y commit de este módulo

```
cmd /c "cd /d C:\\Users\\Jean-Pierre\\Documents\\Proyecto Final Pr-Avanzada\\GestionAlojamientoJJA_Backend\\alojamientosAPI && git checkout -b feature/users-auth && git add pom.xml src\\main\\java\\com\\uniquindio\\alojamientosAPI\\web\\health\\HealthController.java README.md && git commit -m \"feat(config): initialize spring boot project with basic layers\" && git push -u origin feature/users-auth"
```

## Notas de calidad/seguridad
- Se detecta un aviso de vulnerabilidad transitoria en `logback-core` reportado por herramientas SCA externas; queda supervisado y no bloquea build.
- Tests de integración pueden requerir base de datos y variables de entorno; para commit 1 se permite `-DskipTests`.

## Próximos pasos
- Commit 2: entidades `User`, `Role` y relación.
- Commit 3: repositorios JPA.
- Commit 4: configuración de seguridad + JWT.
- Commit 5: servicio de registro y asignación de roles.
- Commit 6: pruebas de autenticación y autorización.

