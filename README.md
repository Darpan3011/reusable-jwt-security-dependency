# User Management Starter with JWT Authentication

A Spring Boot starter that provides JWT-based authentication and user management out of the box. This starter includes secure user registration, login, and token management with blacklisting support.

## Features

- 🔒 JWT-based authentication
- 👤 User management with role-based access control
- 🔄 Stateless authentication with JWT
- 🚫 Token blacklisting for secure logout
- 🔄 Auto-configuration for security
- 📦 Easy integration with Spring Boot applications

## Requirements

- Java 17 or higher
- Spring Boot 3.0.0 or higher
- Maven 3.6.3 or higher
- JPA-compatible database (MySQL/PostgreSQL/H2)

## Installation

Add the following dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.darpan</groupId>
    <artifactId>user-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration

### 1. Database Configuration
Add these properties to your `application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/userdb
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# JWT Configuration
jwt.secret=your-jwt-secret-key-keep-it-secure
jwt.expiration=86400000  # 24 hours in milliseconds
```

### 2. Security Configuration
By default, the following endpoints are configured:
- `/auth/register` - Public endpoint for user registration
- `/auth/login` - Public endpoint for user login
- All other endpoints require authentication

## Usage

### 1. Register a New User
```http
POST /auth/register
Content-Type: application/json

{
    "username": "user@example.com",
    "password": "securePassword123",
    "role": "USER"
}
```

### 2. Login
```http
POST /auth/login
Content-Type: application/json

{
    "username": "user@example.com",
    "password": "securePassword123"
}
```

### 3. Using Authenticated Endpoints
Include the JWT token in the Authorization header for protected endpoints:
```
Authorization: Bearer your.jwt.token.here
```

### 4. Logout
```http
POST /auth/logout
Authorization: Bearer your.jwt.token.here
```

## Customization

### Custom User Details
Extend the `UserV2` entity to add additional user fields:

```java
@Entity
@Table(name = "custom_users")
public class CustomUser extends UserV2 {
    private String fullName;
    private String phoneNumber;
    // additional fields and methods
}
```

### Security Configuration
Override the default security configuration by creating your own `SecurityConfig` class:

```java
@Configuration
@EnableWebSecurity
public class CustomSecurityConfig {
    // Your custom security configuration
}
```

## Dependencies

- Spring Boot Starter Security
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- jjwt (Java JWT)
- Lombok
- Database driver (MySQL/PostgreSQL/H2)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## Support

For support, please open an issue in the GitHub repository.
