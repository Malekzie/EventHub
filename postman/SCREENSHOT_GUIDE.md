# Assignment 3 — Screenshot Guide

Copy captions/bodies below into `Assignment3-Screenshots.docx`. Take each screenshot from Postman (or Swagger where noted). Expected response bodies shown — yours will differ in `id`, `registrationDate`, token values, etc.

Seeded dev accounts (auto-created by `DevDataSeeder`):
- **admin@eventhub.com** / admin123 → ROLE_ADMIN
- **user@eventhub.com** / user123 → ROLE_USER

---

## Section 1 — Authentication

### 1.1 Register new user
**Caption:** `POST /api/v1/auth/register` — creates a new USER and returns a JWT.
**Request body:**
```json
{
  "email": "newuser@example.com",
  "firstName": "New",
  "lastName": "User",
  "password": "password123"
}
```
**Expected:** `201 Created`
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "newuser@example.com",
  "role": "USER"
}
```

### 1.2 Login as USER
**Caption:** `POST /api/v1/auth/login` — authenticates seeded USER account; JWT returned.
**Request:**
```json
{ "email": "user@eventhub.com", "password": "user123" }
```
**Expected:** `200 OK` + token.

### 1.3 Login as ADMIN
**Caption:** `POST /api/v1/auth/login` — authenticates seeded ADMIN account.
**Request:**
```json
{ "email": "admin@eventhub.com", "password": "admin123" }
```
**Expected:** `200 OK`, `role: "ADMIN"`.

### 1.4 Login with wrong password (failure)
**Caption:** Invalid credentials rejected with `401 Unauthorized`.
**Request:**
```json
{ "email": "user@eventhub.com", "password": "wrongpassword" }
```
**Expected:** `401 Unauthorized`.

---

## Section 2 — Password Reset

### 2.1 Request password reset
**Caption:** `POST /api/v1/auth/password-reset-request` — generates a reset token (dev mode returns it in body; prod would email it).
**Request:**
```json
{ "email": "user@eventhub.com" }
```
**Expected:** `200 OK`
```json
{
  "resetToken": "a1b2c3d4-...",
  "message": "Use this token at /api/v1/auth/password-reset to set a new password"
}
```

### 2.2 Reset password with valid token
**Caption:** `POST /api/v1/auth/password-reset` — consumes the token and sets a new password.
**Request:**
```json
{ "token": "<token from 2.1>", "newPassword": "newSecret123" }
```
**Expected:** `200 OK` with success message.

### 2.3 Reset password with invalid token (failure)
**Caption:** Invalid / expired token rejected with `400`.
**Request:**
```json
{ "token": "not-a-real-token", "newPassword": "whatever123" }
```
**Expected:** `400 Bad Request` — `Invalid or expired reset token`.

---

## Section 3 — Public endpoints (no auth)

### 3.1 List events (public)
**Caption:** `GET /api/v1/events` — accessible without authentication.
**Expected:** `200 OK` — paginated events list.

### 3.2 Get event by ID (public)
**Caption:** `GET /api/v1/events/2` — anonymous read allowed.
**Expected:** `200 OK` — event detail.

### 3.3 List categories (public)
**Caption:** `GET /api/v1/categories` — no token required.
**Expected:** `200 OK` — categories array.

---

## Section 4 — USER role authorized

### 4.1 Create registration as USER
**Caption:** `POST /api/v1/registrations` with `Authorization: Bearer {{userToken}}` — USER may book tickets.
**Request:**
```json
{
  "items": [
    { "eventId": 1, "quantity": 2 },
    { "eventId": 3, "quantity": 1 }
  ]
}
```
**Expected:** `201 Created` — registration with items + `totalAmount`.

### 4.2 Get own registrations
**Caption:** `GET /api/v1/registrations/user/1` as USER — returns the user's own registrations.
**Expected:** `200 OK`.

### 4.3 Cancel registration
**Caption:** `PATCH /api/v1/registrations/1/cancel` as USER — status becomes `CANCELLED`.
**Expected:** `200 OK`.

---

## Section 5 — ADMIN role authorized

### 5.1 Create category (ADMIN)
**Caption:** `POST /api/v1/categories` with `Authorization: Bearer {{adminToken}}` — ADMIN-only write.
**Request:**
```json
{ "name": "Workshops" }
```
**Expected:** `201 Created`.

### 5.2 Create event (ADMIN)
**Caption:** `POST /api/v1/events` — ADMIN creates a new event.
**Request:**
```json
{
  "name": "Spring Boot Workshop",
  "description": "Learn Spring Boot fundamentals",
  "ticketPrice": 25.00,
  "categoryId": 2,
  "eventDate": "2026-07-15T10:00:00"
}
```
**Expected:** `201 Created`.

### 5.3 Update event (ADMIN)
**Caption:** `PUT /api/v1/events/2` — ADMIN updates event.
**Request:**
```json
{
  "name": "Spring Boot Workshop (Updated)",
  "description": "Now with more annotations",
  "ticketPrice": 35.00,
  "categoryId": 2,
  "eventDate": "2026-07-22T10:00:00"
}
```
**Expected:** `200 OK`.

### 5.4 Delete event (ADMIN)
**Caption:** `DELETE /api/v1/events/5` — ADMIN deletes event.
**Expected:** `204 No Content`.

### 5.5 List all registrations (ADMIN)
**Caption:** `GET /api/v1/registrations` — ADMIN can view every registration.
**Expected:** `200 OK` — paginated list.

---

## Section 6 — RBAC Failure cases (authorization)

### 6.1 No token on protected route → 401
**Caption:** `POST /api/v1/events` **without** `Authorization` header — rejected.
**Expected:** `401 Unauthorized`.

### 6.2 Invalid token → 401
**Caption:** `GET /api/v1/registrations` with `Authorization: Bearer not.a.real.token`.
**Expected:** `401 Unauthorized`.

### 6.3 USER hitting ADMIN-only route → 403
**Caption:** `POST /api/v1/events` with USER token — authenticated but lacks role.
**Expected:** `403 Forbidden`.

### 6.4 USER listing all registrations → 403
**Caption:** `GET /api/v1/registrations` with USER token — ADMIN-only.
**Expected:** `403 Forbidden`.

### 6.5 USER deleting an event → 403
**Caption:** `DELETE /api/v1/events/1` with USER token.
**Expected:** `403 Forbidden`.

---

## Section 7 — Security headers (evidence of hardening)

### 7.1 Response headers on any endpoint
**Caption:** Response `Headers` tab of any call (e.g., `GET /api/v1/events`) showing:
- `X-Frame-Options: DENY`
- `X-Content-Type-Options: nosniff`
- `X-XSS-Protection: 1; mode=block`
- `Referrer-Policy: strict-origin-when-cross-origin`
- `Content-Security-Policy: default-src 'self'; frame-ancestors 'none'`

---

## Section 8 — Input validation

### 8.1 Register with short password → 400
**Caption:** Bean Validation rejects passwords shorter than 8 characters.
**Request:**
```json
{ "email": "x@y.com", "firstName": "X", "lastName": "Y", "password": "short" }
```
**Expected:** `400 Bad Request` with field error on `password`.

### 8.2 Register with bad email → 400
**Caption:** `@Email` validation rejects malformed addresses.
**Request:**
```json
{ "email": "not-an-email", "firstName": "X", "lastName": "Y", "password": "password123" }
```
**Expected:** `400 Bad Request` with field error on `email`.

---

## Section 9 — Swagger documentation

### 9.1 Swagger UI auth tag
**Caption:** `http://localhost:8080/swagger-ui.html` — **Authentication** tag showing all 4 auth endpoints with request schemas.

### 9.2 Swagger auth-required endpoint
**Caption:** Swagger entry for `POST /api/v1/events` showing 401/403 responses and request schema.

### 9.3 OpenAPI JSON
**Caption:** `http://localhost:8080/v3/api-docs` — raw OpenAPI spec with auth schemas.

---

## Section 10 — Tests passing

### 10.1 SecurityIntegrationTest output
**Caption:** Terminal output from `./mvnw test -Dtest=SecurityIntegrationTest` — `Tests run: 10, Failures: 0, Errors: 0`.

### 10.2 JwtUtilTest output
**Caption:** `./mvnw test -Dtest=JwtUtilTest` showing all assertions pass.

---

## Postman runner summary (bonus)
Run entire collection (Postman → Collection → Run). Screenshot the **Run summary** showing pass counts across Auth, Public, USER, ADMIN, and RBAC-failure folders.
