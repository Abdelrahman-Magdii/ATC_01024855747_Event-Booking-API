# Event Booking System

A full-stack event booking system built with Spring Boot that allows users to browse and book events, manage their bookings, and provides an integrated web-based admin panel for event management.

---
## ERD 
![ERD](eventbookingERD.png)
---
## Features

### Core Features
- **User Authentication & Authorization**
    - JWT token-based authentication
    - Role-based access control (Admin, Organizer, User)
    - User registration

- **Event Management**
    - Create, read, update, and delete events
    - Event details include title, description, date, time, location, capacity, and price
    - Published/unpublished event status

- **Booking System**
    - Book events with multiple tickets
    - View booking history
    - Cancel bookings
    - Booking confirmation notifications

- **Admin Panel**
    - Manage users, events, and bookings
    - View statistics and reports
    - Approve/reject event submissions

### Optional Enhancements
- **Role-Based Permissions** - Different access levels for admin, organizer, and regular users
- **Categories & Tags** - Organize events with categories and flexible tagging
- **Image Upload** - Upload and store event images
- **Pagination** - Efficient data loading for events and bookings lists
- **Responsive Design** - Mobile-friendly UI

## Tech Stack

- **Backend**: Spring Boot 3.2.x, Java 17
- **Database**: MySQL/PostgreSQL
- **Security**: Spring Security with JWT
- **API Documentation**: OpenAPI (Swagger)
- **Testing**: JUnit 5 with MockMvc
- **Build Tool**: Maven

## Project Structure

```
src/
├── main/
│   ├── java/com/eventbooking/
│   │   ├── config/              # Configuration classes
│   │   ├── controller/          # REST controllers
│   │   ├── dto/                 # Data Transfer Objects
│   │   ├── exception/           # Custom exceptions and handler
│   │   ├── entity/               # Entity classes
│   │   ├── enums/               # All Enum
│   │   ├── repository/          # Spring Data JPA repositories
│   │   ├── security/            # Security configurations and JWT
│   │   ├── service/             # Business logic
│   │   └── EventBookingApplication.java
│   │
│   └── resources/
│       ├── application.properties
│       ├── application-prod.properties
│       ├── db/migration/         # Database migrations
│       └── static/               # Static resources
│
└── test/
    └── java/com/eventbooking/
        ├── controller/           # Controller tests
        ├── repository/           # Repository tests
        └── service/              # Service tests
```

