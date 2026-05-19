# Bank of Georgia

Microservices-based digital banking system built with Spring Boot and React.

## Architecture

| Service | Port | Description |
|---|---|---|
| `core-banking` | 8080 | Main service — customer, employee, product, account, and auth management |
| `notification-service` | 8082 | SMS delivery via Twilio |
| `event-service` | 8081 | Event service (in progress) |
| `frontend-customer` | 5173 | Customer-facing React/Vite app |
| `frontend-employee` | 5174 | Employee portal React/Vite app |

## Tech Stack

- **Backend:** Java 21, Spring Boot 4.0.5 (Web MVC, Security, Data MongoDB, Data Redis)
- **Frontend:** React 19, Vite, Tailwind CSS, react-router-dom, axios
- **Database:** MongoDB
- **Cache:** Redis (OTP storage)
- **Messaging:** Kafka + Zookeeper
- **SMS:** Twilio
- **Containerization:** Docker

## API Reference

### Auth — `/api/auth`

#### Customer Auth

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

#### Employee Auth

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/employee/login/username` | Login with username + password |

**Employee login request:**
```json
{
  "username": "emp1",
  "password": "secret"
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

### Employees — `/api/employees`

Employee IDs are human-readable (e.g. `EMP001`) and are used as path variables, not the MongoDB `_id`.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/employees` | Create a new employee |
| GET | `/api/employees` | List all employees |
| GET | `/api/employees/{id}` | Get employee by `employeeId` |
| PUT | `/api/employees/{id}` | Update employee profile |
| PUT | `/api/employees/{id}/role` | Update employee role |
| PUT | `/api/employees/{id}/status` | Update employee status (`ACTIVE` / `INACTIVE`) |

**Create request:**
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane@example.com",
  "username": "jane_smith",
  "phone": "+15559876543",
  "password": "secret",
  "dateOfBirth": "1988-06-15",
  "employeeId": "EMP001",
  "role": "TELLER",
  "department": "OPERATIONS"
}
```

Valid roles: `ADMIN`, `MANAGER`, `TELLER`, `SUPPORT`

Valid departments: `OPERATIONS`, `CUSTOMER_SERVICE`, `PRODUCTS`, `COMPLIANCE`

**Update profile request** (`PUT /api/employees/{id}`) — all fields optional:
```json
{
  "firstName": "Jane",
  "lastName": "Smith",
  "phone": "+15559876543",
  "dateOfBirth": "1988-06-15",
  "department": "COMPLIANCE"
}
```

**Update role request** (`PUT /api/employees/{id}/role`):
```json
{
  "role": "MANAGER"
}
```

**Update status request** (`PUT /api/employees/{id}/status`):
```json
{
  "status": "INACTIVE"
}
```

---

### Products — `/api/products`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/products` | Create a new product |
| GET | `/api/products` | List all products |
| GET | `/api/products/{id}` | Get product by ID |
| PUT | `/api/products/{id}` | Update product details |
| PUT | `/api/products/{id}/status` | Update product status (`ACTIVE` / `INACTIVE`) |

**Create request:**
```json
{
  "productName": "Basic Savings",
  "productType": "SAVINGS_ACCOUNT",
  "description": "Standard savings account with no monthly fee",
  "monthlyMaintenanceFee": "0.00",
  "minimumBalance": "50.00",
  "createdByEmployeeId": "EMP001"
}
```

Valid product types: `CHECKING_ACCOUNT`, `SAVINGS_ACCOUNT`, `CERTIFICATE_OF_DEPOSIT`, `BUSINESS_CHECKING_ACCOUNT`, `STUDENT_SAVINGS_ACCOUNT`

**Update request** (`PUT /api/products/{id}`) — all fields optional:
```json
{
  "productName": "Basic Savings",
  "description": "Updated description",
  "monthlyMaintenanceFee": "2.00",
  "minimumBalance": "100.00",
  "updatedByEmployeeId": "EMP001"
}
```

**Update status request** (`PUT /api/products/{id}/status`):
```json
{
  "status": "INACTIVE",
  "updatedByEmployeeId": "EMP001"
}
```

---

### Accounts — `/api/accounts`

Account numbers are auto-generated (e.g. `ACC000001`) and are used as path variables.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/accounts` | Open a new account |
| GET | `/api/accounts` | List all accounts |
| GET | `/api/accounts/{accountNumber}` | Get account by account number |
| GET | `/api/accounts/customer/{customerId}` | List all accounts for a customer |
| PUT | `/api/accounts/{accountNumber}` | Update account (e.g. product migration) |
| PUT | `/api/accounts/{accountNumber}/status` | Update account status (`ACTIVE` / `FROZEN` / `CLOSED`) |

**Open account request:**
```json
{
  "customerId": "64a1f2b3c4d5e6f7a8b9c0d1",
  "productId": "64a1f2b3c4d5e6f7a8b9c0d2",
  "openedByEmployeeId": "EMP001"
}
```

- The customer must exist and the product must be `ACTIVE`.
- Balance starts at `0.00` and is updated by deposit and withdrawal transactions.

**Update account request** (`PUT /api/accounts/{accountNumber}`) — all fields optional:
```json
{
  "productId": "64a1f2b3c4d5e6f7a8b9c0d3",
  "updatedByEmployeeId": "EMP001"
}
```

**Update status request** (`PUT /api/accounts/{accountNumber}/status`):
```json
{
  "status": "FROZEN",
  "updatedByEmployeeId": "EMP001"
}
```

---

### Transactions — `/api/transactions`

Transaction IDs are auto-generated (e.g. `TXN000001`).

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/transactions/deposit` | Deposit funds into an account |
| POST | `/api/transactions/withdraw` | Withdraw funds from an account |
| GET | `/api/transactions` | List all transactions |
| GET | `/api/transactions/account/{accountNumber}` | List all transactions for an account |

**Deposit request:**
```json
{
  "accountNumber": "ACC000001",
  "amount": "500.00",
  "description": "Initial deposit"
}
```

**Withdraw request:**
```json
{
  "accountNumber": "ACC000001",
  "amount": "100.00",
  "description": "ATM withdrawal"
}
```

**Transaction response:**
```json
{
  "transactionId": "TXN000001",
  "accountNumber": "ACC000001",
  "customerId": "64a1f2b3c4d5e6f7a8b9c0d1",
  "type": "DEPOSIT",
  "amount": "500.00",
  "description": "Initial deposit",
  "balanceAfter": "500.00",
  "status": "SUCCESS",
  "createdAt": "2025-01-01T12:00:00"
}
```

---

### Notifications — `/api/notifications`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/notifications/otp` | Send OTP SMS (called internally by core-banking) |

---

## Running with Docker

### Prerequisites

- Docker and Docker Compose installed
- Twilio credentials (account SID, auth token, phone number)

### Environment variables

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

---

## Running Locally (without Docker)

### Backend

Update `core-banking/src/main/resources/application.properties` to use local config:

```properties
spring.mongodb.uri=mongodb://localhost:27018/bank_of_georgia
spring.data.redis.host=localhost
spring.data.redis.port=6379
notification.service.otp.url=http://localhost:8082/api/notifications/otp
```

Start each service:

```bash
cd core-banking && ./mvnw spring-boot:run
cd notification-service && ./mvnw spring-boot:run
```

### Frontend

Each frontend is a separate Vite app. Run them in separate terminals:

```bash
# Customer app — http://localhost:5173
cd frontend-customer
cp .env.example .env
npm install
npm run dev
```

```bash
# Employee portal — http://localhost:5174
cd frontend-employee
cp .env.example .env
npm install
npm run dev
```

> To point either frontend at a different backend, update `VITE_API_BASE_URL` in its `.env` file.

---

## Branch Strategy

```
main
└── development
    └── feature/*
```
