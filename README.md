# EventHub API

A Spring Boot event management platform API for discovering events, managing categories, and handling registrations.

## Tech Stack

- Java 21
- Spring Boot 3.5.11
- Spring Data JPA
- H2 Database (dev) / PostgreSQL (prod)
- Caffeine Cache
- SpringDoc OpenAPI (Swagger)
- Bean Validation

## Setup Instructions

### Prerequisites

- Java 21+
- Maven (or use the included Maven wrapper)

### Run the Application

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080` with the `dev` profile (H2 in-memory database).

### H2 Console

Available at `http://localhost:8080/h2-console` in dev mode.

- JDBC URL: `jdbc:h2:mem:devdb`
- Username: `sa`
- Password: *(empty)*

### Swagger UI

Available at `http://localhost:8080/swagger-ui.html`

## API Documentation

### Categories

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/categories` | List all categories |
| GET | `/api/categories/{id}` | Get category by ID |
| POST | `/api/categories` | Create a new category |

**Create Category Request Body:**

```json
{
  "name": "Concert"
}
```

### Events

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/events` | List events (paginated, filterable) |
| GET | `/api/v1/events/{id}` | Get event by ID |
| POST | `/api/v1/events` | Create a new event |
| PUT | `/api/v1/events/{id}` | Update an event |
| DELETE | `/api/v1/events/{id}` | Delete an event |

**Create/Update Event Request Body:**

```json
{
  "name": "Spring Boot Workshop",
  "description": "Learn Spring Boot fundamentals",
  "ticketPrice": 25.00,
  "categoryId": 1,
  "eventDate": "2026-04-15T10:00:00"
}
```

### Pagination, Filtering & Sorting

The `GET /api/v1/events` endpoint supports the following query parameters:

| Parameter | Default | Description |
|-----------|---------|-------------|
| `page` | 0 | Page number (0-based) |
| `size` | 20 | Page size |
| `sort` | `id,asc` | Sort field and direction (e.g., `ticketPrice,desc`) |
| `category` | - | Filter by category name |
| `minPrice` | - | Minimum ticket price |
| `maxPrice` | - | Maximum ticket price |
| `startDate` | - | Start date filter (`yyyy-MM-dd'T'HH:mm:ss`) |
| `endDate` | - | End date filter (`yyyy-MM-dd'T'HH:mm:ss`) |

**Example:**

```
GET /api/v1/events?category=concert&minPrice=0&maxPrice=100&page=0&size=10&sort=ticketPrice,desc
```

### Validation Rules

- **Event name**: required, 3-100 characters
- **Ticket price**: required, must be 0 or positive
- **Description**: optional, max 1000 characters
- **Category ID**: required
- **Event date**: required

### Error Responses

Validation errors return a `400` response:

```json
{
  "timestamp": "2026-03-14T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "fieldErrors": {
    "name": "Title must be between 3 and 100 characters"
  }
}
```

Not found errors return a `404` response:

```json
{
  "timestamp": "2026-03-14T10:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Event not found with id: 99"
}
```

### Health Check

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/health` | Application health status |

## Caching

The API uses Caffeine for local caching with a 10-minute TTL and a max of 500 entries. Caches are automatically evicted when data is created, updated, or deleted.

## CORS

CORS is configured to allow requests from `http://localhost:3000` and `http://localhost:4200` with `GET`, `POST`, `PUT`, `DELETE`, and `OPTIONS` methods.
