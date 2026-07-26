# TaskFlowAPI

Uma API REST para gerenciar projetos e tarefas com autenticação JWT e controle de acesso baseado em papéis.

## 🛠️ Stack Tecnológico

- **Java 21**
- **Spring Boot 4.1.0**
- **Spring Data JPA**
- **Spring Security**
- **JWT (java-jwt 4.6.0)**
- **PostgreSQL**
- **MapStruct 1.6.0 Beta1**
- **Lombok**
- **SpringDoc OpenAPI 3.0.3**

## 📋 Pré-requisitos

- Java 21+
- PostgreSQL 12+
- Maven 3.6+

## 🚀 Como executar

### 1. Clone o repositório

```bash
git clone https://github.com/ivanrevelino/taskflowapi.git
cd taskflowapi
```

### 2. Configure as variáveis de ambiente

```bash
cp .env.example .env
```

Edite o arquivo `.env`:

```env
DB_URL=jdbc:postgresql://localhost:5432/taskflow_db
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha

JWT_SECRET=sua_chave_secreta_muito_longa_aqui
SERVER_PORT=8080
```

### 3. Crie o banco de dados

```sql
CREATE DATABASE taskflow_db;
```

### 4. Execute a aplicação

```bash
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`

## 📚 Documentação

Acesse o Swagger UI em:

```
http://localhost:8080/swagger-ui.html
```

## 🔐 Autenticação

Todos os endpoints protegidos exigem um JWT Bearer Token no header:

```
Authorization: Bearer YOUR_JWT_TOKEN
```

### Endpoints de Autenticação

**Registro:**
```bash
POST /auth/register
Content-Type: application/json

{
  "name": "João",
  "username": "joao",
  "password": "senha123"
}
```

**Login:**
```bash
POST /auth/login
Content-Type: application/json

{
  "username": "joao",
  "password": "senha123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 📌 Endpoints Principais

### Projects

```
GET    /project              - Lista seus projetos
POST   /project              - Criar novo projeto
GET    /project/{id}         - Obter detalhes do projeto
DELETE /project/{id}         - Deletar projeto
```

### Tasks

```
GET    /projects/{projectId}/tasks                    - Lista tarefas
POST   /projects/{projectId}/tasks                    - Criar tarefa
PATCH  /projects/{projectId}/tasks/{taskId}           - Marcar tarefa como completa
GET    /projects/{projectId}/tasks/filter?status=...  - Filtrar tarefas por status
DELETE /projects/{projectId}/tasks/{taskId}           - Deletar tarefa
```

### Users (Admin apenas)

```
GET    /users                  - Listar todos os usuários (paginado)
POST   /users                  - Criar novo usuário
GET    /users/{id}             - Obter usuário por ID
GET    /users/findBy?name=...  - Buscar usuário por nome
DELETE /users/{id}             - Deletar usuário
```

## 📊 Status das Tarefas

- `TO_DO` - A fazer
- `IN_PROGRESS` - Em progresso
- `COMPLETED` - Completa

## 👥 Roles de Usuário

- `ADMIN` - Acesso total aos usuários
- `USER` - Usuário comum

## 🏗️ Estrutura do Projeto

```
src/main/java/com/ivan/taskflowapi/
├── controller/          REST Controllers
├── service/             Lógica de negócio
├── models/              Entidades JPA
├── dto/                 Data Transfer Objects
├── repository/          Data Access Layer
├── security/            Configuração JWT
├── mapper/              MapStruct Mappers
├── exception/           Exceções customizadas
└── infra/              Utilitários
```

## 🧪 Testes

```bash
./mvnw test
```

## 📦 Build

```bash
./mvnw clean package -DskipTests
```

Gera JAR em `target/taskflowapi-0.0.1-SNAPSHOT.jar`

## 📝 Exemplos de Uso

**Criar um projeto:**
```bash
curl -X POST http://localhost:8080/project \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "name": "Novo Projeto",
    "description": "Descrição do projeto"
  }'
```

**Criar uma tarefa:**
```bash
curl -X POST http://localhost:8080/projects/1/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "title": "Tarefa 1",
    "description": "Descrição da tarefa"
  }'
```

**Filtrar tarefas por status:**
```bash
curl http://localhost:8080/projects/1/tasks/filter?status=TO_DO \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## 👤 Autor

Ivan Revelino - [@ivanrevelino](https://github.com/ivanrevelino)
