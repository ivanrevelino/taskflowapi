# TaskFlowAPI

API REST para gerenciamento de projetos e tarefas, com autenticação JWT, controle de acesso baseado em papéis (roles) e suporte a membros de projeto, comentários aninhados e atribuição de tarefas.
A aplicação permite que usuários criem projetos, convidem membros, gerenciem tarefas (com status TO_DO, IN_PROGRESS e COMPLETED), atribuam tarefas a si mesmos (claim/release), deixem comentários e respostas, e visualizem seus dados de perfil.
---
🛠️ Stack Tecnológico
Java 21
Spring Boot 4.1.0
Spring Security + JWT (java-jwt)
Spring Data JPA
PostgreSQL
Flyway (migrações de banco)
MapStruct 1.6.0
Lombok
SpringDoc OpenAPI 3 (Swagger)
Docker + Docker Compose
---
📋 Pré-requisitos
Java 21+
Maven 3.6+ (ou use o Maven Wrapper `./mvnw`)
Docker e Docker Compose (recomendado para o banco)
PostgreSQL 12+ (caso não use Docker)
---
⚙️ Configuração das Variáveis de Ambiente
Copie o arquivo de exemplo:
```bash
cp .env.example .env
```
Edite o arquivo `.env` com suas configurações:
```env
# ====================
# Database Configuration
# ====================
DB_URL=jdbc:postgresql://localhost:5434/taskflow_db
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha_aqui

# ====================
# JWT / Security
# ====================
JWT_SECRET=sua_chave_secreta_muito_longa_e_segura_aqui_minimo_32_caracteres

# ====================
# Outras configurações
# ====================
SERVER_PORT=8080
```
> **Importante:** O Docker Compose expõe o PostgreSQL na porta **5434** (para não conflitar com uma instalação local na 5432). Ajuste o `DB_URL` de acordo.
---
🐳 Subindo o Container (PostgreSQL)
Na raiz do projeto, execute:
```bash
docker compose up -d
```
Isso sobe o banco PostgreSQL 17 com o volume persistente `taskflow_postgres_data`.
Para parar:
```bash
docker compose down
```
---
🚀 Primeiros Passos
Clone o repositório:
```bash
git clone https://github.com/ivanrevelino/taskflowapi.git
cd taskflowapi
```
Configure o `.env` (passo anterior).
Suba o banco com Docker Compose (passo anterior).
Execute a aplicação:
```bash
./mvnw spring-boot:run
```
A API estará disponível em:  
http://localhost:8080
---
🔐 Autenticação
Todos os endpoints protegidos exigem o header:
```
Authorization: Bearer SEU_JWT_TOKEN
```
Registro
Request
```http
POST /auth/register
Content-Type: application/json

{
  "name": "Ivan Revelino",
  "username": "ivanrevelino",
  "password": "senha12345"
}
```
Response (201 Created)
```json
{
  "id": 1,
  "name": "Ivan Revelino",
  "username": "ivanrevelino",
  "createdAt": "2026-10-11T20:00:00",
  "role": "USER"
}
```
Login
Request
```http
POST /auth/login
Content-Type: application/json

{
  "username": "ivanrevelino",
  "password": "senha12345"
}
```
Response (200 OK)
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
Refresh Token
Request
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```
---
📌 Exemplos de Endpoints
Criar Projeto
Request
```http
POST /projects
Authorization: Bearer SEU_TOKEN
Content-Type: application/json

{
  "name": "Meu Projeto",
  "description": "Descrição do projeto de exemplo"
}
```
Response (201 Created)
```json
{
  "id": 1,
  "name": "Meu Projeto",
  "description": "Descrição do projeto de exemplo",
  "owner": {
    "id": 1,
    "name": "Ivan Revelino",
    "username": "ivanrevelino",
    "createdAt": "2026-10-11T20:00:00",
    "role": "USER"
  },
  "members": []
}
```
Listar Meus Projetos
```http
GET /projects/me
Authorization: Bearer SEU_TOKEN
```
Criar Tarefa
Request
```http
POST /projects/1/tasks
Authorization: Bearer SEU_TOKEN
Content-Type: application/json

{
  "title": "Implementar autenticação",
  "description": "Adicionar JWT e Spring Security"
}
```
Response (201 Created)
```json
{
  "id": 1,
  "title": "Implementar autenticação",
  "description": "Adicionar JWT e Spring Security",
  "status": "TO_DO",
  "comments": [],
  "createdAt": "2026-10-11T20:05:00",
  "assignee": null
}
```
Marcar Tarefa como Completa
```http
PATCH /projects/1/tasks/1/complete
Authorization: Bearer SEU_TOKEN
```
Atribuir Tarefa a Si Mesmo (Claim)
```http
PATCH /projects/1/tasks/1/claim
Authorization: Bearer SEU_TOKEN
```
Liberar Tarefa (Release)
```http
PATCH /projects/1/tasks/1/release
Authorization: Bearer SEU_TOKEN
```
Filtrar Tarefas por Status
```http
GET /projects/1/tasks/filter?status=TO_DO
Authorization: Bearer SEU_TOKEN
```
---
📖 Documentação Completa (Swagger)
Para ver todos os endpoints, parâmetros, schemas e testar interativamente, acesse a documentação Swagger enquanto a aplicação estiver rodando:
```
http://localhost:8080/swagger-ui.html
```
Também disponível em formato OpenAPI:
```
http://localhost:8080/v3/api-docs
```
---
🧪 Testes e Build
```bash
# Rodar testes
./mvnw test

# Build do JAR
./mvnw clean package -DskipTests
```
O artefato será gerado em `target/taskflowapi-0.0.1-SNAPSHOT.jar`.

👤 Autor
Ivan Revelino  
*GitHub*: @ivanrevelino
