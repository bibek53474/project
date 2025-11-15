# Spring Boot User Authentication & Role Management System

A comprehensive Spring Boot backend application with user authentication, role-based access control (RBAC), and password reset functionality.

## Features

- ✅ User registration and login
- ✅ Password hashing using BCrypt
- ✅ Role-based access control (Admin, Customer, Vendor)
- ✅ Session management
- ✅ Permission checking for different operations
- ✅ Password reset functionality with email
- ✅ Thymeleaf email templates
- ✅ MySQL database integration
- ✅ RESTful API endpoints

## Technology Stack

- **Spring Boot 3.5.7**
- **Spring Security 6** - Authentication and authorization
- **Spring Data JPA** - Database operations
- **MySQL** - Database
- **Thymeleaf** - Email templates
- **Lombok** - Reduced boilerplate code
- **Java 17**

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Email account (Gmail) for password reset functionality

## Setup Instructions

### 1. Database Configuration

Create a MySQL database:
```sql
CREATE DATABASE auth_db;
```

Update `src/main/resources/application.properties` with your database credentials:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 2. Email Configuration

Update email settings in `application.properties`:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

**Note:** For Gmail, you need to use an App Password instead of your regular password. Enable 2-factor authentication and generate an App Password.

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication Endpoints

#### 1. Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "ROLE_CUSTOMER"
}
```

**Available Roles:**
- `ROLE_ADMIN` - Administrator
- `ROLE_CUSTOMER` - Customer
- `ROLE_VENDOR` - Vendor

#### 2. Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "john_doe",
  "password": "password123"
}
```

#### 3. Logout
```http
POST /api/auth/logout
```

#### 4. Get Current User
```http
GET /api/auth/me
Authorization: Session-based (after login)
```

### Password Reset Endpoints

#### 1. Request Password Reset
```http
POST /api/auth/reset-password/request
Content-Type: application/json

{
  "email": "john@example.com"
}
```

#### 2. Confirm Password Reset
```http
POST /api/auth/reset-password/confirm
Content-Type: application/json

{
  "token": "reset_token_from_email",
  "newPassword": "newpassword123"
}
```

#### 3. Validate Reset Token
```http
GET /api/auth/reset-password/validate?token=reset_token
```

### Role-Based Endpoints

#### Admin Endpoints (Requires ROLE_ADMIN)

```http
GET /api/admin/dashboard
GET /api/admin/users/{username}
```

#### Customer Endpoints (Requires ROLE_CUSTOMER or ROLE_ADMIN)

```http
GET /api/customer/dashboard
GET /api/customer/profile
```

#### Vendor Endpoints (Requires ROLE_VENDOR or ROLE_ADMIN)

```http
GET /api/vendor/dashboard
GET /api/vendor/products
```

## Response Format

All API responses follow this format:

```json
{
  "success": true,
  "message": "Operation successful",
  "timestamp": "2024-01-01T12:00:00",
  "data": {
    // Response data
  }
}
```

## Security Features

### Password Hashing
- Passwords are hashed using BCrypt with a strength of 10
- Original passwords are never stored in the database

### Session Management
- Sessions are managed by Spring Security
- Maximum 1 session per user
- Session timeout and invalidation on logout

### Role-Based Access Control
- Three roles: ADMIN, CUSTOMER, VENDOR
- Method-level security using `@PreAuthorize`
- URL-based access control in SecurityConfig

### Password Reset Security
- Secure token generation using SecureRandom
- Token expiration (default: 30 minutes)
- One-time use tokens
- Email validation

## Database Schema

The application uses JPA to automatically create the following tables:

- **users** - User information
- **roles** - Available roles
- **user_roles** - User-Role mapping
- **password_reset_tokens** - Password reset tokens

## Project Structure

```
src/main/java/com/example/project/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── exception/      # Exception handlers
├── repository/     # Data access layers
├── security/       # Security configuration
├── service/        # Business logic
└── util/           # Utility classes
```

## Testing the API

### Using cURL

#### Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123",
    "role": "ROLE_CUSTOMER"
  }'
```

#### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "password123"
  }' \
  -c cookies.txt
```

#### Access Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/customer/dashboard \
  -b cookies.txt
```

## Configuration

### Application Properties

Key configuration options in `application.properties`:

- `app.password-reset.token-expiration` - Token expiration time in minutes (default: 30)
- `app.url` - Application URL for email links (default: http://localhost:8080)
- `spring.jpa.hibernate.ddl-auto` - Database schema management (update/create-drop/none)

## Error Handling

The application includes a global exception handler that returns consistent error responses:

```json
{
  "success": false,
  "message": "Error message",
  "timestamp": "2024-01-01T12:00:00",
  "data": {
    // Additional error details
  }
}
```

## Notes

- The application automatically creates roles (ADMIN, CUSTOMER, VENDOR) on startup
- Password reset tokens expire after 30 minutes by default
- Email functionality requires proper SMTP configuration
- CSRF is disabled for API endpoints (enable for production with proper configuration)

## License

This project is open source and available under the MIT License.

