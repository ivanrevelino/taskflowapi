# TaskFlowAPI

REST API para gerenciar projetos e tarefas com autenticação JWT e controle de acesso baseado em papéis.

## Requisitos

- Java 21+
- PostgreSQL 12+
- Maven 3.6+

## Setup

### 1. Clone o repositório

```bash
git clone https://github.com/ivanrevelino/taskflowapi.git
cd taskflowapi
```

### 2. Configure as variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto:

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

## Stack

- Java 21
- Spring Boot 4.1.0
- Spring Security e JWT
- Spring Data JPA
- PostgreSQL
- MapStruct 1.6.0
- Lombok
- SpringDoc OpenAPI 3.0.3

## Autenticação

Todos os endpoints protegidos exigem um JWT Bearer Token no header `Authorization`:

```
Authorization: Bearer YOUR_JWT_TOKEN
```

### Login e Registro

**Registro:**
```bash
POST /auth/register
Content-Type: application/json

{
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
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "..."
}
```

## Endpoints

### Autenticação
- `POST /auth/login` - Login de usuário
- `POST /auth/register` - Registro de novo usuário
- `POST /auth/refresh` - Renovar token JWT

### Projetos
- `GET /projects/me` - Listar projetos do usuário
- `GET /projects/me/owned` - Listar projetos possuídos pelo usuário
- `GET /projects/{id}` - Obter detalhes de um projeto
- `POST /projects` - Criar novo projeto
- `PUT /projects/{id}` - Atualizar projeto
- `DELETE /projects/{id}` - Deletar projeto

### Tarefas
- `GET /projects/{projectId}/tasks` - Listar tarefas
- `GET /projects/{projectId}/tasks/filter?status=TO_DO` - Filtrar tarefas por status
- `POST /projects/{projectId}/tasks` - Criar tarefa
- `PUT /projects/{projectId}/tasks/{taskId}` - Atualizar tarefa
- `PATCH /projects/{projectId}/tasks/{taskId}/complete` - Marcar como completa
- `PATCH /projects/{projectId}/tasks/{taskId}/claim` - Atribuir tarefa a si mesmo
- `PATCH /projects/{projectId}/tasks/{taskId}/release` - Liberar tarefa
- `DELETE /projects/{projectId}/tasks/{taskId}` - Deletar tarefa

### Comentários em Tarefas
- `GET /projects/{projectId}/tasks/{taskId}/comments` - Listar comentários
- `POST /projects/{projectId}/tasks/{taskId}/comments` - Criar comentário
- `POST /projects/{projectId}/tasks/{taskId}/comments/{commentId}/reply` - Responder comentário
- `PUT /projects/{projectId}/tasks/{taskId}/comments/{commentId}` - Atualizar comentário
- `DELETE /projects/{projectId}/tasks/{taskId}/comments/{commentId}` - Deletar comentário

### Membros de Projeto
- `GET /projects/{projectId}/members` - Listar membros
- `POST /projects/{projectId}/members` - Adicionar membro
- `DELETE /projects/{projectId}/members/{memberId}` - Remover membro
- `DELETE /projects/{projectId}/members/leave` - Sair do projeto

### Usuários
- `GET /users/me` - Obter dados do usuário autenticado
- `GET /users/search?username=...` - Buscar usuários
- `PATCH /users/me/update/password` - Atualizar senha
- `PATCH /users/me/update/username` - Atualizar username

## Status de Tarefas

- `TO_DO` - A fazer
- `IN_PROGRESS` - Em progresso
- `COMPLETED` - Completa

## Papéis

- `USER` - Usuário comum
- `ADMIN` - Acesso administrativo

## Testes

```bash
./mvnw test
```

## Build

```bash
./mvnw clean package -DskipTests
```

Gera JAR em `target/taskflowapi-0.0.1-SNAPSHOT.jar`

## Documentação

Para mais informações sobre todos os endpoints, parâmetros e respostas, acesse a documentação do Swagger da aplicação em execução:

```
http://localhost:8080/swagger-ui.html
```

A documentação OpenAPI também está disponível em:

```
http://localhost:8080/v3/api-docs
```

## Contato

Para dúvidas ou sugestões, entre em contato através de:

- Email: ivanmbilane@gmail.com
- GitHub: [@ivanrevelino](https://github.com/ivanrevelino)
