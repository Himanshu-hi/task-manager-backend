# 🚀 Task Manager API

A **Scalable REST API** with JWT Authentication and Role-Based Access Control, built with Spring Boot 3.x.

---

## 📋 Tech Stack

| Layer | Technology |
|-------|-----------|
| Framework | Spring Boot 3.2.0 |
| Language | Java 17 |
| Security | Spring Security + JWT (JJWT) |
| Database | H2 (dev) / PostgreSQL (prod) |
| ORM | Spring Data JPA / Hibernate |
| Docs | SpringDoc OpenAPI 3 (Swagger UI) |
| Build | Maven |

---

## ⚡ Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+
- (Optional) PostgreSQL for production

### Run with H2 (default - zero config)

```bash
cd backend
mvn spring-boot:run
```

The app starts on `http://localhost:8080`

### Run with PostgreSQL

1. Edit `src/main/resources/application.properties`:
   ```properties
   # Comment out H2 lines, uncomment PostgreSQL:
   spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
   spring.datasource.username=postgres
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   ```

2. Create database: `CREATE DATABASE taskmanager;`

3. Run: `mvn spring-boot:run`

---

## 🔐 Default Users (Auto-seeded)

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN + USER |
| demo | demo123 | USER |

---

## 📖 API Documentation

**Swagger UI:** `http://localhost:8080/swagger-ui.html`

**API Docs JSON:** `http://localhost:8080/api-docs`

**H2 Console (dev):** `http://localhost:8080/h2-console`

---

## 🛣️ API Endpoints

### Authentication (`/api/v1/auth`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/auth/register` | Register new user | ❌ Public |
| POST | `/auth/login` | Login & get JWT | ❌ Public |

### Tasks (`/api/v1/tasks`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/tasks` | Create task | ✅ User |
| GET | `/tasks` | Get my tasks (paginated) | ✅ User |
| GET | `/tasks?status=TODO` | Filter by status | ✅ User |
| GET | `/tasks?search=keyword` | Search tasks | ✅ User |
| GET | `/tasks/{id}` | Get task by ID | ✅ User |
| PUT | `/tasks/{id}` | Update task (full) | ✅ Owner/Admin |
| PATCH | `/tasks/{id}/status` | Update status only | ✅ Owner/Admin |
| DELETE | `/tasks/{id}` | Delete task | ✅ Owner/Admin |

### Admin (`/api/v1/admin`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/admin/users` | List all users | 🔒 Admin |
| GET | `/admin/users/{id}` | Get user by ID | 🔒 Admin |
| DELETE | `/admin/users/{id}` | Delete user | 🔒 Admin |
| GET | `/admin/tasks` | All tasks system-wide | 🔒 Admin |
| GET | `/admin/stats` | System statistics | 🔒 Admin |

---

## 🔑 Authentication Flow

```
1. POST /api/v1/auth/register  → Create account
2. POST /api/v1/auth/login     → Receive JWT token
3. Use token in header: Authorization: Bearer <token>
4. All protected endpoints auto-validate the JWT
```

### Sample Requests

**Register:**
```json
POST /api/v1/auth/register
{
  "username": "john",
  "email": "john@example.com",
  "password": "secret123",
  "fullName": "John Doe"
}
```

**Login:**
```json
POST /api/v1/auth/login
{
  "username": "john",
  "password": "secret123"
}
```

**Create Task:**
```json
POST /api/v1/tasks
Authorization: Bearer eyJhbGc...

{
  "title": "Build REST API",
  "description": "Complete the assignment",
  "priority": "HIGH",
  "status": "IN_PROGRESS",
  "dueDate": "2025-01-15T18:00:00"
}
```

---

## 🏗️ Project Structure

```
src/main/java/com/taskmanager/
├── config/          # Security, OpenAPI, DataSeeder
├── controller/      # REST Controllers (v1)
│   ├── AuthController.java
│   ├── TaskController.java
│   └── AdminController.java
├── dto/
│   ├── request/     # Input DTOs with validation
│   └── response/    # Output DTOs
├── entity/          # JPA Entities (User, Task, Role)
├── exception/       # GlobalExceptionHandler + custom exceptions
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, UserDetails, EntryPoint
├── service/         # Service interfaces
│   └── impl/        # Service implementations
└── util/            # JwtUtils
```

---

## 🗄️ Database Schema

```sql
users       → id, username (unique), email (unique), password (hashed), full_name, enabled
roles       → id, name (ROLE_USER | ROLE_ADMIN)
user_roles  → user_id FK, role_id FK  [Many-to-Many]
tasks       → id, title, description, status, priority, due_date, user_id FK, created_at, updated_at
```

---

## 🔒 Security Features

- **BCrypt** password hashing (strength 10)
- **JWT** tokens with configurable expiration (24h default)
- **Stateless** session management
- **Method-level security** via `@PreAuthorize`
- **CORS** configured for frontend origins
- **Input validation** via Jakarta Validation annotations
- **Global exception handler** returning consistent API responses

---

## 📦 Build for Production

```bash
mvn clean package -DskipTests
java -jar target/task-manager-api-1.0.0.jar
```

---

## 🐳 Docker (Optional)

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/task-manager-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
docker build -t task-manager-api .
docker run -p 8080:8080 task-manager-api
```
