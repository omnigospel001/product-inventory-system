# Product Inventory System

A Spring Boot REST API for managing product inventory, orders, and user authentication. Track products by category, monitor stock levels through inventory transactions, and manage customer orders with role-based access control.

## Stack

- **Language:** Java 17
- **Framework:** Spring Boot 4.1.0
- **Database:** PostgreSQL 15
- **Cache:** Redis 7
- **Notable libraries:** Spring Data JPA, Spring Security, JWT (JJWT), Flyway migrations, Springdoc OpenAPI, Micrometer/Zipkin tracing

## How it's organized

```
src/main/java/com/product/inventory/sysetem/
  config/           Security and application configuration
  controller/       REST endpoints (Auth, Product, User, InventoryTransaction)
  dto/              Request/response data transfer objects
  entity/           JPA entities (Product, Category, Order, User, Review, etc.)
  exception/        Custom exception handling
  repository/       Spring Data JPA repositories
  security/         JWT and authentication logic
  service/          Business logic and transactional operations

src/main/resources/
  db/migration/     Flyway database migration scripts
  application.properties  Database, Redis, Zipkin configuration
```

**How it fits together:** HTTP requests hit the controllers, which delegate to services for business logic. Services interact with repositories to persist data to PostgreSQL via Hibernate. Redis caches frequently accessed data. Flyway manages schema migrations on startup. Spring Security with JWT tokens protects endpoints based on user roles.

## How to run it

Start the full stack with Docker Compose:

```bash
docker-compose up
```

This brings up PostgreSQL, Redis, pgAdmin, Redis Insight, and Zipkin.

Then run the Spring Boot application:

```bash
./mvnw spring-boot:run
```

Or build and run as a JAR:

```bash
./mvnw clean package
java -jar target/product-inventory-system-0.0.1-SNAPSHOT.jar
```

The API starts on `http://localhost:8080`. View the OpenAPI docs at `/swagger-ui.html`.

**Required configuration:** Update `application.properties` with your PostgreSQL and Redis connection details if running outside Docker.
