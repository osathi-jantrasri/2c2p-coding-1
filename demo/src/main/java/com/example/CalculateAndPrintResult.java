package com.example;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class CalculateAndPrintResult {
    public void print(List<Transaction> transactions) {
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
    }
}
