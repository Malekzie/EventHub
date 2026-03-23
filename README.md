# EventHub API

A Spring Boot event management platform API for discovering events, managing categories, and handling registrations.

## Tech Stack

- Java 21
- Spring Boot 3.5.11
- Spring Data JPA
- Flyway (database migrations)
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

The app starts on `http://localhost:8080` with the `dev` profile. Flyway automatically runs all migrations and seeds sample data on startup.

### H2 Console

Available at `http://localhost:8080/h2-console` in dev mode.

- JDBC URL: `jdbc:h2:file:./devdb;AUTO_SERVER=TRUE`
- Username: `sa`
- Password: *(empty)*

### Swagger UI

Available at `http://localhost:8080/swagger-ui.html`

### IntelliJ ERD

To view the Entity Relationship Diagram:
1. Start the app
2. Open **Database** tool window → **+** → **Data Source** → **H2**
3. URL: `jdbc:h2:file:H:/School/Labs/Springboot/eventhub-api/devdb;AUTO_SERVER=TRUE`
4. Username: `sa`, Password: *(empty)*
5. Right-click **PUBLIC** schema → **Diagrams** → **Show Diagram**

## Database Schema

| Table | Description |
|-------|-------------|
| `categories` | Event categories |
| `events` | Events with price, date, and category |
| `users` | Users (future authentication scaffold) |
| `registrations` | Ticket registrations linked to users |
| `registration_items` | Individual event tickets per registration |
| `reviews` | Event reviews with ratings (1–5) |

Migrations are managed by Flyway (`src/main/resources/db/migration/`).

## API Documentation

### Categories

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/categories` | List all categories |
| GET | `/api/v1/categories/{id}` | Get category by ID |
| POST | `/api/v1/categories` | Create a new category |

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
| `startDate` | - | Start date filter (`yyyy-MM-dd`) |
| `endDate` | - | End date filter (`yyyy-MM-dd`) |

**Example:**

```
GET /api/v1/events?category=Technology&minPrice=0&maxPrice=50&page=0&size=10&sort=ticketPrice,desc
```

### Registrations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/registrations` | List all registrations (paginated) |
| GET | `/api/v1/registrations/{id}` | Get registration by ID |
| GET | `/api/v1/registrations/user/{userId}` | Get registrations by user ID |
| POST | `/api/v1/registrations` | Create a new registration |
| PATCH | `/api/v1/registrations/{id}/cancel` | Cancel a registration |

**Create Registration Request Body:**

```json
{
  "items": [
    { "eventId": 1, "quantity": 2 },
    { "eventId": 3, "quantity": 1 }
  ]
}
```

**Registration Response:**

```json
{
  "id": 1,
  "userId": null,
  "status": "CONFIRMED",
  "totalAmount": 124.98,
  "registrationDate": "2026-03-20T22:00:00",
  "items": [
    {
      "id": 1,
      "eventId": 1,
      "eventName": "Spring Tech Summit",
      "quantity": 2,
      "unitPrice": 49.99,
      "subtotal": 99.98
    }
  ]
}
```

Registration statuses: `PENDING`, `CONFIRMED`, `CANCELLED`

### Validation Rules

- **Event name**: required, 3–100 characters
- **Ticket price**: required, must be 0 or positive
- **Category ID**: required
- **Event date**: required
- **Registration items**: at least 1 item required, quantity ≥ 1

### Error Responses

Validation errors return `400`:

```json
{
  "timestamp": "2026-03-20T10:00:00",
  "status": 400,
  "error": "Validation Failed",
  "fieldErrors": {
    "name": "Title must be between 3 and 100 characters"
  }
}
```

Not found errors return `404`:

```json
{
  "error": "Event not found with id: 99"
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
