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

**`ValidateData.java`** - Data validation
- Validates all transactions before processing
- Returns detailed error reports with transaction ID and error list
- **Validation Rules:**
  - `transaction_id` must not be null or blank (after trim)
  - `payment_amount` must be > 0 (rejects zero and negative amounts)
  - `currency` must not be null or blank (after trim)
  - `created_at` must not be null or blank (after trim)
  - `created_at` must be valid epoch timestamp (10 digits = seconds, 13 digits = milliseconds)

**`CalculateAndPrintResult.java`** - Statistics and output
- Groups transactions by currency (case-insensitive, normalized to uppercase)
- Calculates min, max, average, and count per currency
- Outputs results to console in alphabetical order by currency

**`Transaction.java`** - Data model (Java record)
- Record representing a single payment transaction
- Fields: `transaction_id`, `payment_amount`, `currency`, `created_at`
- Provides accessor method `paymentAmount()` for calculations

## Running Locally

### Prerequisites
- Java 21 (OpenJDK)
- Maven 3.9.10

### Run Tests
```bash
cd demo
mvn test
```

### Run the Application
```bash
cd demo
mvn -q exec:java
```
Processes `data.json` and prints currency statistics to console.

**Example Output:**
```
Payment data from: /2c2p-coding-1/demo/data.json
THB -> min: 1.29, max: 100.00, avg: 48.07, count: 495
USD -> min: 1.81, max: 99.92, avg: 51.98, count: 505
```

## Docker

### Build the Image
```bash
# From project root
docker build -t payment-stats:latest .
```

### Run the Container
```bash
docker run --rm payment-stats:latest
```

## Error Handling

If validation errors occur, the application will:
1. Print a summary of errors with transaction IDs
2. Exit without processing statistics
3. Report all validation failures in one pass

Example error output:
```
Invalid transaction <missing-transaction-id> -> transaction_id must not be null or blank
Validation failed. Statistics were not calculated.
```

```
Invalid transaction 0f15ed5c-2237-46f8-ab32-2181a7814846 -> payment_amount must be greater than 0 Validation failed. 
Statistics were not calculated.
```

```
Invalid transaction 068d1761-f317-4d7b-b494-bab0d7a8523a -> currency must not be null or blank Validation failed. 
Statistics were not calculated.
```

```
Invalid transaction 624b6d3c-0713-4f8b-914c-b175306ec75a -> created_at must be a valid epoch timestamp (10-digit seconds or 13-digit milliseconds) Validation failed. Statistics were not calculated.
```

## Data Format

`data.json` should contain an array of transaction objects:
```json
[
  {
    "transaction_id":"403f3bed-2d3f-43e3-97a4-b4eb5487ba62",
    "payment_amount":84.4,
    "currency":"USD",
    "created_at":"1763927285000"
  },
  {
    "transaction_id":"c6f8f398-000c-4688-998a-b5c7eeb01582",
    "payment_amount":58.26,
    "currency":"THB",
    "created_at":"1763685352000"
  }
]
```

## Development Notes

- All validation happens before calculation (fail-fast approach)
- Currency grouping is case-insensitive (AAA, aaa, AaA all become AAA)
- Uses double precision for payment amounts
- Epoch timestamps support both 10-digit (seconds) and 13-digit (milliseconds) formats
