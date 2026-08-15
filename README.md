# Library Management REST API

A REST API for managing users and books in a library system, built with Spring Boot and JPA in a layered
architecture (controller → service → repository).

## Features

- **User Management**: CRUD operations for library users
- **Book Management**: CRUD operations for books
- **Database Integration**: MySQL database with JPA/Hibernate in production; an in-memory H2 database for tests
- **RESTful Design**: Proper HTTP status codes and response formats
- **Error Handling**: Centralized exception handling with consistent JSON error bodies (see [Error Responses](#error-responses))

## Project Structure

```
src/main/java/com/askisi5/app/rest/
├── controller/   REST endpoints - request/response only, no business logic
├── service/      Business logic (validation beyond bean validation, not-found/duplicate checks, etc.)
│   └── impl/
├── repository/   Spring Data JPA repositories
├── model/        JPA entities
├── dto/          Request/response payloads exposed over HTTP
├── mapper/       Entity <-> DTO conversion
└── exception/    Custom exceptions + the centralized @RestControllerAdvice handler
```

Controllers never talk to repositories or entities directly - they take a request DTO, call a service, and
return whatever DTO or exception comes back. This keeps the HTTP contract (the DTOs) decoupled from the
persistence model (the entities).

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

| Field | Required | Constraints |
|---|---|---|
| `firstName` | yes | 2-50 characters |
| `lastName` | yes | 2-50 characters |
| `street` | no | max 100 characters |
| `city` | no | max 50 characters |
| `postalCode` | yes | integer, 1000-99999 (numeric postal codes only) |
| `country` | no | max 50 characters |
| `phoneNumber` | no | E.164-style, e.g. `+15550123` |
| `birthdayDate` | no | must be in the past |
| `sex` | no | `M` or `F` |

`id` is server-generated and read-only - it's ignored if present in a `POST`/`PUT` body.

### Book
```json
{
  "isbn": "978-0-123456-78-9",
  "title": "Sample Book",
  "shortSummary": "A brief description of the book",
  "publishYear": 2023
}
```

| Field | Required | Constraints |
|---|---|---|
| `isbn` | yes | valid ISBN-10 or ISBN-13 format; also the resource's identifier |
| `title` | yes | 1-200 characters |
| `shortSummary` | no | max 1000 characters |
| `publishYear` | yes | 1000-2100 |

On `PUT /api/v1/books/{isbn}`, the body's `isbn` must either match the URL path or be omitted - a
different ISBN in the body is rejected (see below), it does not move/rename the book.

## Error Responses

All errors return a JSON body. Field-level validation failures map each invalid field to its message;
everything else returns a single `error` message.

| Status | When | Example body |
|---|---|---|
| `400 Bad Request` | A field fails validation | `{"title": "Title is required"}` |
| `400 Bad Request` | Malformed JSON body | `{"error": "Malformed or unreadable request body"}` |
| `400 Bad Request` | A path variable has the wrong type (e.g. a non-numeric user id) | `{"error": "Invalid value for parameter 'id': not-a-number"}` |
| `400 Bad Request` | The `PUT` body's ISBN doesn't match the URL path | `{"error": "ISBN in request body (...) does not match the URL path (...)"}` |
| `404 Not Found` | The requested user/book doesn't exist | `{"error": "Book not found with ISBN: 978-..."}` |
| `409 Conflict` | `POST`ing a book whose ISBN already exists | `{"error": "A book with ISBN 978-... already exists"}` |
| `409 Conflict` | A database constraint is violated outside the checks above (e.g. a race between concurrent requests) | `{"error": "The request could not be completed because it conflicts with existing data"}` |
| `500 Internal Server Error` | Anything unexpected | `{"error": "Internal server error"}` |

## Setup and Installation

### Prerequisites
- Java 8 or higher
- MySQL 8.0 or higher (only needed to *run* the app - the test suite uses an in-memory database, see [Testing](#testing))
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
- **MySQL**: Production database
- **H2**: In-memory database used only by the test suite
- **JUnit 5 / Mockito**: Unit tests for the service layer
- **Spring MockMvc**: Integration tests for the REST endpoints
- **JaCoCo**: Test coverage reporting
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

This runs both layers of the test suite - neither requires MySQL or Docker to be running:
- **Unit tests** (`*ServiceImplTest`, `GlobalExceptionHandlerTest`) - service and error-handling logic in
  isolation, with the repository mocked via Mockito.
- **Integration tests** (`*ControllerIntegrationTest`) - full HTTP-through-database round trips via MockMvc,
  backed by an in-memory H2 database (see `src/test/resources/application-test.properties`).

A coverage report is generated at `target/site/jacoco/index.html` after the run.

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
