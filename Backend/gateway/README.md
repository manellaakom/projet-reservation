# Static API Gateway

The gateway routes directly to the fixed local microservice URLs; it does not
use Eureka to find its destinations.

| Gateway path | Destination |
|---|---|
| `/api/candidates/**` | `http://localhost:8081` |
| `/api/jobs/**` | `http://localhost:8082` |

## Run

Start the `candidat` and `job` services first, then run the gateway:

```powershell
.\mvnw.cmd spring-boot:run
```

The gateway listens on port `8088` (port `8080` may already be used by another local service). It forwards each path unchanged, so test
the candidate and job APIs through:

```text
http://localhost:8088/api/candidates
http://localhost:8088/api/jobs
```

These should behave like requests sent directly to ports `8081` and `8082`.
For example, an unknown API path should return the upstream service's 404;
if the upstream service is stopped, the gateway reports a connection error.

## Keycloak security

Start Keycloak with `docker compose -f Backend/docker-compose.yml up -d keycloak`.
The imported realm is `skyvoyage`, with the public frontend client
`skyvoyage-frontend`. The Keycloak administration console is available at
`http://localhost:8180` with `admin` / `admin` for local development only.
Every gateway route requires a bearer token except the Paymee webhook and
preflight requests. Never use the development administrator password outside
local development.

Run the gateway route configuration test with:

```powershell
.\mvnw.cmd test
```

## Docker

`docker compose -f Backend/docker-compose.yml up --build` starts Redis,
Keycloak (port 8180), Eureka (8761), the gateway (8088), bagages and paiement.
Test users in realm `skyvoyage`: `client1`/`client1` (role client) and
`admin1`/`admin1` (roles admin, client) — local development only.
