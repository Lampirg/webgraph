# Simple Star Wars statistics

This is a full-stack project that displays simple statistic about characters from Star Wars franchise. Note that this is more of a training pet-project rather than unique polished product. If you really want gather statistics about Star Wars franchise, use [this GraphQL API](https://studio.apollographql.com/public/star-wars-swapi/variant/current/home) instead.

Project consists of [Spring Boot backend](backend) and [Vue.js frontend](frontend).

Backend is a REST API. Frontend consumes this API.


## Usage

Right now the service isn't deployed anywhere so the only way to use it is to clone repository and run it manually.

To run application you will need Docker installed. If so, you can execute command `docker compose -f "docker-compose.yml" up -d --build` in root directory. After that, you can access frontend application with `localhost:8081` path and backend with `localhost:8080` path. Note that you will need a header `Key` with valid API key (not hard to find in source code). Otherwise your request will be forbidden.

To see all available backend endpoints you can use Swagger UI with `/swagger-ui` endpoint.

## Tech stack

- Java 17
- Spring Boot
- Spring WebFlux
- Spring Security
- Spring Data MongoDB
- Spring GraphQL (for consuming)
- OpenAPI
- Testcontainers
- Vue.js
- Docker