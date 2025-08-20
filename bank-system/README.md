# Digital Banking System

A comprehensive digital bank management system including customer management, accounts, and money transfers.

## Key Features

### Customer Types
- **IndividualCustomer** - Private customers
- **BusinessCustomer** - Business customers  
- **VIPCustomer** - VIP customers

### Functionality
- Create different types of customers
- Open bank accounts
- Execute money transfers
- Automatic balance updates
- Transaction recording in Transaction table
- Send events to Kafka for asynchronous processing

## API Endpoints

### Customer Management

#### Create Individual Customer
```http
POST /api/customers/individual
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@email.com",
  "phone": "+1234567890",
  "address": "123 Main St, City",
  "nationalId": "123456789",
  "dateOfBirth": "1990-01-01",
  "occupation": "Software Engineer"
}
```

#### Create Business Customer
```http
POST /api/customers/business
Content-Type: application/json

{
  "name": "Tech Corp Ltd",
  "email": "info@techcorp.com",
  "phone": "+1987654321",
  "address": "456 Business Ave, City",
  "businessRegistrationNumber": "BRN123456",
  "businessType": "Technology",
  "industry": "Software Development",
  "taxId": "TAX789123"
}
```

#### Create VIP Customer
```http
POST /api/customers/vip
Content-Type: application/json

{
  "name": "Alice Smith",
  "email": "alice.smith@email.com",
  "phone": "+1555666777",
  "address": "789 Elite Blvd, City",
  "vipLevel": "PLATINUM",
  "minimumBalance": 50000.00,
  "personalBanker": "Robert Johnson",
  "specialServices": "24/7 Support, Investment Advisory"
}
```

### Account Management

#### Create Account
```http
POST /api/accounts
Content-Type: application/json

{
  "customerId": 1,
  "accountType": "CHECKING",
  "initialBalance": 5000.00
}
```

#### Get Customer Accounts
```http
GET /api/accounts/customer/{customerId}
```

#### Get Account by Number
```http
GET /api/accounts/{accountNumber}
```

### Money Transfers

#### Process Transfer
```http
POST /api/transactions/transfer
Content-Type: application/json

{
  "fromAccountNumber": "ACC1691234567001",
  "toAccountNumber": "ACC1691234567002",
  "amount": 1000.00,
  "description": "Payment for services",
  "referenceNumber": "REF123456"
}
```

## Kafka Events

Money transfers produce events to Kafka topic: `bank-transactions`

### Event Structure
```json
{
  "eventType": "transaction",
  "timestamp": "2025-08-20T10:30:00.123Z",
  "transactionId": 1,
  "fromAccount": {
    "id": 1,
    "balanceBefore": 5000.00,
    "balanceAfter": 4000.00,
    "customer": {
      "id": 1,
      "name": "John Doe",
      "type": "individual",
      "personalId": "123-45-6789",
      "businessNumber": null
    }
  },
  "toAccount": {
    "id": 2,
    "balanceBefore": 2000.00,
    "balanceAfter": 3000.00,
    "customer": {
      "id": 2,
      "name": "Tech Corp Ltd",
      "type": "business",
      "personalId": null,
      "businessNumber": "BRN-2023-001234"
    }
  },
  "amount": 1000.00,
  "currency": "USD",
  "description": "Payment for services",
  "metadata": {
    "processedBy": "TransactionService",
    "source": "API"
  }
}
```

### Event Features
- Complete transaction details with before/after balances
- Customer information for both sender and receiver
- Metadata for tracking and auditing
- Timestamp in ISO format
- Currency information

## Setup Instructions

### Prerequisites
- Docker and Docker Compose
- Java 21+
- Maven 3.6+

### 1. Start Infrastructure Services

The project includes a Docker Compose file that sets up MySQL and Kafka with all required dependencies.

```bash
# Start MySQL and Kafka services
docker-compose up -d

# Verify services are running
docker-compose ps
```

This will start:
- **MySQL 8.0** on port `3306`
- **Apache Kafka** on port `9092`
- **Zookeeper** on port `2181`
- **Kafka UI** on port `8080` (for monitoring)

### 2. Database Configuration

The application is configured to connect to the MySQL database automatically. Database schema will be created on first startup using JPA/Hibernate.

**Connection Details:**
- Host: `localhost:3306`
- Database: `bankdb`
- Username: `bank_user`
- Password: `bank_password`

### 3. Kafka Configuration

Kafka topics are automatically created by the application:
- **Topic:** `transaction`
- **Partitions:** 3
- **Replication Factor:** 1

### 4. Run the Application

```bash
# Build and run the application
mvn clean spring-boot:run

# Or run tests
mvn test
```

### 5. Access Services

- **Application API:** http://localhost:8080/api
- **Kafka UI:** http://localhost:8081 (to monitor Kafka topics and messages)
- **MySQL:** Connect using any MySQL client on port 3306

### 6. Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (clears all data)
docker-compose down -v
```

## Database Schema

### Customers Table
- Individual, Business, VIP customers in one table with Single Table Inheritance
- Each customer type with unique fields

### Accounts Table
- Many-to-One relationship with Customer
- Account types: CHECKING, SAVINGS, BUSINESS, VIP
- Account statuses: ACTIVE, INACTIVE, SUSPENDED, CLOSED

### Transactions Table
- Records all money transfers
- Relationship with source and destination accounts
- Transaction statuses: PENDING, COMPLETED, FAILED, CANCELLED

## Security Features

- Input validation
- Balance verification before transfers
- Prevention of self-transfers
- Account status verification
- Error handling and fault tolerance

## Asynchronous Processing

Events sent to Kafka enable:
- Regulatory reporting
- BI analysis
- Anomaly alerts
- Audit and tracking
- Recurring payment processing
