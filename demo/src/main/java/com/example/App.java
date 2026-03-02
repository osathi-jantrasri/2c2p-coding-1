package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class App {
    private static final List<String> TARGET_CURRENCIES = List.of("THB", "USD");

    public static void main(String[] args) {
        String dataFilePath = args.length > 0 ? args[0] : "data.json";
        Path filePath = Path.of(dataFilePath);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            List<Transaction> transactions = objectMapper.readValue(
                filePath.toFile(),
                new TypeReference<List<Transaction>>() {
                }
            );

            System.out.println("Payment amount statistics from: " + filePath.toAbsolutePath());
            for (String currency : TARGET_CURRENCIES) {
                DoubleSummaryStatistics statistics = transactions.stream()
                    .filter(transaction -> currency.equalsIgnoreCase(transaction.currency()))
                    .mapToDouble(Transaction::paymentAmount)
                    .summaryStatistics();

                if (statistics.getCount() == 0) {
                    System.out.printf("%s -> no records found%n", currency);
                } else {
                    System.out.printf(
                        "%s -> min: %.2f, max: %.2f, avg: %.2f, count: %d%n",
                        currency,
                        statistics.getMin(),
                        statistics.getMax(),
                        statistics.getAverage(),
                        statistics.getCount()
                    );
                }
            }
        } catch (IOException exception) {
            System.err.println("Failed to read JSON file: " + filePath.toAbsolutePath());
            System.err.println(exception.getMessage());
            System.exit(1);
        }
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
