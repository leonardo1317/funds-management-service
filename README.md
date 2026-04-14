# Funds Service - Gestión de Fondos y Suscripciones

API RESTful para la gestión de clientes, fondos y suscripciones, desarrollada con **Spring Boot** y **Java 17**.

El servicio modela operaciones financieras reales como:
- creación y cancelación de suscripciones a fondos
- movimientos de dinero (débito y crédito sobre el balance del cliente)
- registro de transacciones para trazabilidad

Incluye además:
- control de **idempotencia** para operaciones `POST`
- ejecución **transaccional** de operaciones críticas
- manejo de **errores de negocio** con registro de transacciones rechazadas
- sistema de **notificaciones desacoplado y asíncrono**
- consultas paginadas optimizadas para historial de transacciones

El diseño sigue una arquitectura **hexagonal (ports & adapters)**, separando claramente dominio, casos de uso e infraestructura.

---

## Obtener el proyecto
```bash
git clone https://github.com/leonardo1317/funds-management-service.git
cd funds-management-service
```

## Cómo ejecutar
## 1. Opción local
### Requisitos
- **Java 17+**
- **Gradle 8+**
- **MongoDB** (ejecutándose localmente)

```bash
mongod --dbpath /your/data/path
export MONGODB_URI=mongodb://localhost:27017/funds_db
./gradlew bootRun
```

## 2. Opción recomendada: Docker
### Requisitos
- **Docker + Docker Compose**
### Levantar aplicación
```bash
docker compose up --build -d
```
### Detener y eliminar contenedores (opcional)
```bash
docker compose down
```
La API se iniciará en: http://localhost:8080

## 3. Opción AWS (CloudFormation + EC2)

```bash
aws --version
aws configure
```
### Crear infraestructura
```bash
aws cloudformation create-stack \
  --stack-name funds-service-stack \
  --template-body file://cloudformation.yaml \
  --capabilities CAPABILITY_NAMED_IAM \
  --region us-east-1
```
### Ver estado del stack
```bash
aws cloudformation describe-stacks \
  --stack-name funds-service-stack
```
### Obtener IP pública
```bash
aws ec2 describe-instances \
  --query "Reservations[*].Instances[*].PublicIpAddress" \
  --output text
```
### Acceder a la aplicación
```bash
http://<EC2_PUBLIC_IP>:8080
```
### Eliminar stack (opcional)
```bash
aws cloudformation delete-stack \
  --stack-name funds-service-stack
```

## Variables de entorno

| Variable                 | Descripción |
|--------------------------|-------------|
| `MONGODB_URI`            | URI de conexión a MongoDB |
---

## Base de datos (MongoDB)

Principales colecciones:

- **clients** – Información de clientes
- **subscriptions** – Suscripciones y su estado
- **transactions** – Movimientos financieros
- **idempotency** – Control de operaciones idempotentes
- **funds** – Fondos disponibles

---

## Documentación de la API

Swagger UI disponible en:  
[http://localhost:8080/docs](http://localhost:8080/docs)

---

## Arquitectura

Se sigue un patrón **Hexagonal / Ports & Adapters**, con separación clara entre:

- **Domain**: lógica de negocio y entidades.
- **Application**: casos de uso y servicios de aplicación.
- **Infrastructure**: implementaciones concretas (Mongo, REST, mappers).

### Diagrama de capas general

![L1](docs/arq_general.png)

### Diagrama de arquitectura lógica

![L1](docs/arq_logica.png)

### Diagrama de secuencia - Idempotencia

![L1](docs/seq_idempotency.png)

### Diagrama de secuencia - Creación de suscripción

![L1](docs/seq_create_sub.png)

### Diagrama de secuencia - Cancelación de suscripción

![L1](docs/seq_cancel_sub.png)

### Diagrama de secuencia - Consultar transacciones

![L1](docs/seq_transactions.png)

### Diagrama de secuencia - Enviar notificación

![L1](docs/seq_notification.png)

---

## Licencia

Proyecto desarrollado con fines educativos.  
**Autor:** Leonardo Romero (leonardo1317)  
**Licencia:** Apache License 2.0