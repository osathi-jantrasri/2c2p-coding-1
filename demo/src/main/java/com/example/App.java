package com.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Path;
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
            Map<String, DoubleSummaryStatistics> statisticsByCurrency = transactions.stream()
                .filter(transaction -> transaction.currency() != null)
                .map(transaction -> new Transaction(
                    transaction.transaction_id(),
                    transaction.paymentAmount(),
                    transaction.currency().trim(),
                    transaction.created_at()
                ))
                .filter(transaction -> !transaction.currency().isBlank())
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
