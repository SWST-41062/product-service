# E-commerce Microservices Assignment Design

Date: 2026-06-27

Course: SWST 41062

Execution constraint: one available developer and three calendar days

## Objective

Build the smallest system that satisfies every stated assignment requirement: three Spring Boot microservices, independent PostgreSQL databases, RabbitMQ messaging, automated and manual tests, Order Service CI and SonarCloud analysis, a public Product Service Docker image, and an Azure deployment of Product Service.

The first milestone is a fully working local system. External services and cloud deployment follow only after the local order flow is reliable.

## Scope Decisions

- Use exactly three public GitHub repositories under one free GitHub Organization.
- Keep the repositories as sibling folders under one non-Git parent folder.
- Fully Dockerize all three applications and run the local system with Docker Compose.
- Push only Product Service to Docker Hub because it is the only image the assignment requires publicly.
- Configure GitHub Actions and SonarCloud only for Order Service because that is the stated requirement.
- Deploy only Product Service and its database to Azure.
- Use Swagger UI and existing tool dashboards; do not build a custom frontend.
- Produce one combined Word/PDF evidence report containing validation screenshots.

The system will not include authentication, an API gateway, service discovery, distributed tracing, Kubernetes, a frontend framework, an outbox, or other unrequested infrastructure.

## Repository and Local Folder Layout

```text
Assignment/
└── ecommerce-backend/                 # not a Git repository
    ├── product-service/                # Git repository 1
    ├── order-service/                  # Git repository 2
    └── notification-service/           # Git repository 3
```

Each service repository contains its source, tests, Maven Wrapper, Dockerfile, `.gitignore`, `.dockerignore`, and README. The Product Service repository also contains:

- `compose.yaml`, which builds all three sibling repositories and starts the complete local system;
- the shared Postman collection;
- the Product Service JMeter plan; and
- this shared architecture specification.

Running Compose from `product-service` therefore assumes the three repositories retain the sibling layout shown above.

## Technology Choices

- Java 21
- Spring Boot 3.5.16 across all services
- Maven and Maven Wrapper
- Spring Web for Product and Order REST APIs
- Spring Data JPA and Hibernate
- PostgreSQL containers for `product_db` and `order_db`
- Spring AMQP and RabbitMQ
- Bean Validation
- Springdoc OpenAPI for Swagger UI
- JUnit 5, Mockito, MockMvc, and JaCoCo
- Docker multi-stage builds and Docker Compose

Types and abstractions stay conventional. Lombok, MapStruct, Spring Cloud, Testcontainers, and custom frameworks are excluded.

## System Architecture

### Product Service

Product Service owns product data and `product_db`. It exposes:

- `POST /products` — create a product and return `201 Created`;
- `GET /products/{id}` — return one product or `404 Not Found`;
- `DELETE /products/{id}` — delete one product and return `204 No Content`.

The `product` table contains:

- `product_id` — generated primary key;
- `name` — required text;
- `unit_price` — required positive decimal.

Money values use `BigDecimal`, not floating-point types.

### Order Service

Order Service owns order data and `order_db`. It exposes only the required endpoint:

- `POST /orders` — create an order and return `201 Created`.

The request contains `customerId`, `productId`, and `quantity`. The service calls `GET /products/{id}`, retrieves the authoritative product name and unit price, calculates `totalPrice = unitPrice × quantity`, stores the order, and publishes the notification event.

The quoted PostgreSQL table `"order"` contains:

- `order_id` — generated primary key;
- `customer_id`;
- `product_id`;
- `product_name`;
- `quantity`;
- `total_price`.

The Product Service base URL is supplied through `PRODUCT_SERVICE_URL`. Inside Compose it is `http://product-service:8081`.

### Notification Service

Notification Service has no REST API and no database. It listens to RabbitMQ, receives an event containing `customerId` and `orderId`, and writes a clear mock-notification log entry. It is a consumer-only Spring Boot process and does not expose an HTTP port.

### RabbitMQ

One durable queue named `order.notifications` is sufficient. Order Service publishes a JSON event directly to that queue through RabbitMQ's default exchange. Notification Service consumes the same JSON type. No custom exchange topology is needed for the assignment.

## Local Container Topology

`compose.yaml` starts these containers on one private Compose network:

- Product Service on host port `8081`;
- Order Service on host port `8082`;
- Notification Service with no exposed port;
- Product PostgreSQL on host port `5433` and container port `5432`;
- Order PostgreSQL on host port `5434` and container port `5432`;
- RabbitMQ on ports `5672` and management UI port `15672`.

Application containers use Docker service names rather than `localhost` to reach databases, RabbitMQ, and Product Service. Health checks and Compose dependency conditions prevent applications from starting before their infrastructure is ready.

All credentials and connection values come from environment variables. Local-only demonstration defaults may live in Compose; no real cloud credentials or access tokens are committed.

## Layers and Responsibilities

Product and Order Services use conventional packages:

- `controller` — HTTP mapping, validation, and response status only;
- `service` — business workflow;
- `repository` — Spring Data JPA persistence;
- `entity` — persisted data;
- `dto` — request, response, and message contracts;
- `exception` — domain exceptions and global HTTP handling;
- `config` — small infrastructure configuration.

Order Service additionally has a Product REST client and an order-event publisher. Notification Service contains its RabbitMQ configuration, event DTO, and listener. Constructor injection keeps dependencies explicit and testable.

