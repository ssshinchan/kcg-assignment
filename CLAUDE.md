# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a full-stack assignment management system based on the RuoYi-Vue framework, adapted from the original RuoYi codebase with package rename from `com.ruoyi` to `edu.kcg`. It uses a Spring Boot backend with Vue 3 frontend architecture.

**Tech Stack:**
- Backend: Java 25, Spring Boot 4.0.3, MyBatis 4.0.1, Spring Security, JWT
- Frontend: Vue 3.5, Vite 6.4, Element Plus 2.13, Pinia 3.0
- Database: MySQL with Druid connection pool
- Cache: Redis (Lettuce client)
- Other: Quartz scheduler, PageHelper, SpringDoc (OpenAPI)

## Project Structure

The project is organized as a multi-module Maven project:

- **assignment-admin**: Main application entry point and REST controllers
  - Entry class: `edu.kcg.AssignmentApplication`
  - Controllers organized by category: `system`, `monitor`, `tool`, `common`
  
- **assignment-framework**: Core framework components
  - Security configuration (Spring Security + JWT)
  - Web service layer (login, token, password management)
  - Interceptors, filters, and global exception handling
  - Redis cache integration
  
- **assignment-system**: Business domain layer
  - Service interfaces and implementations
  - MyBatis mappers
  - Domain entities (users, roles, departments, menus, etc.)
  
- **assignment-common**: Shared utilities and common components
  - Annotations (`@Excel`, `@DataScope`, `@RepeatSubmit`, etc.)
  - Base classes (`BaseEntity`, `BaseController`, `TreeEntity`)
  - Utilities (date, file, security, message)
  - Exception hierarchy
  
- **assignment-quartz**: Scheduled task management using Quartz
  
- **assignment-generator**: Code generator for CRUD operations

- **frontend/**: Vue 3 + Vite frontend application
  - Entry: `src/main.js`
  - API layer: `src/api/`
  - Views: `src/views/`
  - State: Pinia stores in `src/store/`

## Configuration

### Environment Variables (.env.local)

The project uses `.env.local` for environment-specific configuration:
- `SERVER_PORT`: Backend port (default: 8080)
- `ASSIGNMENT_PROFILE`: File upload path
- `REDIS_HOST`, `REDIS_PORT`, `REDIS_PASSWORD`: Redis connection
- `TOKEN_SECRET`: JWT secret key
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`: Database connection
- `DRUID_LOGIN_USERNAME`, `DRUID_LOGIN_PASSWORD`: Druid monitoring credentials

### Application Configuration

Main configuration: `assignment-admin/src/main/resources/application.yml`
- References environment variables with `${VAR:default}` syntax
- Druid-specific config: `application-druid.yml`
- MyBatis config: `mybatis/mybatis-config.xml`
- Mapper locations: `classpath*:mapper/**/*Mapper.xml`

## Development Commands

### Backend

```powershell
# Build the project
mvn clean install

# Run backend (from assignment-admin)
cd assignment-admin
mvn spring-boot:run

# Package for deployment
mvn clean package

# Skip tests
mvn clean install -DskipTests
```

The backend runs on port 8080 (configurable via `SERVER_PORT`).

### Frontend

```powershell
cd frontend

# Install dependencies (first time or when package.json changes)
npm install

# Start development server (connects to backend at localhost:8080)
npm run dev

# Build for production
npm run build:prod

# Build for staging
npm run build:stage

# Preview production build
npm run preview
```

Frontend dev server runs on port 80 (configurable in vite.config.js).

### Database Setup

1. Create MySQL database named `assignment`
2. Import schema and initial data from `sql/ry_20260320.sql`
3. If using Quartz scheduler, import `sql/quartz.sql`
4. Update database credentials in `.env.local`

### Redis Setup

Redis must be running before starting the backend:
```powershell
# Start Redis (Windows with Redis installed)
redis-server

# Or via Docker
docker run -d -p 6379:6379 redis
```

## Architecture Patterns

### Security & Authentication

- **JWT-based authentication**: Token stored in Authorization header
- **Token management**: `TokenService` handles creation, parsing, refresh
- **Security filter chain**: `JwtAuthenticationTokenFilter` validates tokens before each request
- **Password encryption**: BCrypt via `SecurityUtils`
- **Anonymous endpoints**: Configured via `PermitAllUrlProperties` and `@Anonymous` annotation

### Data Access Pattern

- **Service layer**: Interface in `assignment-system/service`, implementation in `impl` package
- **Mapper layer**: MyBatis interfaces with `@Mapper` annotation
- **XML mapping**: Corresponding `*Mapper.xml` files in `resources/mapper`
- **Pagination**: PageHelper for automatic pagination (via `startPage()` in controllers)
- **Data scoping**: `@DataScope` annotation for department-level data filtering

### Controller Conventions

- Extend `BaseController` for common methods (`startPage()`, `getResponse()`, etc.)
- Use `AjaxResult` for standardized API responses
- Security annotations: `@PreAuthorize` for permission checks
- Logging annotations: `@Log` for operation logging
- Return patterns: `AjaxResult.success()` / `AjaxResult.error()`

### Frontend-Backend Integration

- Frontend API calls use axios client configured in `src/utils/request.js`
- API endpoints defined in `src/api/*.js` files
- Backend base URL: `http://localhost:8080` (defined in `vite.config.js`)
- Token automatically attached to requests via axios interceptor

## Code Generation

The built-in code generator creates:
- Entity classes
- Mapper interfaces and XML
- Service interface and implementation
- Controller with CRUD operations
- Frontend Vue components and API files

Access at: System Management → Code Generation

## Common Tasks

### Adding a New Entity/Module

1. Create database table
2. Use code generator to scaffold basic CRUD
3. Customize generated service/controller as needed
4. Add menu and permissions via System Management → Menu Management
5. Frontend files generated in separate download, integrate manually

### Adding Custom API Endpoint

1. Add method to appropriate controller in `assignment-admin/src/main/java/edu/kcg/web/controller/`
2. Use `@PreAuthorize` if permission check needed
3. Add corresponding frontend API call in `frontend/src/api/`
4. Call from Vue component via imported API function

### Running Tests

```powershell
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ClassName

# Run with coverage
mvn clean test jacoco:report
```

## Known Patterns

- Package structure follows `edu.kcg.*` namespace (renamed from original `com.ruoyi`)
- Base entities should extend `BaseEntity` for common fields (createTime, updateTime, etc.)
- Tree structures extend `TreeEntity` (e.g., departments, menus)
- Excel export/import uses `@Excel` annotation on domain fields
- XSS filtering configured for specific URL patterns (see `application.yml`)
- File uploads go to path specified in `ASSIGNMENT_PROFILE` environment variable

## API Documentation

When backend is running, access SpringDoc UI at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Monitoring Endpoints

- Druid connection pool monitor: http://localhost:8080/druid/login.html
  - Credentials from `.env.local`: DRUID_LOGIN_USERNAME / DRUID_LOGIN_PASSWORD
- System monitoring available via admin UI under "System Monitoring" menu
