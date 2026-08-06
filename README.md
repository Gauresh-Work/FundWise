# FundWise

A mutual-fund investment platform built with Java 17, Spring Boot microservices, MySQL, and Oracle JET.

FundWise includes two frontend experiences:

- **FundWise Operations** — an admin dashboard served by the API Gateway.
- **FundWise Private Investing** — an Oracle JET investor portal for registration, portfolio tracking, fund discovery, investments, redemptions, and statements.

[▶ Watch the FundWise demo](assets/fundwise-demo.mp4)

## Architecture

| Application | Port | Responsibility |
|---|---:|---|
| Discovery Server | 8761 | Eureka service registry |
| API Gateway | 8080 | Single API entry point, routing, admin frontend, and CORS policy |
| Investor Service | 8081 | Investor profiles, bank mandates, nominees, and KYC documents |
| Scheme Service | 8082 | Mutual-fund schemes and NAV history |
| Folio Service | 8083 | Investor folios and holdings |
| Transaction Service | 8084 | Purchases, redemptions, SIPs, and switches |
| Statement Service | 8085 | Statement records and folio statement generation |
| Oracle JET Investor UI | 8000 | Private investor-facing frontend during development |

## Features

### Admin operations dashboard

Open the gateway frontend at:

```text
http://localhost:8080
```

It supports:

- Investor profile management
- KYC documents, bank mandates, and nominees
- Scheme and NAV history management
- Folio and holdings management
- Purchase, redemption, SIP, and switch transaction management
- Statement generation, viewing, and printing

### Investor portal

Run the Oracle JET application and open:

```text
http://localhost:8000
```

It supports:

- Premium landing page
- Investor registration and sign-in
- Private portfolio dashboard
- Investor-owned folio and transaction views
- Fund discovery and investments
- Partial or full folio encashment
- Profile, bank mandate, and nominee details
- Folio statement viewing and printing

The investor UI calls the API Gateway at `http://localhost:8080`, which routes each request to the correct microservice.

## Prerequisites

- Java 17
- Maven 3.9+
- MySQL 8+
- Node.js 16+ and npm for the Oracle JET frontend

Default local database credentials:

```text
Username: root
Password: root
```

Override them without editing YAML:

```powershell
$env:DB_USERNAME = "your-user"
$env:DB_PASSWORD = "your-password"
```

Each service creates its own database on startup when the MySQL user has permission:

```sql
CREATE DATABASE fundwise_investor;
CREATE DATABASE fundwise_scheme;
CREATE DATABASE fundwise_folio;
CREATE DATABASE fundwise_transaction;
CREATE DATABASE fundwise_statement;
```

## Run the backend

With MySQL running, open PowerShell in the project root:

```powershell
.\run-all.ps1
```

The launcher starts all Spring Boot applications in the background and writes logs to the `logs` folder.

Open Eureka after the services have registered:

```text
http://localhost:8761
```

To stop all FundWise applications:

```powershell
.\stop-all.ps1
```

## Run the investor frontend

From the project root:

```powershell
cd .\fundwise-ui
npm install
npx ojet serve
```

Then open:

```text
http://localhost:8000
```

The API Gateway allows requests from the Oracle JET development server through its CORS configuration.

## Run from IntelliJ IDEA

Open the root `pom.xml` as a Maven project and use Java 17.

Start services in this order:

1. `DiscoveryServerApplication`
2. `InvestorServiceApplication`
3. `SchemeServiceApplication`
4. `FolioServiceApplication`
5. `TransactionServiceApplication`
6. `StatementServiceApplication`
7. `ApiGatewayApplication`

## Gateway routes

Use the API Gateway at port `8080` once all services are registered:

- `/investors/**`
- `/bank-mandates/**`
- `/nominees/**`
- `/kyc-documents/**`
- `/schemes/**`
- `/folios/**`
- `/transactions/**`
- `/statements/**`

Example:

```text
GET http://localhost:8080/investors
```

The gateway forwards this request to the Investor Service on port `8081`.

## Build

Build all backend services:

```powershell
mvn clean verify
```

Build the Oracle JET frontend:

```powershell
cd .\fundwise-ui
npx ojet build --release
```

## Security note

The investor UI filters portfolio, profile, folio, redemption, and statement views to the currently signed-in investor. A production deployment should additionally enforce authentication and investor ownership at the API Gateway and service layers.
