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

Esta entrega não mantém um ambiente cloud ativo nem uma URL pública permanente para evitar custo recorrente em conta pessoal. A solução entrega aplicação containerizada, pipeline de CI e arquitetura de infraestrutura como código para que o ambiente seja reproduzido em AWS quando necessário.

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

A exposição pública em cloud é descrita pela arquitetura e pela infraestrutura como código. O ambiente não permanece ativo nesta entrega por decisão de controle de custo.

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

Swagger UI foi escolhido como ferramenta oficial de execução por estar integrado à própria aplicação, documentar o contrato OpenAPI e permitir testar os cenários do desafio sem depender de coleção externa.

## 8. Observabilidade

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

## 9. Amazon ECR

Amazon Elastic Container Registry armazena as imagens Docker da aplicação.

Responsabilidades:

- manter o repositório de imagens;
- receber imagens geradas pelo pipeline;
- disponibilizar a imagem para o ECS Task Definition;
- integrar autenticação e permissões com IAM.

Estimativa de custo:

- O ECR cobra principalmente armazenamento das imagens.
- A referência pública da AWS para repositórios privados é de aproximadamente `US$ 0.10 por GB-mês`.
- Para uma ou poucas imagens pequenas do desafio, o custo tende a ficar em centavos por mês.
- Referência: https://aws.amazon.com/ecr/pricing/

Fluxo:

```text
docker build
  -> docker tag
  -> docker push
  -> AWS ECR
  -> ECS Task Definition
```

## 10. AWS ECS e Fargate

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

Configuração mínima adotada para este desafio:

- CPU: `256` units (`0.25 vCPU`).
- Memória: `512 MiB`.
- Desired count: `1`.
- Sistema: Linux/x86.

Estimativa de custo do Fargate:

- Em `us-east-1`, a referência pública da AWS para Linux/x86 indica cobrança por vCPU/segundo e GB/segundo.
- Com `0.25 vCPU` e `0.5 GB`, a task fica em aproximadamente `US$ 0.0123/hora`.
- Rodando 24 horas, isso representa cerca de `US$ 0.30/dia`.
- Rodando um mês inteiro, isso representa cerca de `US$ 9/mês`.
- Referência: https://aws.amazon.com/fargate/pricing/

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

## 11. Application Load Balancer

O Application Load Balancer expõe a API publicamente e distribui tráfego para as tasks ECS.

Responsabilidades:

- receber tráfego HTTP;
- encaminhar requests para o Target Group;
- executar health check em `/actuator/health`;
- balancear tráfego entre múltiplas tasks;
- permitir escalabilidade horizontal sem alterar a aplicação.

Estimativa de custo:

- O ALB cobra por hora de uso e por capacidade consumida.
- A referência pública da AWS mostra cobrança base por Application Load Balancer-hour, além de possíveis custos por LCU conforme tráfego.
- Para este desafio, mesmo com baixo tráfego, o ALB tende a ser o principal custo fixo da arquitetura.
- Estimativa base: cerca de `US$ 0.54/dia` ou `US$ 16/mês`, antes de variações por LCU, região e impostos.
- Referência: https://aws.amazon.com/elasticloadbalancing/pricing/

## 12. Terraform/OpenTofu

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

Como a API não permanece exposta em cloud nesta entrega, o uso de Terraform/OpenTofu representa a forma reprodutível de provisionar a arquitetura AWS quando houver uma conta com orçamento aprovado para execução.

Decisões de custo na infraestrutura:

- Não utilizar NAT Gateway, pois ele adiciona custo fixo relevante para o escopo do desafio.
- Usar apenas `desired_count = 1` para ECS Service.
- Usar a menor task Fargate suficiente para a aplicação: `256 CPU / 512 MiB`.
- Configurar retenção limitada de logs no CloudWatch.
- Manter `terraform apply` como ação manual.

## 13. Swagger

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

Por esse motivo, não foi criada coleção Insomnia ou Postman. A execução manual da API é atendida pelo Swagger UI e pelos exemplos de chamada documentados neste README.

## 14. Testes

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

## 15. Execução Local

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

## 16. Exemplo de Chamada

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

## 17. Docker

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

## 18. CI/CD

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

O deploy automatizado para AWS é estruturado em duas partes: build e validação da aplicação no CI, e provisionamento da infraestrutura por Terraform/OpenTofu. O `apply` da infraestrutura permanece como uma ação controlada, evitando criação automática de recursos pagos.

## 19. Engenharia de Prompt

Durante o planejamento da solução, ferramentas de IA foram utilizadas para apoiar discussões sobre arquitetura, infraestrutura, observabilidade, testes e documentação.

Todas as decisões finais foram revisadas criticamente e ajustadas ao escopo do desafio, priorizando aderência ao enunciado, simplicidade e boas práticas de engenharia.

## 20. Estimativa de Custo AWS

As estimativas abaixo consideram `us-east-1`, Linux/x86, 1 task Fargate `0.25 vCPU / 512 MiB`, 1 Application Load Balancer, baixo volume de logs e uma imagem pequena no ECR.

Valores consultados em 14/06/2026 nas páginas públicas de pricing da AWS:

- Fargate: https://aws.amazon.com/fargate/pricing/
- Application Load Balancer: https://aws.amazon.com/elasticloadbalancing/pricing/
- ECR: https://aws.amazon.com/ecr/pricing/
- CloudWatch: https://aws.amazon.com/cloudwatch/pricing/
- IPv4 público: https://aws.amazon.com/blogs/aws/new-aws-public-ipv4-address-charge-public-ip-insights/

Estimativa por componente:

```text
ECS Fargate 0.25 vCPU / 512 MiB:  ~US$ 0.30/dia  | ~US$ 9/mês
Application Load Balancer:         ~US$ 0.54/dia  | ~US$ 16/mês + uso de LCU
IPv4 público:                      ~US$ 0.12/dia por IP público
ECR:                               centavos por mês para imagem pequena
CloudWatch Logs:                   depende do volume de logs; baixo para uso de desafio
NAT Gateway:                       US$ 0, pois não é utilizado
```

Estimativa prática para manter a arquitetura ligada:

```text
1 dia:      ~US$ 1 a US$ 1.50
3 dias:     ~US$ 3 a US$ 5
7 dias:     ~US$ 7 a US$ 10
30 dias:    ~US$ 30 a US$ 40
```

Esses valores não incluem impostos, variações de região, tráfego, uso adicional de logs, múltiplas tasks ou mudanças futuras de preço da AWS.

Por esse motivo, esta entrega não mantém uma URL pública ativa. Quem avaliar a solução consegue provisionar a infraestrutura com Terraform/OpenTofu, validar a execução em AWS e destruir os recursos ao final para evitar custo recorrente.

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


