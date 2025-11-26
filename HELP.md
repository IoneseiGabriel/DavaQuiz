
# DavaQUIZ

Spring Boot + H2 example exposing `GET /api/hello` backed by an in-memory H2 database. SQL schema and seed data are applied from `src/main/resources/schema.sql` and `src/main/resources/data.sql`.

## Requirements
- Java 17+
- Maven
- IntelliJ IDEA on Windows

## Build & run

Run from project root:

```bash
mvn -DskipTests clean package
