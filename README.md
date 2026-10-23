# Sistema de Gestión de Transferencias Interbancarias

Proyecto frontend desarrollado para la evaluación técnica Full Stack Mid-Senior utilizando **Angular 14**, **Bootstrap 5** y documentación interactiva con **OpenAPI / Swagger**.

FRONTEND ESTA DENTRO DE LA CARPETA BACKEND con el NOMBRE FRONTEND

Proyecto backend desarrollado para la evaluación técnica Full Stack Mid-Senior utilizando **Java 17**, **Quarkus 3**, **Hibernate ORM con Panache**, **MySQL** y documentación interactiva con **OpenAPI / Swagger**.

---

## 🏛️ Arquitectura y Buenas Prácticas

El proyecto implementa una arquitectura en capas basada en **Código Limpio (Clean Code)** y principios **SOLID**:

```
com.evaluacion.gabrielborjamoscoso/
├── resource/            # Capa de presentación (JAX-RS / REST Controllers)
│   ├── CuentaResource.java
│   └── TransferenciaResource.java
├── service/             # Capa de lógica de negocio (Interfaces e Implementaciones)
│   ├── CuentaService.java & impl/CuentaServiceImpl.java
│   ├── TransferenciaService.java & impl/TransferenciaServiceImpl.java
│   ├── DeteccionSospechosaService.java & impl/DeteccionSospechosaServiceImpl.java
│   └── LimiteDiarioService.java & impl/LimiteDiarioServiceImpl.java
├── repository/          # Capa de acceso a datos (PanacheRepository)
│   ├── CuentaRepository.java
│   └── TransferenciaRepository.java
├── entity/              # Entidades JPA persistentes
│   ├── Cuenta.java
│   ├── Transferencia.java
│   ├── TipoCuenta.java
│   ├── EstadoCuenta.java
│   └── EstadoTransferencia.java
├── dto/                 # Data Transfer Objects (Requests y Responses)
│   ├── request/
│   │   ├── CuentaRequestDto.java
│   │   └── TransferenciaRequestDto.java
│   └── response/
│       ├── CuentaResponseDto.java
│       ├── CuentaResumenDto.java
│       ├── TransferenciaResponseDto.java
│       ├── PageResponseDto.java
│       └── ErrorResponseDto.java
├── mapper/              # Mapeadores automáticos con MapStruct
│   ├── CuentaMapper.java
│   └── TransferenciaMapper.java
└── exception/           # Manejo centralizado de excepciones
    ├── BusinessException.java
    ├── ResourceNotFoundException.java
    └── GlobalExceptionMapper.java
```

---

## 📋 Reglas de Negocio Implementadas

1. **Gestión de Cuentas (CA-01):**
   - Número de cuenta único de 10 dígitos numéricos.
   - Validación de no duplicados y saldo inicial no negativo ($\ge 0$).
   - Tipos de cuenta: `AHORRO` o `CORRIENTE`. Estados: `ACTIVA` o `INACTIVA`.
2. **Transferencias Atómicas (CA-02):**
   - Transacción atómica garantizada con `@Transactional`.
   - Validación de existencia y estado activo de cuentas origen y destino.
   - Validación de cuenta origen $\ne$ destino y monto $> 0$.
   - Validación de saldo suficiente en cuenta origen.
3. **Detección Automática de Operaciones Sospechosas (CA-03):**
   - Se marca automáticamente con estado `REQUIERE_REVISION` y `sospechosa = true` si:
     - El monto individual supera **\$5,000.00**.
     - La cuenta origen acumula **más de 5 transferencias** en la última hora.
4. **Control de Límite Diario (CA-04):**
   - Máximo de **\$10,000.00 acumulados** transferidos por cuenta en un día calendario.
   - Si se supera, la transacción es rechazada indicando el **monto restante disponible** para el día.
5. **Historial y Resumen en Tiempo Real (CA-05):**
   - Historial de transferencias con filtros dinámicos (cuenta origen, destino, rango de fechas y estado) ordenado por fecha descendente.
   - Resumen de cuenta con saldo actual, total transferido hoy, transferencias del día y cantidad sospechosas.
