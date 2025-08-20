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
  "transactionId": "TXN1691234567ABC123",
  "fromAccountNumber": "ACC1691234567001",
  "toAccountNumber": "ACC1691234567002",
  "amount": 1000.00,
  "transactionType": "TRANSFER",
  "status": "COMPLETED",
  "description": "Payment for services",
  "timestamp": "2025-08-14T10:30:00",
  "customerId": "1",
  "eventType": "TRANSFER_COMPLETED"
}
```

### Event Types
- `TRANSFER_COMPLETED` - Transfer completed successfully
- `TRANSFER_FAILED` - Transfer failed

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

## Running the Application

### Prerequisites
1. **MySQL Database**: Create a database named `bankdb`
2. **Kafka Server**: Install and run Kafka

### Setup MySQL
```sql
CREATE DATABASE bankdb;
CREATE USER 'bankuser'@'localhost' IDENTIFIED BY 'bankpass';
GRANT ALL PRIVILEGES ON bankdb.* TO 'bankuser'@'localhost';
FLUSH PRIVILEGES;
```

### Start Kafka
```bash
# Start Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka
bin/kafka-server-start.sh config/server.properties
```

### Run the Application
```bash
mvn spring-boot:run
```

### Database Access
- **MySQL Connection**: `jdbc:mysql://localhost:3306/bankdb`
- **Username**: `bankuser`
- **Password**: `bankpass`

## Configuration

Update `application.properties` with your MySQL configuration:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bankdb
spring.datasource.username=bankuser
spring.datasource.password=bankpass
```

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
