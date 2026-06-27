# E-commerce Microservices Implementation Plan

Date: 2026-06-27

Design source: `docs/superpowers/specs/2026-06-27-ecommerce-microservices-design.md`

## Delivery Strategy

Build and verify one vertical path at a time. Finish the local system before GitHub, SonarCloud, Docker Hub, or Azure. Keep every commit runnable and avoid unrequested features.

## Phase 0: Local Toolchain

1. Enable Docker Desktop integration for the active WSL distribution.
2. Install Java 21 and Maven in WSL.
3. Verify `java -version`, `mvn -version`, `docker version`, and `docker compose version`.
4. Create sibling directories for Order and Notification Services.
5. Initialize each service as an independent Git repository on `main`.

Checkpoint: Java, Maven, and Docker commands work from the project folder.

## Phase 1: Product Service

Create the Maven project and conventional `entity`, `dto`, `repository`, `service`, `controller`, and `exception` packages. Implement only:

- `POST /products`
- `GET /products/{id}`
- `DELETE /products/{id}`

Use `BigDecimal`, Bean Validation, constructor injection, JPA, and environment-variable database configuration.

Create `ProductServiceTest` and `ProductControllerTest`. Test create, get, delete, missing product, successful responses, validation, and `404` handling.

Verification:

```bash
mvn test
mvn verify
```

Commit: `feat: implement product service API`

## Phase 2: Order Service

Create the Order Service with these responsibilities:

- accept `POST /orders`;
- call Product Service through Spring `RestClient`;
- calculate `totalPrice` using `BigDecimal`;
- store the order in `order_db`;
- publish `OrderCreatedEvent` to `order.notifications`.

Use conventional packages plus `client`, `messaging`, and small REST/Rabbit configuration classes.

Test correct product lookup and total calculation, persistence, publication, invalid quantity, missing product, Product Service failure, controller success, validation, and mapped errors.

Verification:

```bash
mvn test
mvn verify
```

Commit: `feat: implement order creation workflow`

## Phase 3: Notification Service

Create a consumer-only Spring Boot project with Rabbit configuration, `OrderCreatedEvent`, and `OrderNotificationListener`. The listener logs a mock notification with `customerId` and `orderId`. It has no database, controller, or HTTP server.

Create `OrderNotificationListenerTest` and invoke the listener with a sample event.

Verification:

```bash
mvn test
mvn verify
```

Commit: `feat: consume and log order notifications`

## Phase 4: Docker and Local Integration

Add a multi-stage Dockerfile, `.dockerignore`, Maven Wrapper, and README to each service.

Add `product-service/compose.yaml` with two PostgreSQL containers, RabbitMQ management, the three application builds using sibling paths, health checks, persistent volumes, and environment variables.

Run:

```bash
docker compose up --build -d
docker compose ps
```

Smoke-test product creation, product retrieval, order creation, Notification Service logs, and RabbitMQ management UI.

Commit in each repository: `build: add Docker image configuration`

Product follow-up commit: `build: add local Docker Compose stack`

## Phase 5: Documentation and Test Assets

Verify Product Swagger UI on port 8081 and Order Swagger UI on port 8082.

Create in Product Service:

- `postman/ecommerce-microservices.postman_collection.json`
- `postman/local.postman_environment.json`
- `jmeter/product-service-load-test.jmx`

The Postman collection captures `productId` and performs the required workflow. The JMeter plan uses 20 users, a 10-second ramp-up, and 5 loops.

Run all three Maven suites, Postman, and JMeter against Compose. Capture evidence immediately.

Commit: `test: add API and performance test assets`

## Phase 6: GitHub Organization and Repositories

User UI actions:

1. create a GitHub Free Organization;
2. create public `product-service`, `order-service`, and `notification-service` repositories;
3. add local repositories as remotes and push `main`;
4. invite group members when appropriate.

No parent repository is created.

## Phase 7: Order CI and SonarCloud

Add `order-service/.github/workflows/ci.yml`, triggered by pull requests to `main`, with visible steps to run tests, build Maven, and run SonarCloud analysis.

User UI actions:

1. sign in to SonarCloud through GitHub;
2. import only Order Service;
3. add GitHub secret `SONAR_TOKEN`;
4. add variables `SONAR_ORGANIZATION` and `SONAR_PROJECT_KEY`.

Validate with a genuine feature branch and PR. Capture the successful workflow and SonarCloud dashboard before merging.

Commit: `ci: test build and analyze pull requests`

## Phase 8: Docker Hub

Create a public Product Service repository in Docker Hub. Build, tag, push, pull, and run the image with database environment variables. Capture the public image and successful run.

## Phase 9: Azure

Only after all earlier phases pass:

1. activate Azure for Students;
2. create Azure Database for PostgreSQL and `product_db`;
3. create App Service for Containers using the public image;
4. set database environment variables;
5. verify startup logs;
6. create and retrieve a product through the cloud URL;
7. capture evidence;
8. stop or delete billable resources after grading when safe.

## Phase 10: Evidence and Final Rehearsal

Create one concise report in assignment order with captions explaining what each screenshot proves.

Final checks:

```bash
mvn test
docker compose down -v
docker compose up --build -d
docker compose ps
```

Repeat the Postman workflow from a clean database, confirm notification logs, verify repository links, and export the report to PDF.

## Immediate Execution Order

1. Toolchain setup.
2. Product implementation and tests.
3. Order implementation and tests.
4. Notification implementation and tests.
5. Docker integration and local smoke test.
6. Test assets and documentation.
7. External UI integrations in the order above.
