# hello-service

Minimal Spring Boot 3 / Java 21 service used as a study lab for GitOps delivery and observability on Kubernetes.

| Endpoint | Purpose |
| --- | --- |
| `GET /hello?name=...` | Returns a greeting and the running version (commit SHA) |
| `GET /fail` | Always returns HTTP 500, to trigger alerts and SLO burn on purpose |
| `GET /actuator/health/liveness`, `/readiness` | Kubernetes probes |
| `GET /actuator/prometheus` | Metrics: HTTP requests, JVM, and `hello_greetings_total` |

## Delivery flow

1. A push to `main` runs the tests (`mvn verify`).
2. The image is built once, tagged with the commit SHA and pushed to `ghcr.io/cyb3rdm0nlab/hello-service`.
3. The pipeline writes that tag into [`hello-config`](https://github.com/cyb3rDm0nLab/hello-config) (`environments/lab/values.yaml`).
4. Argo CD, running in the cluster, sees the change and rolls out the new version.

The cluster is never reached by the pipeline: Argo CD pulls from Git.

## Run locally

```bash
mvn spring-boot:run
curl localhost:8080/hello?name=lab
curl localhost:8080/actuator/prometheus | grep hello_greetings

# or with Docker
docker build -t hello-service .
docker run --rm -p 8080:8080 hello-service
```
