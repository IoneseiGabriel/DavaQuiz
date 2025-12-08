# Dava Quiz - Auth Module

Implemented username/password login with JWT and IP-based rate limiting.

## Login API

**Endpoint**

`POST /auth/login`

**Request**

```json
{
  "username": "luca",
  "password": "password"
}
```

**Responses**

- `200 OK` – returns JWT and user info:
  ```json
  {
    "token": "jwt-token-here",
    "user": {
      "id": 1,
      "username": "luca"
    }
  }
  ```
- `400 Bad Request` – validation errors (missing username/password)
- `401 Unauthorized` – invalid credentials
- `429 Too Many Requests` – IP blocked by rate limiter

## Main Components

- `AuthController` – exposes `/auth/login` and returns proper HTTP codes.
- `AuthServiceImpl` – validates credentials, checks rate limiter, generates JWT.
- `LoginRateLimiter` – tracks failed attempts per IP and blocks when limit is exceeded.
- `JwtTokenProvider` – creates and validates HMAC256 JWTs with `userId` as subject.
- `UserEntity` / `UserRepository` – JPA entity + repo for users.
- `SecurityConfig` – provides `BCryptPasswordEncoder` bean.

## Configuration

Example properties:

```properties
jwt.secret=${JWT_SECRET} #is extracted from .env file
jwt.expiration-millis=86400000

auth.rate-limit.max-failed-attempts=5
auth.rate-limit.interval-seconds=900
auth.rate-limit.block-seconds=900
```

Test profile (`application-test.yml`) uses:
- H2 in-memory DB
- Very small rate limit and JWT expiration values for fast tests.
