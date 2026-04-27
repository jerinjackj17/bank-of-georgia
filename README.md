# Bank of Georgia

Microservices-based digital banking system built with Spring Boot and React.

## Architecture

| Service | Port | Description |
|---|---|---|
| `core-banking` | 8080 | Main service — customer management and authentication |
| `notification-service` | 8082 | SMS delivery via Twilio |
| `event-service` | 8081 | Event service (in progress) |
| `frontend` | 3000 | React/Vite web client |

## Tech Stack

- **Backend:** Java 21, Spring Boot 4.0.5 (Web MVC, Security, Data MongoDB, Data Redis)
- **Frontend:** React, Vite, Tailwind CSS
- **Database:** MongoDB
- **Cache:** Redis (OTP storage)
- **SMS:** Twilio
- **Containerization:** Docker

## API Reference

### Auth — `/api/auth`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/customer/login` | Login with username or email + password |
| POST | `/api/auth/customer/login/phone` | Request OTP via SMS |
| POST | `/api/auth/customer/login/verifyOtp` | Verify OTP and authenticate |

**Login request:**
```json
{
  "loginId": "john_doe",
  "loginType": "username",
  "password": "secret"
}
```

**OTP request:**
```json
{
  "phoneNumber": "+15551234567"
}
```

**OTP verification:**
```json
{
  "phoneNumber": "+15551234567",
  "otp": "482910"
}
```

---

### Customers — `/api/customers`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/customers/register` | Register a new customer |
| GET | `/api/customers` | List all customers |
| GET | `/api/customers/{id}` | Get customer by ID |
| PUT | `/api/customers/{id}` | Update customer profile |
| PUT | `/api/customers/{id}/status` | Update customer status (`ACTIVE` / `BLOCKED`) |

**Register request:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "username": "john_doe",
  "phone": "+15551234567",
  "password": "secret",
  "dateOfBirth": "1990-01-01"
}
```

---

### Notifications — `/api/notifications`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/notifications/otp` | Send OTP SMS (called internally by core-banking) |

## Running with Docker

> All services are configured to run as Docker containers.

### Prerequisites

- Docker and Docker Compose installed
- Twilio credentials (account SID, auth token, phone number)

### Environment Variables

Create a `.env` file in the project root:

```
TWILIO_ACCOUNT_SID=your_account_sid
TWILIO_AUTH_TOKEN=your_auth_token
TWILIO_PHONE_NUMBER=your_twilio_phone_number
```

### Start all services

```bash
docker compose up --build
```

## Running Locally (without Docker)

Update `core-banking/src/main/resources/application.properties` to use the local config block (commented out by default):

```properties
spring.mongodb.uri=mongodb://localhost:27018/bank_of_georgia
spring.data.redis.host=localhost
spring.data.redis.port=6379
notification.service.otp.url=http://localhost:8082/api/notifications/otp
```

Then start each service individually:

```bash
# core-banking
cd core-banking && ./mvnw spring-boot:run

# notification-service
cd notification-service && ./mvnw spring-boot:run

# frontend
cd frontend && npm install && npm run dev
```

## Branch Strategy

```
main
└── development
    └── feature/*
```