
# Service Request API

A backend application for managing service requests, built with Java 21 and Spring Boot. This project represents a public version of an internal system originally designed for university administrative use.

## ✨ Features

- Submit and manage service requests
- Track status of requests (e.g., pending, in progress, completed)
- Assign requests to handlers or departments
- Role-based access control (admin/user)
- JWT-based authentication
- RESTful endpoints
- (Planned) API documentation with Swagger/OpenAPI

## 🔧 Tech Stack

- Java 21
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Spring Security with JWT
- Gradle
- Docker (optional)
- Lombok
- (Planned) Swagger/OpenAPI for API docs

## 📦 Project Structure

```
src/main/java/com/yourdomain/servicerequest
 ├── controller       # REST controllers
 ├── service          # Business logic
 ├── repository       # JPA repositories
 ├── model            # Entity classes
 ├── dto              # Request/response objects
 ├── config           # Security and app config
 └── exception        # Custom exception handling
```

## 🚀 Getting Started

### Prerequisites

- JDK 21
- PostgreSQL (or modify to use MySQL)
- Gradle

### Setup

1. Clone the repo
```bash
git clone https://github.com/MD-Hamisu/service-request-api.git
cd service-request-api
```

2. Configure your database in `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/yourdb
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
```

3. Run the app
```bash
./gradlew bootRun
```

4. Access API (e.g., via Postman or Swagger, if enabled)
```
http://localhost:8080/api/...
```

## 🔐 Authentication

This API uses JWT for authentication and authorization. After logging in, use the token in your requests:
```
Authorization: Bearer <token>
```

## 📘 To-Do

- [ ] Add Swagger UI documentation
- [ ] Write unit and integration tests
- [ ] Dockerize for easy deployment
- [ ] Include CI/CD setup (GitHub Actions)

## 📎 License

This project is licensed for personal, educational, and portfolio use.

---

**Author:** Mohammed Nura Hamisu  
[LinkedIn](https://linkedin.com/in/mohammed-nura-hamisu-1530b9278)
