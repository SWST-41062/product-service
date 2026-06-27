# Product Service

Spring Boot service that owns product data and provides the required create, get, and delete APIs.

## API

| Method | Path | Purpose |
| --- | --- | --- |
| POST | `/products` | Create a product |
| GET | `/products/{id}` | Get a product |
| DELETE | `/products/{id}` | Delete a product |

Swagger UI: `http://localhost:18081/swagger-ui.html`

## Run the complete local system

Keep `product-service`, `order-service`, and `notification-service` as sibling folders. Then run from this repository:

```bash
cp .env.example .env
./build-and-run.sh
```

The script builds the three tested JAR files, creates the Docker images, and starts the complete system.

RabbitMQ UI: `http://localhost:15672`

Stop the system with `docker compose down`. Add `-v` only when you intentionally want to delete local database data.

## Test

```bash
./mvnw verify
```

JaCoCo report: `target/site/jacoco/index.html`
