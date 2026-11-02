# Order Reservation Platform

A Java/Spring Boot backend project for experimenting with production-oriented application architecture and, later, distributed systems and microservices.

## Current Version

The first version currently provides:

- PostgreSQL persistence
- Flyway database migrations
- Spring Data JPA
- `Order` entity and repository
- Order service and REST controller
- `GET /orders` endpoint

The application currently runs as a single Spring Boot application.

## Technologies

- Java 21
- Spring Boot
- Spring Data JPA / Hibernate
- PostgreSQL 18
- Flyway
- Maven
- Docker / Docker Compose

## Running the Application

### Requirements

Install:

- Java 21
- Docker
- Git

The project includes the Maven Wrapper, so a separate Maven installation is not required.

### 1. Start PostgreSQL

From the project root:

```bash
docker compose up -d
```

Verify that the PostgreSQL container is running:

```bash
docker ps
```

The database is exposed locally on port `5433`.

### 2. Start the Spring Boot Application

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Alternatively, run `OrderReservationPlatformApplication` directly from IntelliJ IDEA.

On startup, Flyway automatically applies the database migrations.

### 3. Test the API

Get all orders:

```http
GET http://localhost:8080/orders
```

Example response:

```json
[
  {
    "orderId": 1,
    "status": "PENDING",
    "createdAt": "2026-11-01T20:00:00",
    "updatedAt": "2026-11-01T20:00:00"
  }
]
```
