# 🧭 Payment Routing Engine — Modular Monolith

A modern, highly flexible **Payment Routing & Splitting Engine** designed to dynamically select, validate, and route transactions across multiple payment gateways based on processing rules, operational limits, commission rates, and real-time availability. 

The project features a **Spring Boot (Java 17)** Modular Monolith backend and a responsive **Angular (v21)** dashboard frontend.

---

## 🏗️ Architecture & Core Features

This platform is structured as a clean Monorepo divided into:
1. **Backend (`Payment Routing Engine/`)**: Spring Boot modular monolith containing:
   - **User / Security Module**: JWT-based authentication (`/api/auth`) with BCrypt password hashing.
   - **Gateway Module**: Manages payment gateways, including status toggling, availability rules (operational days/hours), and transactional limits (min/max limits, daily caps).
   - **Transaction & Routing Module**: Selects optimal gateways, calculates minimum commissions, provides a smart **payment splitter** algorithm, and processes transactions.
   - **Usage Module**: Monitors daily usage quotas per gateway.
   - **Biller Module**: Manages supported billers.
2. **Frontend (`payment-routing-ui/`)**: A rich, interactive Angular administration portal.

---

## 🛠️ Prerequisites

Before you begin, ensure you have the following installed on your machine:
* **Java Development Kit (JDK) 17** or higher
* **Node.js** (v18.x or newer) and **npm**
* **Angular CLI** (v17.x or newer, v21 recommended)
* **PostgreSQL Database** (running locally or remotely)
* **Maven** (optional, wrapper `./mvnw` is included in the project)

---

## 🚀 Getting Started

### 1. Database Setup
The system uses PostgreSQL for persistence. Flyway automatically runs migrations on startup to create the schema and seed initial gateway/biller configurations.

1. Connect to your PostgreSQL server.
2. Create a new database named `payment_routing_db`:
   ```sql
   CREATE DATABASE payment_routing_db;
   ```
3. *(Optional)* If your local PostgreSQL username or password is not `postgres`, you can set them using system environment variables, or update `Payment Routing Engine/src/main/resources/application-dev.yml`:
   * `DB_USERNAME`: Database username (Defaults to `postgres`)
   * `DB_PASSWORD`: Database password (Defaults to `postgres`)

---

### 2. Backend Installation & Setup (Spring Boot)

1. Open a terminal and navigate to the backend directory:
   ```bash
   cd "Payment Routing Engine"
   ```
2. Build the project and download all dependencies:
   ```bash
   ./mvnw clean install
   ```
3. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```
   *The server runs by default on **port `8090`**.*

> [!NOTE]
> **Default Admin Account:**
> On application startup, a `CommandLineRunner` automatically checks if the database is empty of users and seeds a default admin account:
> * **Username:** `admin`
> * **Password:** `password123`
> This account must be used to authenticate requests via JWT.

> [!TIP]
> **Swagger UI Integration:**
> Once the backend is running, you can explore, test, and document all APIs visually through the Swagger UI:
> * URL: [http://localhost:8090/swagger-ui/index.html](http://localhost:8090/swagger-ui/index.html)

---

### 3. Frontend Installation & Setup (Angular UI)

1. Open a new terminal window and navigate to the UI directory:
   ```bash
   cd payment-routing-ui
   ```
2. Install all required npm packages:
   ```bash
   npm install
   ```
3. Start the Angular local development server:
   ```bash
   ng serve
   ```
4. Access the administration portal in your browser:
   * URL: [http://localhost:4200/](http://localhost:4200/)
   * Log in using the seeded credentials: `admin` / `password123`

---

## 🔌 API Documentation Reference

Below is a summary of the most critical endpoints in the system.

### 🔑 Authentication
| Endpoint | Method | Description | Payloads |
| :--- | :--- | :--- | :--- |
| `/api/auth/login` | `POST` | Authenticates user and returns JWT token | `{ "username": "admin", "password": "password123" }` |

### 💳 Gateways Management
*All Gateway/Transaction requests require an `Authorization: Bearer <JWT_TOKEN>` header.*

| Endpoint | Method | Description |
| :--- | :--- | :--- |
| `/api/gateways` | `GET` | Retrieve list of all payment gateways |
| `/api/gateways` | `POST` | Create a new payment gateway |
| `/api/gateways/{id}` | `PUT` | Update configuration of an existing gateway |
| `/api/gateways/{id}/status` | `PATCH` | Enable/Disable a specific gateway |

### 🔀 Smart Routing & Splitting
| Endpoint | Method | Description | Payloads |
| :--- | :--- | :--- | :--- |
| `/api/payments/recommend` | `POST` | Returns a sorted list of active gateways sorted from cheapest to most expensive commission for a specific transaction amount. | `{ "amount": 1000, "billerCode": "bil1" }` |
| `/api/payments/split` | `POST` | Calculates and returns a recommended breakdown to split a large payment amount into smaller chunks that fit within available gateway limits. | `{ "amount": 25000, "billerCode": "bil2" }` |
| `/api/payments/process-split` | `POST` | Validates, processes, and commits a split transaction breakdown. | *(Refer to manual test scenarios below)* |

---

## 🧪 Manual Verification & Core Test Scenarios

### Scenario A: Payment Recommendation (Cheapest Gateway routing)
* **Goal:** Verify that the system recommends the gateway with the lowest calculated commission that satisfies all rules (active, operational days, hours, transaction limits, and daily quotas).
* **Sample Request (`POST /api/payments/recommend`):**
  ```json
  {
    "amount": 1000.00,
    "billerCode": "bil1"
  }
  ```
* **Expected Behavior:**
  1. The system evaluates all active gateways.
  2. It filters out gateways whose minimum transaction amount is greater than `1000.00`, or whose maximum limit is less than `1000.00`.
  3. It filters out gateways that are outside their operational availability days/hours.
  4. It computes the total commission (Fixed Commission + Percentage Commission of amount) for each valid gateway.
  5. It returns the available gateways sorted in ascending order of calculated commission.

---

### Scenario B: Payment Splitting
* **Goal:** Verify that a large amount exceeding individual gateway limits is successfully divided into multiple smaller transactions across valid gateways.
* **Sample Request (`POST /api/payments/split`):**
  ```json
  {
    "amount": 25000.00,
    "billerCode": "bil1"
  }
  ```
* **Expected Behavior:**
  1. The system identifies active, operational gateways.
  2. It generates a recommended distribution schema where chunks of the amount are assigned to various gateways, ensuring each chunk respects the individual gateway's `min_transaction_amount` and `max_transaction_amount` rules.
  3. Returns a split breakdown with proposed transaction amounts per gateway and the total calculated commission.
