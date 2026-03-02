# Payment Statistics Calculator

A Java console application that reads transaction data from a JSON file, validates it, and calculates payment statistics (min, max, average) grouped by currency.

## Project Overview

This console application processes payment transaction records and provides statistical analysis by currency. It features comprehensive data validation including payment amount checks, null/blank field detection, and epoch timestamp format verification.

**Technology Stack:**
- Java 21 (OpenJDK)
- Maven 3.9.10
- Jackson 2.19.2 (JSON parsing)
- JUnit 4.13.2 (unit testing)

## Project Structure

### Core Classes

**`App.java`** - Entry point
- Orchestrates the data pipeline: loads → validates → calculates
- Exits early if validation fails with detailed error messages

**`GetData.java`** - Data loading
- Reads `data.json` from the application root
- Uses Jackson ObjectMapper to parse JSON into Transaction objects
- Throws `IOException` if file is missing or malformed

**`Transaction.java`** - Data model (Java record)
- Immutable record representing a single payment transaction
- Fields: `transaction_id`, `payment_amount`, `currency`, `created_at`
- Provides accessor method `paymentAmount()` for calculations

**`ValidateData.java`** - Data validation
- Validates all transactions before processing
- Returns detailed error reports with transaction ID and error list
- **Validation Rules:**
  - `payment_amount` must be > 0 (rejects zero and negative amounts)
  - `currency` must not be null or blank (after trim)
  - `created_at` must not be null or blank (after trim)
  - `created_at` must be valid epoch timestamp (10 digits = seconds, 13 digits = milliseconds)

**`CalculateAndPrintResult.java`** - Statistics and output
- Groups transactions by currency (case-insensitive, normalized to uppercase)
- Calculates min, max, average, and count per currency
- Outputs results to console in alphabetical order by currency

## Running Locally

### Prerequisites
- Java 21 (OpenJDK)
- Maven 3.9.10
- `data.json` file in the `demo/` directory

### Run Tests
```bash
cd demo
mvn test
```
Executes 27 unit tests covering all core functionality (4 test classes).

### Run the Application
```bash
cd demo
mvn -q exec:java
```
Processes `data.json` and prints currency statistics to stdout.

**Example Output:**
```
THB -> min: 50.00, max: 100.00, avg: 75.00, count: 2
USD -> min: 10.00, max: 30.00, avg: 20.00, count: 3
```

## Docker

### Build the Image
```bash
# From project root
docker build -t payment-stats:latest demo/
```
Creates a lightweight multi-stage Docker image (~200MB) with Java 21 Alpine JRE.

### Run the Container
```bash
docker run --rm payment-stats:latest
```
Executes the application inside the container and prints results.

**Mount Local Data:**
```bash
docker run --rm -v $(pwd)/demo/data.json:/app/data.json payment-stats:latest
```
Takes `data.json` from the host filesystem instead of the image.

## Sample Data Format

`data.json` should contain an array of transaction objects:
```json
[
  {
    "transaction_id": "TXN001",
    "payment_amount": 10.50,
    "currency": "USD",
    "created_at": "1704067200"
  },
  {
    "transaction_id": "TXN002",
    "payment_amount": 100.00,
    "currency": "THB",
    "created_at": "1704153600000"
  }
]
```

## Error Handling

If validation errors occur, the application will:
1. Print a summary of errors with transaction IDs
2. Exit without processing statistics
3. Report all validation failures in one pass

Example error output:
```
[ERROR] Validation failed for transaction TXN003:
  - payment_amount must be greater than 0
  - currency cannot be blank

[ERROR] Validation failed for transaction TXN005:
  - created_at is not a valid epoch timestamp
```

## Build Output

- **JAR File:** `target/demo.jar` (fat JAR with all dependencies included)
- **Test Reports:** `target/surefire-reports/` (detailed test execution reports)

## Development Notes

- All validation happens before calculation (fail-fast approach)
- Currency grouping is case-insensitive (AAA, aaa, AaA all become AAA)
- Uses double precision for payment amounts
- Epoch timestamps support both 10-digit (seconds) and 13-digit (milliseconds) formats
