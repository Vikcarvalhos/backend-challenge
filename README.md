# JWT Validator

API REST em Java 21 e Spring Boot 3 para validar JWTs conforme as regras de um desafio técnico.

O objetivo da solução é receber um JWT como string e retornar apenas um booleano indicando se ele é válido para as regras de negócio propostas.

```json
{
  "valid": true
}
```

ou

```json
{
  "valid": false
}
```

Motivos técnicos de invalidação ficam restritos aos logs internos.

## 1. Regras de Validação

Um JWT é considerado válido quando:

- possui estrutura com três partes separadas por ponto;
- header e payload são decodificáveis em Base64URL;
- header decodificado é um objeto JSON;
- payload decodificado é um objeto JSON;
- contém exatamente 3 claims:
  - `Name`
  - `Role`
  - `Seed`
- `Name` não contém números;
- `Name` possui no máximo 256 caracteres;
- `Role` possui somente um dos valores:
  - `Admin`
  - `Member`
  - `External`
- `Seed` é numérico e primo.

## 2. API

Endpoint principal:

```http
POST /api/v1/jwt/validate
```

Request:

```json
{
  "token": "eyJ..."
}
```

Response para JWT válido:

```json
{
  "valid": true
}
```

Response para JWT inválido por regra de negócio:

```json
{
  "valid": false
}
```

Códigos HTTP:

- `200 OK`: JWT processado, válido ou inválido por regra de negócio.
- `400 Bad Request`: request malformado, JSON inválido, token ausente ou token vazio.
- `500 Internal Server Error`: erro inesperado.

## 3. Arquitetura da Aplicação

A aplicação foi organizada em camadas simples:

```text
app/src/main/java/com/challengeitau/jwtvalidator/
|-- config/
|-- controller/
|-- domain/
|-- dto/
|-- exception/
|-- service/
`-- validator/
    |-- ClaimRule.java
    |-- ClaimsValidator.java
    `-- rules/
        |-- ClaimCountRule.java
        |-- NameRule.java
        |-- RoleRule.java
        `-- SeedPrimeRule.java
```

Responsabilidades:

- `controller`: expõe o contrato HTTP.
- `dto`: representa request e response.
- `service`: coordena extração das claims e validação.
- `domain`: representa objetos centrais da validação.
- `validator`: orquestra as regras.
- `validator/rules`: contém uma regra por responsabilidade.
- `exception`: centraliza tratamento de erros HTTP.
- `config`: configurações da aplicação, como OpenAPI.

As regras implementam a interface `ClaimRule`, permitindo adicionar novas regras com baixo acoplamento e sem alterar o fluxo principal de validação.

## 4. Arquitetura AWS

A arquitetura oficial da solução é:

```text
GitHub
    |
    v
GitHub Actions
    |
    v
Build + Test + Docker Build
    |
    v
Push para AWS ECR
    |
    v
Terraform/OpenTofu
    |
    v
AWS ECR
    |
    v
AWS ECS Cluster
    |
    v
AWS ECS Service
    |
    v
AWS Fargate Task
    |
    v
Application Load Balancer
    |
    v
JWT Validator
```

Essa arquitetura utiliza serviços gerenciados da AWS para reduzir complexidade operacional, manter a aplicação stateless e permitir escalabilidade horizontal.

## 5. Tecnologias

Aplicação:

- Java 21
- Spring Boot 3
- Spring Web
- Spring Validation
- Spring Boot Actuator
- Springdoc OpenAPI / Swagger
- Micrometer Tracing
- JUnit 5

Build e execução:

- Maven
- Maven Wrapper
- Docker

Cloud e infraestrutura:

- AWS ECR
- AWS ECS
- AWS Fargate
- Application Load Balancer
- CloudWatch Logs
- Terraform/OpenTofu

CI/CD:

- GitHub Actions

## 6. Estrutura do Repositório

```text
.
|-- .github/
|   `-- workflows/
|       `-- ci.yml
|-- app/
|   |-- Dockerfile
|   |-- pom.xml
|   |-- mvnw
|   |-- mvnw.cmd
|   `-- src/
|-- infra/
|   `-- terraform/
`-- README.md
```

## 7. Decisões Técnicas e Premissas

### 7.1 Validação do JWT

O desafio não fornece material criptográfico, como chave secreta, chave pública, JWKS, emissor confiável ou algoritmo esperado.

A validação adotada é estrutural e baseada nas claims do payload:

- quantidade de partes do JWT;
- decodificação Base64URL;
- payload JSON;
- presença exata de `Name`, `Role` e `Seed`;
- regras de negócio de cada claim.

A assinatura criptográfica não é validada. Essa decisão evita uma falsa sensação de segurança e mantém a implementação aderente ao enunciado.

### 7.2 Contrato da API

A API retorna apenas:

```json
{
  "valid": true
}
```

ou

```json
{
  "valid": false
}
```

Motivos de invalidação não aparecem no body da resposta. Eles ficam nos logs internos para fins técnicos e operacionais.

JWT inválido por regra de negócio retorna `200 OK` com `valid=false`. Request malformado retorna `400 Bad Request`.

