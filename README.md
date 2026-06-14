# JWT Validator

API REST para validar JWTs conforme as regras do desafio técnico.

## Status

Projeto em construção incremental.

Nesta etapa inicial, o repositório contém a estrutura base da aplicação Spring Boot, o contrato inicial do endpoint e os pacotes principais do domínio.

## Stack inicial

- Java 21
- Spring Boot 3
- Maven
- Spring Web
- Spring Validation
- Spring Boot Actuator
- JUnit 5

## Estrutura

```text
app/
|-- pom.xml
|-- src/main/java/com/itau/jwtvalidator/
|   |-- JwtValidatorApplication.java
|   |-- controller/
|   |-- domain/
|   |-- dto/
|   |-- service/
|   `-- validator/
`-- src/test/java/com/itau/jwtvalidator/
```

## API

```http
POST /api/v1/jwt/validate
```

Request:

```json
{
  "token": "eyJ..."
}
```

Response:

```json
{
  "valid": false
}
```

As regras de validação serão implementadas nos próximos commits.
