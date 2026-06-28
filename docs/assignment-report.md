# E-commerce Microservices Assignment Report

> Add the group member names and final GitHub/Docker/Azure links before submission. Put all screenshots in this one report; do not create a separate report for each repository.

## 1. System Summary

The system contains three independent Spring Boot repositories:

| Repository | Responsibility | Database/API |
| --- | --- | --- |
| Product Service | Create, retrieve, and delete products | PostgreSQL `product_db`, REST on local port `18081` |
| Order Service | Retrieve product, calculate total, store order, publish event | PostgreSQL `order_db`, REST on local port `18082` |
| Notification Service | Consume order event and log mock notification | RabbitMQ consumer only |

Local infrastructure and all three applications are started from Product Service with `./build-and-run.sh`.

## 2. Repository Links

- Product Service: <https://github.com/SWST-41062/product-service>
- Order Service: <https://github.com/SWST-41062/order-service>
- Notification Service: <https://github.com/SWST-41062/notification-service>

## 3. Local Development Evidence

Add screenshots showing:

1. `docker compose ps` with all six containers running.
2. Product Swagger UI at `http://localhost:18081/swagger-ui.html`.
3. Order Swagger UI at `http://localhost:18082/swagger-ui.html`.
4. Postman Collection Runner with all four tests passing.
5. RabbitMQ Queues page at `http://localhost:15672` showing `order.notifications`.
6. Notification Service log containing `Mock notification sent`.
7. Maven test summaries for all three services and the JaCoCo reports.

Verified locally on 2026-06-27:

- Product Service: 10 tests passed; 89.8% line coverage.
- Order Service: 14 tests passed; 88.0% line coverage.
- Notification Service: 1 listener test passed.
- End-to-end order: quantity `2` × price `75.50` = total `151.00`; RabbitMQ notification received.
- Postman/Newman: 4 requests and 4 assertions passed.

## 4. Performance Testing

Configuration:

- Endpoint: `GET /products/1`
- Virtual users: 20
- Ramp-up: 10 seconds
- Loop count: 5
- Total requests: 100

Verified locally on 2026-06-27: 100 successful samples, 0 errors, 8.08 ms average response time. Add screenshots of **View Results Tree** and **Summary Report** from JMeter.

## 5. GitHub Actions and SonarCloud

- Validation pull request: <https://github.com/SWST-41062/order-service/pull/1>
- Successful post-merge workflow: <https://github.com/SWST-41062/order-service/actions/runs/28300288642>
- SonarCloud dashboard: <https://sonarcloud.io/summary/overall?id=SWST-41062_order-service&branch=main>

Verified results:

- SonarCloud quality gate: Passed.
- Coverage: 85.5%.
- Bugs: 0.
- Vulnerabilities: 0.
- Duplicated lines: 0.0%.
- GitHub protection requires the successful `Test, build, and analyze` check and one approval; administrators can bypass in an emergency.

Add screenshots showing:

1. The Order Service pull request.
2. The successful GitHub Actions test/build/SonarCloud checks.
3. The SonarCloud project dashboard and quality gate.
4. The merged pull request.

## 6. Docker Hub

- Product image: <https://hub.docker.com/r/prabhathchathura/product-service>
- Published tags: `1.0.0` and `latest`.
- Both tags use image digest `sha256:d52ae2696596bc1a6d096ebcf3b52d07e4ef6e80f46b0dc49c54e4b4ecb7669a`.
- Verification: the public `1.0.0` image was pulled into a fresh container, connected to PostgreSQL, and served its OpenAPI endpoint successfully.

Add screenshots of the local image, successful push, and public Docker Hub page.

## 7. Azure Deployment

- Azure App Service URL: `[add URL]`
- Azure PostgreSQL server: `[add server name]`

Add screenshots of Azure resources and Postman successfully creating and retrieving a product from the cloud URL.

## 8. Conclusion

The implementation demonstrates separate microservice repositories, database ownership, synchronous REST communication, asynchronous RabbitMQ messaging, automated tests, performance testing, CI/CD, static analysis, container publishing, and cloud deployment.
