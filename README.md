# SaaS Builder AI

SaaS Builder AI is a production-focused full-stack platform for generating, reviewing, and managing AI-assisted software projects. The application combines a React-based workspace experience with a Spring Boot backend, enabling users to collaborate on generated code, review AI output, inspect project context, and manage their account and project lifecycle.

## Features

- AI-assisted project generation and review workflows
- Monaco-powered code editor experience
- Workspace-based file browsing and previewing
- Authentication, profile, and account management
- Project collaboration and version tracking
- Docker-ready backend and frontend services

## AI Capabilities

- AI review modes for generated project analysis
- Workspace-aware project context review
- Code generation and review support through existing backend services

## Workspace Features

- Browse generated files in an interactive workspace
- Inspect AI review results and recommendations
- Preview generated applications in context
- Continue existing review workflows without changing the architecture

## Supported AI Review Modes

- Architecture review
- Code review
- Deployment review
- Quality and production readiness review

## Tech Stack

- Frontend: React, TypeScript, Vite, Monaco Editor
- Backend: Java 17, Spring Boot 3.5, Spring Security, JPA, PostgreSQL
- Containerization: Docker, Docker Compose
- Testing: JUnit 5, Maven, Vitest

## Installation

### Prerequisites

- Java 17+
- Node.js 20+
- Docker Desktop (optional, for containerized setup)
- PostgreSQL 16+ (or use Docker Compose)

### Clone and install

```bash
git clone <repository-url>
cd saasbuilder-ai
```

## Environment Variables

Copy the example environment file and update values before running locally:

```bash
cp .env.example .env
```

Required variables:

- DB_URL: PostgreSQL JDBC URL
- DB_USERNAME: PostgreSQL username
- DB_PASSWORD: PostgreSQL password
- JWT_SECRET: strong JWT signing secret
- JWT_EXPIRATION_HOURS: JWT lifetime in hours
- JWT_REFRESH_EXPIRATION_DAYS: refresh token lifetime in days
- OPENROUTER_API_KEY: optional AI provider API key
- OPENROUTER_MODEL: optional AI model name
- OPENROUTER_ENDPOINT: optional AI provider endpoint
- OPENROUTER_TIMEOUT_SECONDS: optional timeout in seconds
- CORS_ALLOWED_ORIGINS: allowed frontend origins
- SPRING_PROFILES_ACTIVE: Spring profile to use

## Running Locally

### Backend

```bash
cd backend
./mvnw clean spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Docker Setup

From the project root:

```bash
docker compose up --build
```

The stack includes:

- PostgreSQL database
- Spring Boot backend on port 8080
- React frontend on port 3000

## Build Instructions

### Backend

```bash
cd backend
./mvnw clean package
```

### Frontend

```bash
cd frontend
npm run build
```

## Testing

### Backend

```bash
cd backend
./mvnw clean test
```

### Frontend

```bash
cd frontend
npm run build
```

## Deployment

This project is prepared for container-based deployment and CI pipelines. For production deployments, configure secrets through your host platform or secret manager and provide a valid HTTPS certificate and environment variables.

## Architecture

The project is organized around the existing backend and frontend modules:

- Backend services manage authentication, project data, collaboration, and AI workflows
- Frontend components provide the workspace, editor, review panels, and preview experience
- Docker Compose orchestrates the app stack for local development and release readiness

## Screenshots

Placeholder for screenshots and demo assets.

## Future Improvements

- Add richer deployment automation and observability
- Expand automated end-to-end tests
- Introduce staged environments for staging and production
