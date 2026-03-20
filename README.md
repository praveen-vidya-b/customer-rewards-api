# Customer Rewards API

Spring Boot REST API that calculates customer reward points for the latest three months based on recent transactions. 
It exposes endpoints to fetch an overall summary for a per-customer summary. 
The data source is an in-memory repository with sample data .

## Tech Stack
- Language: Java 17
- Framework: Spring Boot 4.0.4
  - spring-boot-starter-webmvc
  - spring-boot-starter-validation
- Build tool / Package manager: Maven
- Testing Framework: Junit 5

## API Endpoints

### Get summary for a single customer
`GET /api/rewards/summary/{customerId}`

## Reward Calculation Logic

- Amount <= 50 → 0 points
- 50 < Amount <= 100 → Amount-50
- Amount > 100 → 50 + (Amount - 100) * 2

### Run the application
```bash
mvn clean test
mvn spring-boot:run
```

## Data Source
- Transactions are provided by an in-memory repository (`InMemoryTransactionRepository`) with sample data spanning three months (currently October–December 2026). There is no external database required.