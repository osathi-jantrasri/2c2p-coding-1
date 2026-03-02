package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        //get data from root
        String dataFilePath = "data.json";
        Path filePath = Path.of(dataFilePath);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Transaction> transactions = objectMapper.readValue(
                filePath.toFile(),
                new TypeReference<List<Transaction>>() {
                }
            );

            System.out.println("Payment data from: " + filePath.toAbsolutePath());
            boolean hasValidationErrors = false;
            for (Transaction transaction : transactions) {
                List<String> errors = validateTransaction(transaction);
                if (!errors.isEmpty()) {
                    hasValidationErrors = true;
                    System.out.printf(
                        "Invalid transaction %s -> %s%n",
                        displayTransactionId(transaction.transaction_id()),
                        String.join(", ", errors)
                    );
                }
            }

            if (hasValidationErrors) {
                System.out.println("Validation failed. Statistics were not calculated.");
                return;
            }

            Map<String, DoubleSummaryStatistics> statisticsByCurrency = transactions.stream()
                .map(transaction -> new Transaction(
                    transaction.transaction_id(),
                    transaction.paymentAmount(),
                    transaction.currency().trim(),
                    transaction.created_at()
                ))
                .collect(Collectors.groupingBy(
                    transaction -> transaction.currency().toUpperCase(Locale.ROOT),
                    TreeMap::new,
                    Collectors.summarizingDouble(Transaction::paymentAmount)
                ));

            if (statisticsByCurrency.isEmpty()) {
                System.out.println("No valid currency records found");
                return;
            }

            statisticsByCurrency.forEach((currency, statistics) ->
                System.out.printf(
                    "%s -> min: %.2f, max: %.2f, avg: %.2f, count: %d%n",
                    currency,
                    statistics.getMin(),
                    statistics.getMax(),
                    statistics.getAverage(),
                    statistics.getCount()
                )
            );
        } catch (IOException exception) {
            System.err.println("Failed to read JSON file: " + filePath.toAbsolutePath());
            System.err.println(exception.getMessage());
            System.exit(1);
        }
    }

    private static List<String> validateTransaction(Transaction transaction) {
        List<String> errors = new ArrayList<>();

        if (transaction.paymentAmount() <= 0) {
            errors.add("payment_amount must be greater than 0");
        }

        if (transaction.currency() == null || transaction.currency().isBlank()) {
            errors.add("currency must not be null or blank");
        }

        if (transaction.created_at() == null || transaction.created_at().isBlank()) {
            errors.add("created_at must not be null or blank");
        } else if (!isValidEpochTimestamp(transaction.created_at().trim())) {
            errors.add("created_at must be a valid epoch timestamp (10-digit seconds or 13-digit milliseconds)");
        }

        return errors;
    }

    private static boolean isValidEpochTimestamp(String rawValue) {
        if (!rawValue.matches("^\\d{10}$|^\\d{13}$")) {
            return false;
        }

        try {
            long epochValue = Long.parseLong(rawValue);
            long epochMillis = rawValue.length() == 10 ? Math.multiplyExact(epochValue, 1000L) : epochValue;
            Instant.ofEpochMilli(epochMillis);
            return true;
        } catch (NumberFormatException | ArithmeticException | DateTimeException exception) {
            return false;
        }
    }

    private static String displayTransactionId(String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            return "<missing-transaction-id>";
        }
        return transactionId;
    }

    private record Transaction(
        String transaction_id,
        double payment_amount,
        String currency,
        String created_at
    ) {
        double paymentAmount() {
            return payment_amount;
        }
    }
}
