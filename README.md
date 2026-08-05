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

The frontend is packaged inside the API gateway, so no separate Node.js server or dependency installation is required.

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