These boundaries satisfy the layered architecture, SOLID, repository, dependency-injection, and publisher-consumer requirements without decorative interfaces or premature abstraction.

## Validation and Error Handling

- Product name and customer ID must not be blank.
- Unit price and quantity must be greater than zero.
- Missing products produce `404 Not Found`.
- Invalid request bodies produce `400 Bad Request`.
- Product Service or RabbitMQ availability failures produce `503 Service Unavailable` from Order Service.
- Unexpected server failures produce `500 Internal Server Error`.

Product and Order Services return the same compact JSON error structure: timestamp, status, error, message, and path. Logs include useful context but never database passwords, tokens, or other secrets.

Order creation runs in a database transaction and throws on a publication failure so the database transaction can roll back. Full atomic coordination between PostgreSQL and RabbitMQ is intentionally out of scope; the assignment does not justify an outbox or distributed transaction.

## Testing Design

### Automated tests

JUnit 5 and Mockito cover every service method. MockMvc controller tests cover all Product and Order endpoints, validation, success responses, and important error responses. Notification Service tests invoke the listener with a sample event and verify successful handling.

Minimum high-value Order Service cases are:

- valid order and correct price calculation;
- requested product is missing;
- invalid quantity;
- Product Service call fails;
- order is saved and the expected event is published.

JaCoCo generates coverage reports for all three repositories. SonarCloud consumes the Order Service JaCoCo report. Repository interfaces themselves require no artificial unit tests.

### Postman

One collection contains environment variables for Product and Order base URLs and requests for:

1. create product;
2. get product;
3. create order;
4. optional delete product.

Collection scripts retain the created product ID. The operator verifies the RabbitMQ management UI and Notification Service logs after creating the order.

### JMeter

The committed plan targets `GET /products/{id}` with:

- 20 virtual users;
- 10-second ramp-up;
- 5 loops per user;
- 100 total requests.

A product is created before the run. The evidence report includes the View Results Tree and a concise result interpretation.

## Git and Collaboration Design

The GitHub Organization uses the free plan and contains three public repositories matching the folder names. Each repository uses `main` plus short-lived feature branches and meaningful commit messages.

The currently available developer creates the working system honestly under their own identity. Unavailable group members are not impersonated and no contribution history is fabricated. If they return, they may make genuine review, documentation, or correction commits through their own branches.

## Order Service CI and SonarCloud

The Order Service workflow triggers for pull requests targeting `main`, including new commits pushed to an existing PR. It uses Java 21 and runs visible steps in this order:

1. run unit tests;
2. build the Maven project;
3. execute SonarCloud analysis.

Any failing command fails the workflow. The Sonar token is stored as a GitHub Actions secret and never written to the repository. Validation uses a real feature branch and PR; the PR is merged only after the checks succeed. Screenshots capture the PR, successful workflow, and SonarCloud dashboard.

## Docker Hub and Azure

Product Service uses a multi-stage Dockerfile and is pushed to a public Docker Hub repository with a version tag and `latest` tag. A clean `docker run` validation proves that the public image starts when the required database environment variables are provided.

Azure is attempted only after all local, CI, SonarCloud, JMeter, and Docker Hub work is complete. The cloud design is:

- Azure Database for PostgreSQL hosts Product Service data;
- Azure App Service for Containers pulls the public Product image;
- App Service environment settings provide the JDBC URL, username, and password;
- Postman creates and retrieves a product through the public cloud URL.

The preferred subscription route is Azure for Students through a group member's eligible university account. If account activation is externally unavailable, no Azure success evidence will be fabricated; all other completed assignment parts remain valid.

## Evidence Report

One combined Word document, exported to PDF for submission, contains short captions and screenshots in assignment order:

1. local containers and service startup;
2. Swagger API documentation;
3. Product and Order Postman responses;
4. RabbitMQ queue and Notification Service log;
5. JUnit and coverage results;
6. JMeter configuration, View Results Tree, and interpretation;
7. GitHub PR and successful Actions run;
8. SonarCloud analysis;
9. public Docker Hub image and local run;
10. Azure resources and cloud Postman validation.

Screenshots are captured immediately after each milestone to avoid reconstructing evidence at the deadline.

## Solo Three-Day Execution Sequence

### Day 1: working local system

1. Enable Docker Desktop WSL integration and install Java 21 and Maven.
2. Initialize the three local repositories.
3. Implement Product Service, then Order Service, then Notification Service.
4. Add all Dockerfiles and Compose.
5. Prove the complete create-product-to-notification flow.

### Day 2: testing and external quality services

1. Complete JUnit, MockMvc, and JaCoCo coverage.
2. Complete Swagger, Postman, and JMeter validation.
3. Create the GitHub Organization and push the repositories.
4. Validate an Order Service PR through GitHub Actions and SonarCloud.
5. Publish and validate Product Service on Docker Hub.

### Day 3: cloud and submission

1. Activate Azure for Students and create PostgreSQL and App Service resources.
2. Deploy and validate Product Service.
3. Complete the combined evidence report.
4. Perform a clean-clone, one-command Compose rehearsal.

## Acceptance Criteria

The local design is complete when `docker compose up --build` starts the system and a user can create a product, create an order for it, observe the persisted order, and see the corresponding notification log. All automated tests pass, Swagger is usable, environment variables hold credentials, and each service remains independently versioned.

The full assignment is complete when the required JMeter, GitHub Actions, SonarCloud, Docker Hub, and Azure validations are also captured in the evidence report.
