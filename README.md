# Library Management REST API

A REST API for managing users and books in a library system, built with Spring Boot and JPA.

## Features

- **User Management**: CRUD operations for library users
- **Book Management**: CRUD operations for books
- **Database Integration**: MySQL database with JPA/Hibernate
- **RESTful Design**: Proper HTTP status codes and response formats
- **Error Handling**: Comprehensive error handling and validation

## API Endpoints

### Users

- `GET /api/v1/users` - Get all users
- `GET /api/v1/users/{id}` - Get user by ID
- `POST /api/v1/users` - Create a new user
- `PUT /api/v1/users/{id}` - Update an existing user
- `DELETE /api/v1/users/{id}` - Delete a user

### Books

- `GET /api/v1/books` - Get all books
- `GET /api/v1/books/{isbn}` - Get book by ISBN
- `POST /api/v1/books` - Create a new book
- `PUT /api/v1/books/{isbn}` - Update an existing book
- `DELETE /api/v1/books/{isbn}` - Delete a book

## Data Models

### User
```json
{
  "id": 1,
  "firstName": "John",
  "lastName": "Doe",
  "street": "123 Main St",
  "city": "Anytown",
  "postalCode": 12345,
  "country": "USA",
  "phoneNumber": "+1-555-0123",
  "birthdayDate": "1990-01-01",
  "sex": "M"
}
```

### Book
```json
{
  "isbn": "978-0-123456-78-9",
  "title": "Sample Book",
  "shortSummary": "A brief description of the book",
  "publishYear": 2023
}
```

## Setup and Installation

### Prerequisites
- Java 11 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Database Setup
1. Create a MySQL database named `library`
2. Update the database credentials in `src/main/resources/application.properties` or set environment variables

### Environment Variables (Recommended)
Set the following environment variables for database configuration:
- `DB_URL=jdbc:mysql://localhost:3306/library?useSSL=false&serverTimezone=UTC`
- `DB_USERNAME=your_username`
- `DB_PASSWORD=your_password`

### Running the Application
1. Clone the repository
2. Navigate to the project directory
3. Run with Maven: `mvn spring-boot:run`
4. Or run with Maven wrapper: `./mvnw spring-boot:run` (Linux/Mac) or `mvnw.cmd spring-boot:run` (Windows)

The API will be available at `http://localhost:8080`

## Technologies Used

- **Spring Boot**: Framework for building the REST API
- **Spring Data JPA**: Data access layer
- **Hibernate**: ORM for database operations
- **MySQL**: Database management system
- **Maven**: Build and dependency management

## Development

### Building
```bash
mvn clean compile
```

### Testing
```bash
mvn test
```

### Packaging
```bash
mvn clean package
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.