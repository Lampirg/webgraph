# Simple Star Wars statistics

This is a full-stack project that displays simple statistic about characters from Star Wars franchise. Note that this service is made for training purpose rather than polished product. If you really want gather statistics about Star Wars franchise, use [this GraphQL API](https://studio.apollographql.com/public/star-wars-swapi/variant/current/home) instead.

Project consists of [Spring Boot backend](backend) and [Vue.js frontend](frontend).

Backend is a REST API. Frontend consumes this API.


## Usage

Right now the service isn't deployed anywhere so the only way to use it is to clone repository and run it manually.

The service has Swagger UI API docs which can be accessed with ["/swagger-ui"](localhost:8080/swagger-ui) endpoint.

Since you have to run project manually, you can either run both applications (frontend and backend) or run only backend REST API. To do the last you can run command './mvnw spring-boot:run' in [backend directory](backend).

If you want to use both applications, you should also run vue.js app with 'npm run serve -- --port 8081' in [frontend directory](frontend). You can access it with [localshost:8081/](localshost:8081/) path.

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