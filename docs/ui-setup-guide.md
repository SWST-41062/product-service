# UI Setup Guide

Follow these sections in order. Azure is intentionally last.

## 1. Secrets and Environment Files

Never commit the real `.env` file. All three repositories already ignore `.env`.

- `.env.example`: committed; contains disposable local examples only.
- `.env`: not committed; contains the actual local values used by Docker Compose.
- `SONAR_TOKEN`: GitHub repository secret, never a file.
- Azure database password: Azure App Service environment variable, never a file.

The Spring applications require database and RabbitMQ credentials through environment variables. They no longer contain fallback passwords.

Before every push, check:

```bash
git status --short
git ls-files | rg '(^|/)\.env$'
```

The second command should print nothing.

## 2. Create the Free GitHub Organization

1. Open <https://github.com/account/organizations/new?plan=free>.
2. Choose **Create a free organization** if GitHub asks for a plan.
3. Organization name: `swst-41062-ecommerce-2026`.
4. Enter the contact email.
5. Choose **My personal account** when asked who owns the organization.
6. Complete the verification and create the organization.
7. Member invitations can be skipped for now and added later from **People → Invite member**.

After this is complete, Codex can create and push the three public repositories using the authenticated GitHub CLI.

## 3. Run the System

From Product Service:

```bash
cp .env.example .env  # only needed the first time
./build-and-run.sh
```

Open:

- Product Swagger: <http://localhost:18081/swagger-ui.html>
- Order Swagger: <http://localhost:18082/swagger-ui.html>
- RabbitMQ: <http://localhost:15672>

RabbitMQ uses the username and password stored in `.env`.

## 4. Use Postman

1. Open the Postman desktop application.
2. Click **Import**.
3. Import these two files:
   - `postman/ecommerce-microservices.postman_collection.json`
   - `postman/local.postman_environment.json`
   - From the Windows file picker, the folder is `\\wsl.localhost\Ubuntu\home\prabhath\works\Assignment\ecommerce-backend\product-service\postman`.
4. Select **E-commerce Local** from the environment selector at the upper right.
5. Open **Collections → E-commerce Microservices**.
6. Click **Run**.
7. On **Functional**, click **Run manually**.
8. Keep one iteration and click **Start run**.

The requests execute in this order:

1. Create Product and save its `productId`.
2. Get that Product.
3. Create an Order and verify the total is `151.00`.
4. Delete the Product as optional cleanup.

Capture the final runner screen showing four requests and four passed tests. Then verify the message:

```bash
docker compose logs notification-service | rg 'Mock notification sent'
```

## 5. Use JMeter

JMeter 5.6.3 is already installed. Make sure Product ID `1` exists, then run:

```bash
jmeter jmeter/product-service-load-test.jmx
```

In the JMeter window:

1. Expand **Product Service Load Test**.
2. Confirm **20 Virtual Users** has 20 threads, 10-second ramp-up, and 5 loops.
3. Click the green **Start** triangle.
4. Open **View Results Tree** and confirm requests are green/HTTP 200.
5. Open **Summary Report** and confirm 100 samples and 0 errors.
6. Capture both result screens.

If Product ID `1` does not exist, create a product and change the `productId` value under the test plan variables.

## 6. Connect Order Service to SonarCloud

Do this only after the GitHub organization and repositories exist.

1. Open <https://sonarcloud.io> and sign in with GitHub.
2. Click **Analyze new project**.
3. Install/authorize the SonarQube Cloud GitHub application for `swst-41062-ecommerce-2026`.
4. Give it access to **Only select repositories → order-service**.
5. Create/import the SonarQube Cloud organization using the free plan.
6. Import only `order-service` as a project.
7. Select **CI-based analysis → GitHub Actions → Maven**. Disable automatic analysis if it was enabled.
8. Copy the organization key, project key, and generated token shown by SonarQube Cloud.

In GitHub, open **order-service → Settings → Secrets and variables → Actions**:

1. Under **Secrets**, create `SONAR_TOKEN` with the token from SonarQube Cloud.
2. Under **Variables**, create `SONAR_ORGANIZATION` with the SonarQube Cloud organization key.
3. Under **Variables**, create `SONAR_PROJECT_KEY` with the Order Service project key.

The workflow is already stored at `.github/workflows/ci.yml`. It runs tests, builds the Maven project, and performs SonarCloud analysis whenever a pull request targets `main`.

## 7. Validate the Pull Request

After SonarCloud variables are saved:

1. Create a small branch and documentation change.
2. Push the branch.
3. Open a pull request into `main`.
4. Wait for the GitHub Actions and SonarCloud checks to pass.
5. Capture the PR checks and SonarCloud dashboard.
6. Merge the pull request.

Codex can perform the branch, push, and pull-request steps after the UI configuration is finished.

## 8. Remaining Work

After GitHub/SonarCloud:

1. Create a free Docker Hub repository and push Product Service.
2. Attempt Azure student subscription setup and deploy Product Service last.
3. Insert all screenshots and final URLs into `docs/assignment-report.md`.
