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

The gateway listens on port `8080`. It forwards each path unchanged, so test
the candidate and job APIs through:

```text
http://localhost:8080/api/candidates
http://localhost:8080/api/jobs
```

These should behave like requests sent directly to ports `8081` and `8082`.
For example, an unknown API path should return the upstream service's 404;
if the upstream service is stopped, the gateway reports a connection error.

Run the gateway route configuration test with:

```powershell
.\mvnw.cmd test
```
