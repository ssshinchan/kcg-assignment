# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

Assignment is a RuoYi-derived, full-stack management application. The backend is a Java 25, Spring Boot 4 multi-module Maven build; the frontend is a separate Vue 3 + Vite application in `frontend/`. The UI text and several configuration comments are Japanese/Chinese.

## Commands

### Backend

Run from the repository root unless noted otherwise:

```powershell
# Compile, test, and install every Maven module
mvn clean install

# Compile/package all modules without tests
mvn clean package -DskipTests

# Run all backend tests
mvn test

# Run one test class (or one test method)
mvn test -Dtest=ClassName
mvn test -Dtest=ClassName#methodName

# Start the application (run from assignment-admin)
cd assignment-admin
mvn spring-boot:run
```

The application starts on port 8080 by default. It requires MySQL and Redis. Runtime values are supplied by the root `.env.local`; Spring configuration uses environment-variable placeholders in `assignment-admin/src/main/resources/application.yml` and `application-druid.yml`.

### Frontend

```powershell
cd frontend
npm install
npm run dev
npm run build:prod
npm run build:stage
npm run preview
```

`frontend/package.json` defines no lint or test scripts. Development Vite serves on port 80, proxies `/dev-api` to the backend at port 8080, and uses `VITE_APP_BASE_API=/dev-api`. Production API configuration is in `frontend/.env.production`.

## Backend Architecture

The root Maven reactor contains these layers:

- `assignment-admin` is the executable Spring Boot application and the conventional REST-controller layer. `AssignmentApplication` is the entry point. It depends on the framework, Quartz, generator, and AI modules.
- `assignment-framework` owns cross-cutting web behavior: Spring Security, JWT authentication, CORS, Druid/MyBatis wiring, Redis-backed login state, AOP concerns, and global exception handling. It depends on the system layer.
- `assignment-system` is the core domain/data layer. It contains domain objects plus MyBatis mapper interfaces/XML and service interfaces/implementations; it depends on `assignment-common`.
- `assignment-common` holds shared result/base types, annotations, utilities, constants, exceptions, and Redis support.
- `assignment-quartz` implements scheduled-job management; `assignment-generator` provides CRUD generation; `assignment-ai` contains the LLM chat domain, controller, persistence, and LangChain4j provider integration.

Typical backend flow is controller → service interface/implementation → MyBatis mapper → mapper XML/DB. Controllers normally extend `BaseController`, use `AjaxResult` responses, and apply `@PreAuthorize` for permissions and `@Log` for audited operations. Use `startPage()` before list service calls when endpoint pagination is required.

Security is stateless. `SecurityConfig` permits login, registration, captcha, static resources, Swagger, Druid, and URLs registered through `PermitAllUrlProperties`; other requests require authentication. `JwtAuthenticationTokenFilter` resolves the Bearer token through `TokenService`, whose login session state is stored in Redis. Do not assume a JWT alone is sufficient: the corresponding Redis login record is required.

MyBatis scans `edu.kcg.**.domain` aliases and `classpath*:mapper/**/*Mapper.xml`. The active `druid` profile configures MySQL via `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD`; Redis uses `REDIS_HOST`, `REDIS_PORT`, and `REDIS_PASSWORD`.

## Frontend Architecture

The frontend is JavaScript Vue 3 with Element Plus and Pinia. `src/utils/request.js` is the shared Axios client: it adds the Bearer token, applies duplicate-submission protection, and normalizes the backend's `AjaxResult` response codes. Put ordinary backend calls under `src/api/` and consume them from views/components rather than creating ad-hoc Axios clients.

Routing combines static routes in `src/router/index.js` with server-driven menu routes. After authentication, the Pinia permission store calls `/getRouters`, maps component names from the backend to `src/views/**/*.vue` via `import.meta.glob`, and registers authorized routes. Therefore, a new navigable view requires both the Vue view/API integration and matching backend menu/permission configuration; merely adding a route file does not make it appear in the sidebar.

## AI Chat

`assignment-ai` uses LangChain4j and is packaged into the admin application. Its `AiChatController` exposes authenticated conversation CRUD under `/ai/chat/conversations` and streams replies from `/ai/chat/stream` as SSE. The Vue API wrapper is `frontend/src/api/ai/chat.js`; the chat view is `frontend/src/views/ai/chat.vue`.

AI provider settings are under `ai.model` in `application.yml`: the selected provider, API key from `API_KEY`, model name, and generation settings. The chat endpoints use the authenticated user ID, and the service is responsible for conversation ownership checks.

## Operational Endpoints

When the backend is running, SpringDoc is served at `/swagger-ui.html` and `/v3/api-docs`; Druid monitoring is at `/druid/` using the configured Druid credentials.