6. **Extras de Nivel Senior Implementados:**
   - ✅ **Manejo Centralizado de Excepciones:** `GlobalExceptionMapper` con códigos HTTP estándar (201, 400, 404, 422).
   - ✅ **DTOs con MapStruct:** Desacoplamiento total entre capa de persistencia y capa REST.
   - ✅ **OpenAPI / Swagger UI:** Totalmente anotado con esquemas y ejemplos.
   - ✅ **Paginación:** Soportada en listados de cuentas y transferencias.
   - ✅ **Logs Estructurados:** Trazabilidad en auditoría de operaciones críticas con JBoss Logging.
   - ✅ **Configuración CORS:** Habilitado para consumo desde frontend Angular (`http://localhost:4200`).
   - ✅ **Pruebas Unitarias y de Integración:** 12 pruebas automatizadas con JUnit 5, Mockito y `@QuarkusTest` con RestAssured.
   - ✅ **Docker y Docker Compose:** Orquestación completa con `docker-compose.yml`.

---

## 🗄️ Configuración de la Base de Datos MySQL

1. Asegúrate de tener el servicio MySQL iniciado en el puerto `3306`.
2. La base de datos se crea automáticamente si no existe gracias a la configuración de Quarkus y JDBC, o puedes crearla manualmente con:
```sql
CREATE DATABASE IF NOT EXISTS transferencias_db;
```
3. Credenciales configuradas por defecto en `src/main/resources/application.properties`:
   - **Host:** `localhost:3306`
   - **Base de datos:** `transferencias_db`
   - **Usuario:** `root`
   - **Contraseña:** `2468`

---

## 🚀 Compilación y Ejecución

### 1. Ejecutar las Pruebas Automatizadas
```bash
# En Windows (PowerShell / CMD)
.\mvnw.cmd test

# En Linux / Mac
./mvnw test
```

### 2. Ejecutar en Modo Desarrollo (Hot Reload)

#### Backend (Quarkus):
```bash
.\mvnw.cmd quarkus:dev
```
- **API Base:** `http://localhost:8080/api`
- **Swagger UI:** `http://localhost:8080/swagger-ui/`
- **Especificación OpenAPI (JSON):** `http://localhost:8080/openapi`

#### Frontend (Angular):
```bash
cd frontend
npm start
```
- **Aplicación Web:** `http://localhost:4200`

### 3. Empaquetar y Ejecutar en Producción
```bash
.\mvnw.cmd clean package
java -jar target/quarkus-app/quarkus-run.jar
```

### 4. Ejecución con Docker Compose
```bash
docker-compose up --build -d
```

---

## 📡 Catálogo de Endpoints REST

### 1. Cuentas Bancarias
- `POST /api/cuentas`: Registra una nueva cuenta bancaria (`HTTP 201 Created`).
  ```json
  {
    "numeroCuenta": "1020304050",
    "titular": "Gabriel Borja",
    "tipoCuenta": "AHORRO",
    "saldoInicial": 2500.00,
    "estado": "ACTIVA"
  }
  ```
- `GET /api/cuentas`: Lista todas las cuentas registradas. Permite paginación con query params opcionales `?pagina=0&tamano=10`.
- `GET /api/cuentas/{id}/resumen`: Retorna el balance y la actividad operativa en tiempo real para una cuenta.

### 2. Transferencias Interbancarias
- `POST /api/transferencias`: Ejecuta una transferencia atómica entre cuentas (`HTTP 201 Created`).
  ```json
  {
    "numeroCuentaOrigen": "1020304050",
    "numeroCuentaDestino": "9080706050",
    "monto": 450.00
  }
  ```
- `GET /api/transferencias`: Consulta el historial de transferencias ordenadas por fecha descendente con filtros opcionales:
  - `cuentaOrigen`: Filtrar por número de cuenta origen.
  - `cuentaDestino`: Filtrar por número de cuenta destino.
  - `fechaInicio`: Fecha inicial en formato ISO (`2026-10-21T00:00:00`).
  - `fechaFin`: Fecha final en formato ISO (`2026-10-21T23:59:59`).
  - `estado`: `COMPLETADA`, `RECHAZADA` o `REQUIERE_REVISION`.
  - `pagina` y `tamano`: Parámetros opcionales para paginación.
