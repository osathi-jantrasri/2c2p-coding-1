package com.example;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ValidateData {
    public List<ValidationError> validate(List<Transaction> transactions) {
        List<ValidationError> validationErrors = new ArrayList<>();

        for (Transaction transaction : transactions) {
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

            if (!errors.isEmpty()) {
                validationErrors.add(new ValidationError(displayTransactionId(transaction.transaction_id()), errors));
            }
        }

        return validationErrors;
    }

    private boolean isValidEpochTimestamp(String rawValue) {
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

    private String displayTransactionId(String transactionId) {
        if (transactionId == null || transactionId.isBlank()) {
            return "<missing-transaction-id>";
        }
        return transactionId;
    }

    public record ValidationError(String transactionId, List<String> errors) {
    }
}
