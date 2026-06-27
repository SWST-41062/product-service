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

Use the RabbitMQ username and password from `.env`.

Stop the system with `docker compose down`. Add `-v` only when you intentionally want to delete local database data.

## Postman

Import both files from the `postman` folder, select the **E-commerce Local** environment, and run the collection in order. It creates a product, gets it, creates an order, and deletes the product.

The same collection can be verified from the terminal:

```bash
npx --yes newman run postman/ecommerce-microservices.postman_collection.json \
  -e postman/local.postman_environment.json
```

## JMeter

Open the ready-made test plan in the JMeter UI:

```bash
jmeter jmeter/product-service-load-test.jmx
```

The plan calls `GET /products/1` with 20 users, a 10-second ramp-up, and 5 loops (100 requests). It includes **View Results Tree** and **Summary Report** listeners.

## Test

```bash
./mvnw verify
```

JaCoCo report: `target/site/jacoco/index.html`
