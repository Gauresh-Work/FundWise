# FundWise Backend

Simple Java 21 / Spring Boot Maven microservices for the FundWise mutual-fund case study. Each business service owns its own MySQL database. There are no joins across services: IDs are passed between services.

| Application | Port | Responsibility |
|---|---:|---|
| Discovery server | 8761 | Eureka registry |
| API gateway | 8080 | Single entry point and routing |
| Investor service | 8081 | Investors, bank mandates, nominees and KYC documents |
| Scheme service | 8082 | Schemes and NAV history |
| Folio service | 8083 | Folios, storing only investor and scheme IDs |
| Transaction service | 8084 | Purchase, redemption, SIP and switch records |
| Statement service | 8085 | Statement-generation records and simple JSON statements |
| Auth service | 8086 | Accounts, password hashing, rotating refresh tokens and roles |

## FundWise web application

The API gateway now includes a responsive operations frontend. After starting all services, open:

```text
http://localhost:8080
```

The web application includes:

- Portfolio dashboard with live operational totals and transaction activity
- Investor profile management
- KYC document, bank mandate, and nominee management
- Scheme management with NAV history
- Folio and holdings management
- Purchase, redemption, SIP, and switch transaction management
- Statement generation, viewing, and printing

The frontend is packaged inside the API gateway and uses Oracle JET with the Redwood theme, so no separate Node.js server is required.

### Authentication and authorization

Public registration is exclusively for investors. It creates a linked investor profile with pending KYC documents, a bank mandate, and a nominee. Investor accounts can access only their own onboarding portal.

Administrator accounts are never created through the public signup form. Provision the initial administrator once, before starting the auth service, by setting these environment variables:

```powershell
$env:FUNDWISE_BOOTSTRAP_ADMIN_NAME = "FundWise Administrator"
$env:FUNDWISE_BOOTSTRAP_ADMIN_EMAIL = "admin@example.com"
$env:FUNDWISE_BOOTSTRAP_ADMIN_PASSWORD = "UseAUniquePassword123"
.\run-all.ps1
```

The bootstrap is ignored after an `ADMIN` account already exists. An administrator can then assign approved staff roles from **Team access**:

- `ADMIN`: all operations, scheme management, API documentation, and user administration
- `ADVISOR`: read access plus investor, compliance, folio, transaction, and statement changes
- `VIEWER`: read-only access to operational data
- `INVESTOR`: access only to the investor's own onboarding status

Access tokens expire after 15 minutes. Refresh tokens are rotated, stored as SHA-256 hashes in MySQL, and revoked on logout. For non-local use, set the same strong secret for the gateway and auth service:

```powershell
$env:AUTH_JWT_SECRET = "replace-with-at-least-32-random-characters"
```

## Prerequisites

- JDK 21
- Maven 3.9+
- MySQL 8+

The default local credentials are `root` / `root123`. Change them without editing YAML:

```powershell
$env:DB_USERNAME = "your-user"
$env:DB_PASSWORD = "your-password"
```

Each service creates its own `fundwise_*` database on startup when the MySQL user has permission. Alternatively, create them yourself:

```sql
CREATE DATABASE fundwise_investor;
CREATE DATABASE fundwise_scheme;
CREATE DATABASE fundwise_folio;
CREATE DATABASE fundwise_transaction;
CREATE DATABASE fundwise_statement;
CREATE DATABASE fundwise_auth;
```

## Run from IntelliJ IDEA

Open the root `pom.xml` as a Maven project and configure the project SDK as Java 21. Start applications in this order:

1. `DiscoveryServerApplication`
2. `InvestorServiceApplication`, `SchemeServiceApplication`, `FolioServiceApplication`, `TransactionServiceApplication`, and `StatementServiceApplication`
3. `ApiGatewayApplication`

The services register at [Eureka](http://localhost:8761). Swagger is available at `http://localhost:<service-port>/swagger-ui.html` for every business service.

## Gateway routes

Use the gateway at port 8080 once all services are registered:

- `/investors/**`, `/bank-mandates/**`, `/nominees/**`, `/kyc-documents/**`
- `/schemes/**`
- `/folios/**`
- `/transactions/**`
- `/statements/**`

For example, `GET http://localhost:8080/investors` is forwarded to Investor Service.

## Build

```powershell
mvn clean verify
```

## Start everything with one command

With MySQL running, open PowerShell in this folder and run:

```powershell
.\run-all.ps1
```

The launcher uses your Java 17 installation. It starts all applications in the background and writes logs to the `logs` folder. For example, watch Investor Service live with:

```powershell
Get-Content .\logs\investor-service.out.log -Wait
```

Wait about a minute before opening Eureka at `http://localhost:8761`.

To stop every FundWise application:

```powershell
.\stop-all.ps1
```
