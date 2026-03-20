# Customer Rewards API

Spring Boot REST API that calculates customer reward points for the latest three months based on recent transactions. 
It exposes endpoints to fetch a per-customer summary.
The data source is an in-memory repository initialized from a JSON configuration file with relative dates.

## Tech Stack
- Language: Java 17
- Framework: Spring Boot 4.0.4
  - spring-boot-starter-webmvc
  - spring-boot-starter-validation
- Build tool: Maven
- Testing Framework: JUnit 5, Mockito

## Project Structure
```text
src/main/java/com/vidyab/rewards/
├── config/             # Configuration classes
├── controller/         # REST Controllers
├── dto/                # Data Transfer Objects
├── exception/          # Custom Exceptions and Global Exception Handler
├── model/              # Domain Models
├── repository/         # In-memory Repository
└── service/            # Business Logic / Service Layer
```

## API Endpoints

### Get summary for a single customer
`GET /api/rewards/summary/{customerId}`

Example Request:
`GET http://localhost:8080/api/rewards/summary/101`

Example Response:
```json
{
  "customerId": 101,
  "customerName": "Vidya Baligar",
  "monthlyRewards": [
    {
      "month": "January 2026",
      "points": 115
    },
    {
      "month": "February 2026",
      "points": 250
    },
    {
      "month": "March 2026",
      "points": 110
    }
  ],
  "totalPoints": 475
}
```

## Reward Calculation Logic

- Amount <= $50 → 0 points
- $50 < Amount <= $100 → 1 point for every dollar over $50 (Max 50 points)
- Amount > $100 → 2 points for every dollar over $100 + 50 points

### Run the application
```bash
mvn clean test
mvn spring-boot:run
```

## Data Source
- Transactions are provided by `InMemoryTransactionRepository` which loads sample data from `src/main/resources/data/transactions.json`. 
- Dates in the JSON are defined as `monthsAgo` to ensure the sample data is always relevant to the current date.