# Event Booking API Documentation

 [![Postman](https://assets.getpostman.com/common-share/postman-logo-horizontal-320x132.png)](https://documenter.getpostman.com/view/36966051/2sB2jAbTvh) | [![Swagger](https://static1.smartbear.co/swagger/media/assets/images/swagger_logo.svg)](https://event-booking-system-f79cebebc9ce.herokuapp.com/swagger-ui/swagger-ui/index.html#/) 
----------------------------------------------------------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 [View Postman Collection](https://documenter.getpostman.com/view/36966051/2sB2jAbTvh)                                                                    | [View Swagger UI](https://event-booking-system-f79cebebc9ce.herokuapp.com/swagger-ui/swagger-ui/index.html#/)                                                                       

A comprehensive REST API for event management and booking system with user authentication, authorization, and
administrative features.

## Table of Contents

- [API Overview](#api-overview)
- [Authentication](#authentication)
- [Endpoints](#endpoints)
  - [Auth Controller](#auth-controller)
  - [Admin Controller](#admin-controller)
  - [Booking Controller](#booking-controller)
  - [Category Controller](#category-controller)
  - [Event Controller](#event-controller)
- [Response Formats](#response-formats)
- [Error Handling](#error-handling)
- [Rate Limiting](#rate-limiting)
- [Examples](#examples)

## API Overview

**Base URL**: `https://event-booking-system-f79cebebc9ce.herokuapp.com/`

**Content-Type**: `application/json`

## Authentication

The API uses JWT (JSON Web Tokens) for authentication. Include the token in the `Authorization` header for protected
endpoints.

```http
Authorization: Bearer your.jwt.token.here
```

## Endpoints

### Auth Controller

| Endpoint                   | Method | Description               | Required Role |
|----------------------------|--------|---------------------------|---------------|
| `/api/auth/register`       | POST   | Register a new user       | None          |
| `/api/auth/register/admin` | POST   | Register a new admin user | None          |
| `/api/auth/login`          | POST   | Authenticate user         | None          |

#### Register User

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user123",
  "email": "user@example.com",
  "password": "securePassword123!"
}
```

#### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123!"
}
```

### Admin Controller

| Endpoint                         | Method | Description             | Required Role |
|----------------------------------|--------|-------------------------|---------------|
| `/api/admin/users`               | GET    | Get all users           | ADMIN         |
| `/api/admin/users/{id}/role`     | PUT    | Update user role        | ADMIN         |
| `/api/admin/events`              | GET    | Get all events          | ADMIN         |
| `/api/admin/events/{id}/publish` | PUT    | Publish/unpublish event | ADMIN         |
| `/api/admin/bookings`            | GET    | Get all bookings        | ADMIN         |
| `/api/admin/stats`               | GET    | Get system statistics   | ADMIN         |

#### Example: Update User Role

```http
PUT /api/admin/users/123/role
Content-Type: application/json
Authorization: Bearer admin.jwt.token.here

{
  "name": "MODERATOR",
  "description": "Event Moderator"
}
```

### Booking Controller

| Endpoint                         | Method | Description            | Required Role |
|----------------------------------|--------|------------------------|---------------|
| `/api/bookings`                  | POST   | Create new booking     | AUTHENTICATED |
| `/api/bookings/{id}`             | GET    | Get booking by ID      | AUTHENTICATED |
| `/api/bookings/{id}`             | DELETE | Delete booking         | AUTHENTICATED |
| `/api/bookings/{id}/cancel`      | PUT    | Cancel booking         | AUTHENTICATED |
| `/api/bookings/user`             | GET    | Get user's bookings    | AUTHENTICATED |
| `/api/bookings/events/{eventId}` | GET    | Get bookings for event | AUTHENTICATED |

#### Example: Create Booking

```http
POST /api/bookings
Content-Type: application/json
Authorization: Bearer user.jwt.token.here

{
  "eventId": 456,
  "attendeeName": "John Doe",
  "attendeeEmail": "john@example.com",
  "additionalNotes": "Vegetarian meal preference"
}
```

### Category Controller

| Endpoint                      | Method | Description             | Required Role |
|-------------------------------|--------|-------------------------|---------------|
| `/api/categories`             | GET    | Get all categories      | ADMIN         |
| `/api/categories/{id}`        | GET    | Get category by ID      | ADMIN         |
| `/api/categories/{id}/events` | GET    | Get events for category | ADMIN         |
| `/api/categories`             | POST   | Create new category     | ADMIN         |
| `/api/categories/{id}`        | PUT    | Update category         | ADMIN         |
| `/api/categories/{id}`        | DELETE | Delete category         | ADMIN         |

### Event Controller

| Endpoint                       | Method | Description          | Required Role |
|--------------------------------|--------|----------------------|---------------|
| `/api/events`                  | GET    | Search/filter events | None          |
| `/api/events/published`        | GET    | Get published events | None          |
| `/api/events/user/{userId}`    | GET    | Get user's events    | AUTHENTICATED |
| `/api/events/{id}`             | GET    | Get event by ID      | None          |
| `/api/events`                  | POST   | Create new event     | AUTHENTICATED |
| `/api/events/{id}`             | PUT    | Update event         | AUTHENTICATED |
| `/api/events/{id}`             | DELETE | Delete event         | AUTHENTICATED |
| `/api/events/{id}/publish`     | POST   | Publish event        | ADMIN         |
| `/api/events/{id}/unpublish`   | POST   | Unpublish event      | ADMIN         |
| `/api/events/{id}/images`      | POST   | Upload event image   | ADMIN         |
| `/api/events/images/{imageId}` | DELETE | Delete image         | ADMIN         |

#### Example: Create Event

```http
POST /api/events
Content-Type: application/json
Authorization: Bearer user.jwt.token.here

{
  "title": "Tech Conference 2023",
  "description": "Annual technology conference",
  "location": "Convention Center, New York",
  "startTime": "2023-11-15T09:00:00",
  "endTime": "2023-11-17T18:00:00",
  "categories": ["Technology", "Conference"],
  "tags": ["AI", "Blockchain"]
}
```

## Response Formats

### Success Response

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "status": 200,
  "data": {
    // Response data here
  }
}
```

### Error Response

```json
{
  "success": false,
  "message": "Event not found",
  "status": 404,
  "errors": [
    {
      "code": "EVENT_404",
      "detail": "No event found with ID 123"
    }
  ]
}
```

## Rate Limiting

- Public endpoints: 100 requests/minute
- Authenticated endpoints: 500 requests/minute
- Admin endpoints: 200 requests/minute

## Examples

### Complete Flow Example

1. **Register User**

```http
POST /api/auth/register
```

2. **Login**

```http
POST /api/auth/login
```

3. **Create Event**

```http
POST /api/events
```

4. **Create Booking**

```http
POST /api/bookings
```

5. **View Bookings**

```http
GET /api/bookings/user
```

---

# Event Booking API - Complete Setup Guide 🚀

Here's a comprehensive **Getting Started** guide with PostgreSQL configuration, email setup, and CI/CD pipeline
integration:

## 1. Prerequisites 📋

| Requirement | Version | Installation                                     |
|-------------|---------|--------------------------------------------------|
| Java        | 23+     | `sudo apt install openjdk-17-jdk`                |
| PostgreSQL  | 17+     | `sudo apt install postgresql postgresql-contrib` |
| Maven       | 3.8+    | `sudo apt install maven`                         |
| Git         | Latest  | `sudo apt install git`                           |

## 2. PostgreSQL Setup 🐘

### Configure Database

```bash
sudo -u postgres psql
CREATE DATABASE eventbooking;
CREATE USER eventuser WITH PASSWORD 'securepassword';
GRANT ALL PRIVILEGES ON DATABASE eventbooking TO eventuser;
\q
```

### Update `application.properties`

```properties
# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/eventbooking
spring.datasource.username=eventuser
spring.datasource.password=securepassword
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
# Hibernate
spring.jpa.hibernate.ddl-auto=validate
```

## 3. Email Configuration ✉️

### SMTP Properties

```properties
# Email Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## 4. CI/CD Pipeline ⚙️

### GitHub Actions (`.github/workflows/CICD.yml`)

```yaml
name: Event Booking API CI/CD Pipeline

on: [ push ]

jobs:
  build:
    name: Build and Test
    runs-on: ubuntu-latest

    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Set up JDK 23
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '23'
          java-package: jdk

      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-maven-

      - name: Build and Test with Maven
        run: mvn clean verify

      - name: Package Application
        run: mvn package -DskipTests

      - name: Upload JAR artifact
        uses: actions/upload-artifact@v4
        with:
          name: app-jar
          path: target/*.jar
          retention-days: 1

  deploy:
    name: Deploy to Heroku
    runs-on: ubuntu-latest
    needs: build
    if: github.ref == 'refs/heads/main' || github.ref == 'refs/heads/master'

    steps:
      - name: Checkout Repository
        uses: actions/checkout@v4

      - name: Download JAR artifact
        uses: actions/download-artifact@v4
        with:
          name: app-jar
          path: target

      - name: Create necessary files for Heroku
        run: |
          # Create system.properties file for Java version
          echo "java.runtime.version=23" > system.properties

          # Create Procfile with proper preview flag
          echo "web: java -Dserver.port=\$PORT \$JAVA_OPTS --enable-preview -jar target/*.jar" > Procfile

      - name: Install Heroku CLI
        run: |
          curl https://cli-assets.heroku.com/install.sh | sh

      - name: Deploy to Heroku
        uses: akhileshns/heroku-deploy@v3.12.14
        with:
          heroku_api_key: ${{ secrets.HEROKU_API_KEY }}
          heroku_app_name: ${{ secrets.HEROKU_APP_NAME }}
          heroku_email: ${{ secrets.HEROKU_EMAIL }}
          procfile: "web: java -Dserver.port=$PORT $JAVA_OPTS --enable-preview -jar target/*.jar"

      - name: Add PostgreSQL Database
        env:
          HEROKU_API_KEY: ${{ secrets.HEROKU_API_KEY }}
        run: |
          # Check if PostgreSQL addon already exists
          if heroku addons:info heroku-postgresql --app ${{ secrets.HEROKU_APP_NAME }} 2>/dev/null; then
            echo "PostgreSQL database already exists"
          else
            echo "Creating PostgreSQL database..."
            heroku addons:create heroku-postgresql:hobby-dev --app ${{ secrets.HEROKU_APP_NAME }}
            sleep 10  # Wait for database provisioning
          fi

      - name: Configure Database Connection
        env:
          HEROKU_API_KEY: ${{ secrets.HEROKU_API_KEY }}
        run: |
          # Get and convert DATABASE_URL to JDBC format for Spring Boot
          DB_URL=$(heroku config:get DATABASE_URL --app ${{ secrets.HEROKU_APP_NAME }})

          HOST_AND_DB=$(echo "$DB_URL" | sed -E 's|postgres://[^@]+@([^?]+).*|\1|')
          JDBC_URL="jdbc:postgresql://${HOST_AND_DB}?sslmode=require"

          DB_USER=$(echo "$DB_URL" | awk -F 'postgres://|:|@' '{print $2}')
          DB_PASS=$(echo "$DB_URL" | awk -F 'postgres://|:|@' '{print $3}')

          # Set database connection properties
          heroku config:set \
          SPRING_DATASOURCE_URL="$JDBC_URL" \
          SPRING_DATASOURCE_USERNAME="$DB_USER" \
          SPRING_DATASOURCE_PASSWORD="$DB_PASS" \
          --app ${{ secrets.HEROKU_APP_NAME }}

      - name: Configure Heroku Environment Variables
        env:
          HEROKU_API_KEY: ${{ secrets.HEROKU_API_KEY }}
        run: |
          # Set all environment variables atomically on Heroku app
          heroku config:set \
          JAVA_OPTS="-Xmx300m -Xss512k -XX:CICompilerCount=2 --enable-preview" \
          PORT="8080" \
          MAVEN_OPTS="--enable-preview" \
          MAIL_USERNAME="${{ secrets.MAIL_USERNAME }}" \
          MAIL_PASSWORD="${{ secrets.MAIL_PASSWORD }}" \
          JWT_SECRET_KEY="${{ secrets.JWT_SECRET_KEY }}" \
          --app ${{ secrets.HEROKU_APP_NAME }}

```

## 5. Docker Deployment 🐳

### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw package -DskipTests
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/target/event-booking-system-*.jar"]
```

### docker-compose.yml

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/eventbooking
      - SPRING_DATASOURCE_USERNAME=eventuser
      - SPRING_DATASOURCE_PASSWORD=securepassword
    depends_on:
      db:
        condition: service_healthy

  db:
    image: postgres:14
    environment:
      - POSTGRES_DB=eventbooking
      - POSTGRES_USER=eventuser
      - POSTGRES_PASSWORD=securepassword
    ports:
      - "5432:5432"
    healthcheck:
      test: [ "CMD-SHELL", "pg_isready -U eventuser -d eventbooking" ]
      interval: 5s
      timeout: 5s
      retries: 5
```

## 6. Deployment Options

### Heroku Deployment

```bash
# Create new Heroku app
heroku create your-app-name

# Add PostgreSQL addon
heroku addons:create heroku-postgresql:hobby-dev

# Set config vars
heroku config:set \
  SPRING_DATASOURCE_URL=$DATABASE_URL \
  SPRING_JPA_HIBERNATE_DDL-AUTO=update \
  SPRING_MAIL_USERNAME=your-email@gmail.com \
  SPRING_MAIL_PASSWORD=your-app-password

# Deploy
git push heroku main
```

## 7. Verification ✅

```bash
# Check application logs
heroku logs --tail

# Verify endpoints
curl https://event-booking-system-f79cebebc9ce.herokuapp.com/health
```

## Troubleshooting 🔧

| Issue                        | Solution                                          |
|------------------------------|---------------------------------------------------|
| PostgreSQL connection failed | Verify credentials and ensure PG is running       |
| Email not sending            | Check SMTP settings and enable "Less secure apps" |
| CI/CD pipeline failing       | Check workflow logs and test locally first        |
| Docker build issues          | Clean build with `docker system prune -a`         |

---

<div align="center">
  <img src="https://img.shields.io/badge/PostgreSQL-14-blue" alt="PostgreSQL">
  <img src="https://img.shields.io/badge/Java-17-red" alt="Java">
  <img src="https://img.shields.io/badge/CI/CD-GitHub%20Actions-orange" alt="CI/CD">
</div>

This guide provides:

- Complete PostgreSQL configuration
- SMTP email setup with Spring Mail
- GitHub Actions CI/CD pipeline
- Docker and Heroku deployment options
- Health checks and verification steps
- Comprehensive troubleshooting table
