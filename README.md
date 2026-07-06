# Tlias Web Management

> Tlias Intelligent Learning Assistance System - Backend Service
>
> An enterprise-grade employee and student management platform built with Spring Boot.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Architecture](#architecture)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [API Documentation](#api-documentation)
- [Development Guide](#development-guide)
- [License](#license)

---

## Project Overview

Tlias Web Management is a comprehensive management system for the education and training industry. It provides core features such as employee management, department management, class management, and student management, along with extended capabilities like statistical reports, file uploads, operation logging, and RBAC permission control.

---

## Tech Stack

| Category | Technology | Version |
|----------|-----------|---------|
| Backend Framework | Spring Boot | 4.0.6 |
| ORM Framework | MyBatis | 4.0.1 |
| Database | MySQL | 8.x |
| Cache | Redis | 7.x |
| Message Queue | RabbitMQ | 3.x |
| Search Engine | Elasticsearch | 8.12.0 |
| Pagination | PageHelper | 1.4.7 |
| Authentication | JWT (jjwt) | 0.9.1 |
| File Storage | Alibaba Cloud OSS | 0.4.0 |
| AOP | Spring AOP | 3.1.1 |
| API Documentation | Knife4j (OpenAPI 3) | 4.5.0 |
| Utility Library | Lombok | - |
| Java Version | JDK | 17 |
| Containerization | Docker Compose | - |

---

## Features

### 1. Authentication & Authorization
- User login with BCrypt password verification
- Dual-token mechanism (accessToken + refreshToken)
- Multi-device login support
- Active logout with token revocation
- Scheduled cleanup of expired tokens
- RBAC (Role-Based Access Control) permission system
- Permission-based interface control via `@PreAuthorize` annotation
- Redis caching for permissions and roles

### 2. Department Management
- Department list query (tree structure)
- Add, update, and delete departments
- Get department details by ID

### 3. Employee Management
- Paginated conditional query for employees
- Add, update, and batch delete employees
- Get employee details by ID
- Employee work experience management

### 4. Class Management
- Paginated conditional query for classes
- Add, update, and delete classes
- Get class details by ID
- Get all classes list

### 5. Student Management
- Paginated conditional query for students (name, degree, class filter)
- Add, update, and batch delete students
- Get student details by ID
- Student violation point deduction

### 6. Reports & Statistics
- Employee position count statistics
- Employee gender ratio statistics
- Student degree distribution statistics
- Class student count statistics

### 7. File Management
- File upload based on Alibaba Cloud OSS
- File type validation (images, documents)
- File size limits (image: 5MB, document: 20MB)
- File deletion support
- Permission-controlled upload/delete operations

### 8. Operation Logs
- Auto-logging via AOP aspect
- Log persistence to MySQL (async via RabbitMQ)
- Full-text search with Elasticsearch
- Enhanced log fields: IP, request method, URL, result status, error message
- Paginated log query with filters

### 9. Frontend Log Reporting
- Frontend error/performance/behavior log collection
- Dedicated `frontend_log` table
- Paginated query with filters

### 10. Data Dictionary
- Dictionary data management by type
- Common dictionaries: employee positions, student degrees, gender
- Public API for dictionary lookup

### 11. API Documentation
- Auto-generated OpenAPI 3 documentation via Knife4j
- Bearer token authentication support
- Grouped by module (11 controller groups)
- Accessible at: `http://localhost:8080/doc.html`

---

## Architecture

### High-Level Architecture

```
                    ┌─────────────────────┐
                    │     Frontend UI     │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │  Spring Boot (API)   │
                    └──────────┬──────────┘
                               │
          ┌────────────────────┼────────────────────┐
          │                    │                    │
 ┌────────▼──────┐  ┌──────────▼────────┐  ┌──────▼────────┐
 │  TokenFilter  │  │  PermissionAspect │  │  Log Aspect   │
 │  JWT verify   │  │  RBAC check       │  │  Record ops   │
 └────────┬──────┘  └──────────┬────────┘  └──────┬────────┘
          │                    │                    │
          ▼                    ▼                    ▼
 ┌─────────────────────────────────────────────────────────┐
 │                    Service Layer                        │
 └─────┬───────────┬──────────────┬──────────────┬─────────┘
       │           │              │              │
       ▼           ▼              ▼              ▼
   DeptService  EmpService    ClazzService   StudentService
       │           │              │              │
       └───────────┴──────┬───────┴──────────────┘
                          │
                          ▼
                    ┌─────────────┐
                    │   MyBatis   │
                    └──────┬──────┘
                           │
         ┌─────────────────┼─────────────────┐
         ▼                 ▼                 ▼
    ┌─────────┐       ┌─────────┐       ┌─────────┐
    │  MySQL  │       │  Redis  │       │ RabbitMQ│
    │ (Main)  │       │ (Cache) │       │  (Async)│
    └─────────┘       └─────────┘       └────┬────┘
                                             │
                                             ▼
                                        ┌───────────┐
                                        │     ES    │
                                        │ (Search)  │
                                        └───────────┘
```

### Authentication Flow

```
Login → Generate accessToken (2h) + refreshToken (7d)
      → Store refreshToken in DB
      → Return both tokens + user info + roles + permissions

Request → TokenFilter validates accessToken
        → Load permissions from Redis (or DB if miss)
        → Store in ThreadLocal (PermissionHolder)
        → @PreAuthorize aspect checks permission
        → Execute business logic
        → Clear ThreadLocal
```

---

## Project Structure

```
src/main/java/org/example/
├── anno/                  # Custom annotations
│   ├── Log.java           # Operation log annotation
│   └── PreAuthorize.java  # Permission check annotation
├── aspect/                # AOP aspects
│   ├── OperationLogAspect.java
│   └── PermissionAspect.java
├── config/                # Configuration classes
│   ├── Knife4jConfig.java
│   ├── RabbitMQConfig.java
│   ├── RedisConfig.java
│   └── WebConfig.java
├── controller/            # Controller layer (11 controllers)
│   ├── AuthController.java
│   ├── ClazzController.java
│   ├── DeptController.java
│   ├── DictController.java
│   ├── EmpController.java
│   ├── LogController.java
│   ├── LoginController.java
│   ├── ReportController.java
│   ├── StudentController.java
│   ├── UploadController.java
│   └── UserController.java
├── exception/             # Exception handling
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── filter/                # Servlet filters
│   └── TokenFilter.java
├── interceptor/           # Spring interceptors
│   └── TokenInterceptor.java
├── mapper/                # Data access layer
│   ├── ClazzMapper.java
│   ├── DeptMapper.java
│   ├── DictDataMapper.java
│   ├── EmpExprMapper.java
│   ├── EmpLogMapper.java
│   ├── EmpRoleMapper.java
│   ├── EmpMapper.java
│   ├── FrontendLogMapper.java
│   ├── OperateLogMapper.java
│   ├── PermissionMapper.java
│   ├── RefreshTokenMapper.java
│   └── StudentMapper.java
├── pojo/                  # Entity classes & DTOs
│   ├── entity/            # Database entities
│   ├── param/             # Query parameters
│   ├── dto/               # Data transfer objects
│   ├── vo/                # View objects
│   └── ...
├── service/               # Business logic layer
│   ├── impl/              # Service implementations
│   │   ├── AuthServiceImpl.java
│   │   ├── PermissionServiceImpl.java
│   │   ├── RoleServiceImpl.java
│   │   ├── LogConsumer.java
│   │   └── ...
│   ├── AuthService.java
│   ├── LogProducer.java
│   ├── LogBatchWriter.java
│   ├── PermissionService.java
│   ├── RoleService.java
│   └── ...
├── task/                  # Scheduled tasks
│   └── RefreshTokenCleanupTask.java
├── utils/                 # Utility classes
│   ├── AliyunOSSOperator.java
│   ├── AliyunOSSProperties.java
│   ├── CurrentHolder.java
│   ├── IpUtils.java
│   ├── JwtUtils.java
│   ├── PermissionHolder.java
│   └── RedisUtil.java
└── TliasWebManagementApplication.java  # Main class
```

---

## Quick Start

### Prerequisites

- JDK 17+
- Maven 3.6+
- Docker & Docker Compose (recommended for local dev)

### Option 1: Docker Compose (Recommended)

Start all infrastructure services with one command:

```bash
# Clone the repository
git clone <repository-url>
cd Tlias-web-management

# Start MySQL, Redis, RabbitMQ, Elasticsearch
docker-compose up -d

# Check service status
docker-compose ps
```

Services will be available at:
| Service | Port | Management UI |
|---------|------|--------------|
| MySQL | 3306 | - |
| Redis | 6379 | - |
| RabbitMQ | 5672 | http://localhost:15672 (admin/admin) |
| Elasticsearch | 9200 | - |

### Option 2: Manual Setup

Install and configure each service individually:
- MySQL 8.x on port 3306
- Redis on port 6379
- RabbitMQ on port 5672 (management on 15672)
- Elasticsearch 8.12.0 on port 9200

### Database Initialization

1. Create database: `tlias`
2. Execute initialization scripts in order:
   - `src/main/resources/db/init-all.sql` - Create all tables and seed data
   - `src/main/resources/db/alter-operate-log.sql` - Enhance operate_log table (optional)

### Configuration

Update `src/main/resources/application.yml` or use `application-local.yml` for local overrides:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/tlias
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  elasticsearch:
    uris: http://localhost:9200

aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    bucketName: your-bucket-name
    region: cn-hangzhou
```

### Run the Application

```bash
# Build
mvn clean package -DskipTests

# Run
mvn spring-boot:run
```

The application will start at `http://localhost:8080`

### Verify Installation

- **API Docs**: http://localhost:8080/doc.html
- **Health Check**: Access any public endpoint (e.g., `/login`)
- **RabbitMQ Console**: http://localhost:15672 (admin/admin)

---

## API Documentation

Full interactive API documentation is available via Knife4j at:
- **URL**: `http://localhost:8080/doc.html`
- **Format**: OpenAPI 3.0
- **Authentication**: Bearer token (login first to get token)

### API Summary

| Module | Endpoints | Permissions Required |
|--------|-----------|---------------------|
| Authentication | `/login`, `/auth/refresh`, `/auth/logout` | Public (login/logout) |
| User Info | `/user/info` | Authenticated |
| Department | `/depts` (GET/POST/PUT/DELETE) | `system:dept:*` |
| Employee | `/emps` (GET/POST/PUT/DELETE) | `system:emp:*` |
| Class | `/clazzs` (GET/POST/PUT/DELETE) | `stu:clazz:*` |
| Student | `/students` (GET/POST/PUT/DELETE) | `stu:stu:*` |
| Reports | `/report/*` | `report:*:view` |
| Files | `/files/upload`, `/files` (DELETE) | `system:file:*` |
| Dictionary | `/dicts`, `/dicts/all` | Public |
| Logs | `/log/operate/page`, `/log/frontend/page` | `report:log:view` |

### Default Response Format

All endpoints return a unified `Result` structure:

```json
{
  "code": 1,
  "msg": "success",
  "data": {}
}
```

- `code`: Status code (1 = success, 0 = failure)
- `msg`: Response message
- `data`: Response payload

---

## Development Guide

### Code Conventions

- **Layered architecture**: Controller → Service → Mapper
- **Entity separation**: Distinguish PO (persistent object), DTO, VO, BO
- **Pagination**: Use PageHelper plugin for all list queries
- **Exception handling**: Global exception handler with `@RestControllerAdvice`
- **Logging**: Use `@Log` annotation for important operations
- **Permission control**: Use `@PreAuthorize("perm:code")` on controller methods
- **Constants**: Extract magic numbers and strings to constant classes or enums
- **Comments**: JavaDoc for all public classes and methods

### Caching Strategy

| Cache Key | Content | TTL | Invalidated On |
|-----------|---------|-----|----------------|
| `perms:{empId}` | Permission code list | 30 min | Employee update/delete |
| `roles:{empId}` | Role list | 30 min | Employee update/delete |

### Message Queue Architecture

```
Operation Aspect → Direct Exchange → operate_log_queue → Consumer (batch write to MySQL + ES)
Operation Aspect → Direct Exchange → frontend_log_queue → Consumer (batch write to MySQL + ES)
```

- **Batch size**: 10 messages or 5 second timeout
- **Dead letter queue**: Configured for failed messages
- **Message format**: JSON (Jackson serialization)

### Docker Compose Commands

```bash
# Start all services
docker-compose up -d

# Stop all services (keep data)
docker-compose down

# Stop and remove all data
docker-compose down -v

# View logs
docker-compose logs -f

# Restart a specific service
docker-compose restart mysql
```

---

## License

This project is for internal training and educational purposes.
