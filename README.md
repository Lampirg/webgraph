# Simple Star Wars statistics

This is a REST service that displays simple statistic about characters from Star Wars franchise. Note that this service is made for training purpose rather than polished product. If you really want gather statistics about Star Wars franchise, use [this GraphQL API](https://studio.apollographql.com/public/star-wars-swapi/variant/current/home) instead.

## Usage

Right now the service isn't deployed anywhere so the only way to use it is to clone repository and run it manually.

The service has Swagger UI API docs which can be accessed with "/swagger-ui" path.

The service has no frontend (except for the Swagger).

## Tech stack

- Java 17
- Spring Boot
- Spring WebFlux
- Spring Security
- Spring Data MongoDB
- Spring GraphQL (for consuming)
- OpenAPI
- Testcontainers