### 7.3 Organização das Regras

Cada regra possui uma classe dedicada:

- `ClaimCountRule`
- `NameRule`
- `RoleRule`
- `SeedPrimeRule`

Essa organização favorece:

- uma regra por responsabilidade;
- alta coesão;
- baixo acoplamento;
- testes unitários focados;
- extensibilidade por composição.

Novas regras podem ser adicionadas criando novas implementações de `ClaimRule`, sem alterar o fluxo principal de validação.

### 7.4 Execução e Implantação

A aplicação é empacotada com Docker e executada em AWS ECS com Fargate.

Fluxo de execução e implantação:

- GitHub Actions executa build, testes e Docker build.
- A imagem Docker é armazenada no Amazon ECR.
- ECS Task Definition usa a imagem do ECR.
- ECS Service mantém a quantidade desejada de tasks.
- Fargate executa os containers sem gerenciamento de servidores.
- Application Load Balancer expõe a API e direciona tráfego para as tasks.
- Terraform/OpenTofu descreve e provisiona a infraestrutura.

### 7.5 Observabilidade

A solução cobre os três pilares de observabilidade:

- Logging com SLF4J/Logback.
- Monitoring com Spring Boot Actuator.
- Tracing básico com Micrometer Tracing.

Os logs incluem `traceId` e `spanId`, mas não incluem JWT completo, payload completo ou claims completas.

### 7.6 Escalabilidade

A aplicação é stateless:

- não mantém sessão;
- não depende de disco local;
- não armazena estado de usuário em memória;
- processa cada requisição de forma independente;
- não depende do número de tasks em execução.

O mesmo container funciona com uma ou várias tasks no ECS, sem alteração no código.

```text
Application Load Balancer
|-- ECS Task 1
|-- ECS Task 2
|-- ECS Task 3
`-- ECS Task N
```

### 7.7 Ferramentas de Execução

Swagger UI é a ferramenta principal para exploração e execução manual da API:

```text
http://localhost:8080/swagger-ui/index.html
```

A especificação OpenAPI fica disponível em:

```text
http://localhost:8080/v3/api-docs
```

## 8. Decisões Conscientemente Não Adotadas

### HATEOAS

Não foi adotado porque a API possui uma única operação de validação e não apresenta navegação entre recursos ou transições de estado.

A alternativa escolhida foi uma API REST simples, com recurso, verbo HTTP e status codes claros.

O trade-off aceito é não expor links de navegação, já que eles não agregam valor ao domínio deste desafio.

### Kubernetes e Helm

Não foram adotados porque aumentam a complexidade operacional para uma aplicação stateless de escopo reduzido.

A alternativa escolhida foi AWS ECS com Fargate, que entrega orquestração gerenciada, execução de containers e escalabilidade com menos carga operacional.

O trade-off aceito é menor portabilidade entre provedores em troca de simplicidade e integração direta com serviços AWS.

### EKS

Não foi adotado porque implicaria operar Kubernetes na AWS, mantendo a mesma complexidade que a solução evita.

A alternativa escolhida foi ECS/Fargate.

O trade-off aceito é abrir mão do ecossistema Kubernetes em favor de uma plataforma gerenciada mais simples para o contexto do desafio.

### Jaeger e Zipkin

Não foram adotados porque a solução possui um único serviço e não exige backend completo de tracing distribuído.

A alternativa escolhida foi Micrometer Tracing com `traceId` e `spanId` nos logs.

O trade-off aceito é não ter visualização gráfica de traces, mantendo correlação suficiente nos logs para este escopo.

### Validação Criptográfica da Assinatura

Não foi adotada porque o desafio não fornece chave secreta, chave pública, JWKS, emissor confiável ou algoritmo esperado.

A alternativa escolhida foi validar estrutura, payload e claims.

O trade-off aceito é não afirmar autenticidade criptográfica do token, mantendo a validação limitada às regras informadas.

## 9. Observabilidade

Logging:

- SLF4J/Logback.
- Logs de entrada, sucesso e falha.
- Logs com motivo técnico genérico.
- Sem JWT completo, payload completo ou claims completas.

Monitoring:

- Spring Boot Actuator.
- Endpoints:
  - `/actuator/health`
  - `/actuator/info`
  - `/actuator/metrics`

Tracing:

- Micrometer Tracing.
- `traceId` e `spanId` nos logs.

Exemplo:

```text
INFO [traceId=..., spanId=...] JwtValidationService : Received JWT validation request
INFO [traceId=..., spanId=...] JwtValidationService : JWT validated successfully
WARN [traceId=..., spanId=...] JwtValidationService : Invalid JWT structure
```

## 10. Amazon ECR

Amazon Elastic Container Registry armazena as imagens Docker da aplicação.

Responsabilidades:

- manter o repositório de imagens;
- receber imagens geradas pelo pipeline;
- disponibilizar a imagem para o ECS Task Definition;
- integrar autenticação e permissões com IAM.

Fluxo:

```text
docker build
  -> docker tag
  -> docker push
  -> AWS ECR
  -> ECS Task Definition
