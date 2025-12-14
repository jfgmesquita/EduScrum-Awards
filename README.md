
-----

# EduScrum Awards - Backend API

   

**EduScrum Awards** is a gamification platform designed to support Scrum methodology teaching in academic environments. This API manages users (Students, Teachers, Admins), degrees, courses, projects, teams, sprints, tasks, and a complex awarding/ranking system.

This repository contains the **Backend RESTful API**.

### 🔗 Related Repositories

  * **Frontend Application:** [EduScrum-Frontend](https://github.com/devyssonsc/Edu-Scrum-Frontend)

-----

## 🛠 Tech Stack

The architecture follows a strict layered MVC pattern (Controller, Service, Repository).

  * **Core:** Java 21 LTS, Spring Boot
  * **Build Tool:** Maven
  * **Database:** MySQL
  * **Security:** Spring Security & JWT (Stateless Authentication)
  * **Testing:** JUnit 5, Mockito, JaCoCo & JMeter

-----

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed:

  * Java JDK 17+
  * Apache Maven
  * MySQL Server (running on port 3306)

### 1. Database Configuration

For security reasons, the `application-dev.properties` file is included in `.gitignore`. You must configure your local database credentials before running the app.

1.  Navigate to `src/main/resources/`.
2.  Create and edit the file `application-dev.properties`.
3.  Add your MySQL credentials as shown below:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/eduscrum_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=YOUR_MYSQL_USER
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Hibernate (Use 'update' to keep data, 'create' to reset)
spring.jpa.hibernate.ddl-auto=update

# JWT Secret Key (Must be a strong Base64 string)
jwt.secret.key=UkruTJWQ3xSe5UV/e/wtlRKBYued5t33FZujm7ovadgtHJdv8cxRxMR0MDQTOvmVqWboB1BtxuWZRQq77qkJWw==
```

### 2. Installation

Install dependencies and run unit tests:

```bash
mvn clean install
```

### 3. Running the Application

To run the server using the **development profile**:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```


The API will be available at `http://localhost:8080`.

### 4. Default Credentials

The system initializes with a default Admin account:

  * **Email:** `admintest@eduscrum.com`
  * **Password:** `admin123`

-----

## 🔒 Security & JWT

This API uses **Stateless Authentication** via JSON Web Tokens (JWT).

  * The server does not store sessions in memory.
  * Upon login (`/api/v1/auth/login`), the server returns an `access_token`.
  * All subsequent requests to protected endpoints must include the token in the **Authorization Header**:
    ```
    Authorization: Bearer <YOUR_TOKEN_HERE>
    ```
  * **IDOR Protection:** The system implements strict checks to ensure users (Students) can only access their own private data (e.g., Dashboards).

-----

## 🧪 Testing Strategy

A comprehensive testing strategy was implemented to ensure quality and performance:

  * **Unit Testing:** Used **JUnit 5** and **Mockito** to test `Services` and `Controllers` in isolation, ensuring business logic correctness.
  * **Code Coverage:** Analyzed using **JaCoCo** to identify dead code and ensure critical paths are covered.
  * **Integration Testing:** **Postman** was used extensively to simulate the Frontend, validating the full HTTP request flow and security filters.
  * **Performance Testing:** **Apache JMeter** was used to stress-test the application (e.g., 500 concurrent users accessing Student Dashboards), achieving an average response time of \~21ms.

-----