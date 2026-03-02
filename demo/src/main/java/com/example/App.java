package com.example;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class App {
    public static void main(String[] args) {
        String dataFilePath = "data.json";
        Path filePath = Path.of(dataFilePath);

        GetData getData = new GetData();
        ValidateData validateData = new ValidateData();
        CalculateAndPrintResult calculateAndPrintResult = new CalculateAndPrintResult();

        try {
            List<Transaction> transactions = getData.load(filePath);

            System.out.println("Payment data from: " + filePath.toAbsolutePath());
            List<ValidateData.ValidationError> validationErrors = validateData.validate(transactions);

            if (!validationErrors.isEmpty()) {
                validationErrors.forEach(error ->
                    System.out.printf(
                        "Invalid transaction %s -> %s%n",
                        error.transactionId(),
                        String.join(", ", error.errors())
                    )
                );
                System.out.println("Validation failed. Statistics were not calculated.");
                return;
            }

            calculateAndPrintResult.print(transactions);
        } catch (IOException exception) {
            System.err.println("Failed to read JSON file: " + filePath.toAbsolutePath());
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }
}