```

## 11. AWS ECS e Fargate

### ECS Cluster

O ECS Cluster agrupa os serviços da aplicação.

Neste projeto, ele concentra o serviço responsável por executar o JWT Validator.

### ECS Task Definition

A Task Definition descreve como o container é executado.

Ela define:

- imagem Docker armazenada no ECR;
- CPU;
- memória;
- porta do container;
- variáveis de ambiente;
- configuração de logs;
- health checks;
- role de execução.

### ECS Service

O ECS Service mantém a aplicação em execução.

Ele define:

- desired count;
- substituição automática de tasks com falha;
- associação com Target Group do ALB;
- estratégia de atualização;
- execução em Fargate.

### AWS Fargate

O Fargate executa as tasks sem necessidade de gerenciar instâncias EC2.

A escolha reduz operação de infraestrutura, mantendo foco no container, na configuração de recursos e na disponibilidade do serviço.

## 12. Application Load Balancer

O Application Load Balancer expõe a API publicamente e distribui tráfego para as tasks ECS.

Responsabilidades:

- receber tráfego HTTP;
- encaminhar requests para o Target Group;
- executar health check em `/actuator/health`;
- balancear tráfego entre múltiplas tasks;
- permitir escalabilidade horizontal sem alterar a aplicação.

## 13. Terraform/OpenTofu

A infraestrutura é descrita como código com Terraform/OpenTofu.

Recursos provisionados:

- ECR;
- ECS Cluster;
- ECS Task Definition;
- ECS Service;
- Application Load Balancer;
- Target Group;
- Listener HTTP;
- IAM Roles;
- Security Groups;
- CloudWatch Log Group;
- Outputs.

Outputs relevantes:

- DNS do ALB;
- URL do repositório ECR;
- nome do ECS Cluster;
- nome do ECS Service.

O `terraform apply` não é executado automaticamente no CI. A decisão prioriza controle operacional e evita aplicar mudanças de infraestrutura sem revisão manual.

## 14. Swagger

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

A documentação contém:

- regras do desafio;
- premissa sobre assinatura;
- exemplos dos 4 casos oficiais;
- respostas `valid=true` e `valid=false`;
- documentação dos status `200`, `400` e `500`.

## 15. Testes

A suíte cobre:

- casos oficiais do desafio;
- regras de claims;
- estrutura inválida do JWT;
- payload inválido;
- claims ausentes ou extras;
- `Name` com número e limite de tamanho;
- `Role` inválida;
- `Seed` não numérico, não primo e primo;
- request com token vazio;
- request com token ausente;
- JSON malformado;
- Swagger/OpenAPI;
- Actuator health/info/metrics.

Rodar testes:

```powershell
cd app
.\mvnw.cmd test
```

Em Linux/macOS:

```bash
cd app
./mvnw test
```

## 16. Execução Local

Pré-requisitos:

- Java 21.
- Maven Wrapper incluído no projeto.

Subir a aplicação:

```powershell
cd app
.\mvnw.cmd spring-boot:run
```

Em Linux/macOS:

```bash
cd app
./mvnw spring-boot:run
```

Health check:

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

## 17. Exemplo de Chamada

PowerShell:

```powershell
$body = @{
  token = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri http://localhost:8080/api/v1/jwt/validate `
  -ContentType "application/json" `
  -Body $body
```

Resposta esperada:

```json
{
  "valid": true
}
```

## 18. Docker

Build da imagem:

```powershell
docker build -t jwt-validator:local ./app
```

Executar container:

```powershell
docker run --rm -p 8080:8080 jwt-validator:local
```

Se a porta `8080` estiver em uso:

```powershell
docker run --rm -p 8081:8080 jwt-validator:local
```

Validar container:

```powershell
Invoke-RestMethod http://localhost:8081/actuator/health
```

O `Dockerfile` usa build multi-stage:

- etapa de build com JDK 21;
- etapa final com JRE 21;
- execução com usuário não-root;
- porta `8080` exposta.

## 19. CI/CD

O workflow fica em:

```text
.github/workflows/ci.yml
```

Ele executa:

- checkout do repositório;
- setup do Java 21;
- cache Maven;
- testes;
- package da aplicação;
- upload do JAR como artifact;
- Docker build.

O fluxo de entrega integra GitHub Actions, Docker, ECR, ECS/Fargate e Terraform/OpenTofu conforme a arquitetura da solução.

## 20. Engenharia de Prompt

Durante o planejamento da solução, ferramentas de IA foram utilizadas para apoiar discussões sobre arquitetura, infraestrutura, observabilidade, testes e documentação.

Todas as decisões finais foram revisadas criticamente e ajustadas ao escopo do desafio, priorizando aderência ao enunciado, simplicidade e boas práticas de engenharia.

## 21. Comandos Úteis

Testes:

```powershell
cd app
.\mvnw.cmd test
```

Package:

```powershell
cd app
.\mvnw.cmd package
```

Docker build:

```powershell
docker build -t jwt-validator:local ./app
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

Health:

```text
http://localhost:8080/actuator/health
```




