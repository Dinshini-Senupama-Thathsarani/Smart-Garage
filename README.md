# Smart Garage API
Senu Auto MObile

Spring Boot REST API for managing a vehicle garage / spare-parts business — customers, vehicle bookings, job cards, mechanics, spare parts inventory, purchase orders, invoices and payments — with JWT-based authentication and a built-in vanilla-JS front end.

## Tech Stack

- Java 21
- Spring Boot 3.3.2 (Web, Data JPA, Validation, Security)
- MySQL
- JWT (jjwt 0.12.6)
- springdoc-openapi (Swagger UI)
- Lombok
- Spring Mail (optional)
- ZXing (QR code generation)

## Features

- JWT authentication (register / login) with role-based access — `ADMIN`, `MECHANIC`, `CUSTOMER`
- Customer, Vehicle, Booking, Job Card, Mechanic management
- Spare parts inventory with categories, suppliers, purchase orders
- Invoicing and payments
- QR code generation for job cards
- Optional email notifications (SMTP)
- Optional AI-generated report summaries (Anthropic Claude API)
- Swagger UI for API docs
- Static HTML/CSS/JS front end served from `src/main/resources/static`

## Prerequisites

- Java 21+
- Maven 3.9+ (or use the included `mvnw` wrapper)
- MySQL running locally (or update the datasource URL to point elsewhere)

## Configuration

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_garage_db_new?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=mysql
```

Update the username/password to match your local MySQL setup. The database is created automatically on first run (`createDatabaseIfNotExist=true`), and tables are auto-managed via `spring.jpa.hibernate.ddl-auto=update`.

Optional integrations (email, AI report summaries) are disabled by default (`app.mail.enabled=false`, `app.ai.enabled=false`) — enable them and add real credentials only if you need them.

## Running the Project

```bash
# using the Maven wrapper
./mvnw spring-boot:run

# or with a local Maven install
mvn spring-boot:run
```

The app starts on **http://localhost:8082**.

- Front end: `http://localhost:8082/`
- Swagger UI: `http://localhost:8082/swagger-ui.html`

## Authentication

- `POST /api/v1/auth/register` — self-registration (defaults to `CUSTOMER` role)
- `POST /api/v1/auth/login` — returns a JWT

Send the token on subsequent requests as:



## Postman
https://dinshinisenupama737-6397775.postman.co/workspace/Dinshini-Senupama's-Workspace~1475e2b6-bf39-4a14-81fb-55ce3cc52d94/collection/55297692-625abbe7-8996-426d-bd06-8174bbe01f6d?action=share&source=copy-link&creator=55297692