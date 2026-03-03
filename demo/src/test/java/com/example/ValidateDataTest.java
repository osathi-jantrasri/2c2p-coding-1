package com.example;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ValidateDataTest {
    private ValidateData validateData;

    @Before
    public void setUp() {
        validateData = new ValidateData();
    }

    @Test
    public void testValidateValidTransaction() {
        Transaction transaction = new Transaction("1", 100.0, "USD", "1751512490000");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateNegativePaymentAmount() {
        Transaction transaction = new Transaction("1", -50.0, "USD", "1751512490000");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).errors().toString().contains("payment_amount must be greater than 0"));
    }

    @Test
    public void testValidateZeroPaymentAmount() {
        Transaction transaction = new Transaction("1", 0.0, "USD", "1751512490000");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).errors().toString().contains("payment_amount must be greater than 0"));
    }

    @Test
    public void testValidateNullCurrency() {
        Transaction transaction = new Transaction("1", 50.0, null, "1751512490000");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).errors().toString().contains("currency must not be null or blank"));
    }

    @Test
    public void testValidateBlankCurrency() {
        Transaction transaction = new Transaction("1", 50.0, "  ", "1751512490000");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).errors().toString().contains("currency must not be null or blank"));
    }

    @Test
    public void testValidateNullCreatedAt() {
        Transaction transaction = new Transaction("1", 50.0, "USD", null);
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).errors().toString().contains("created_at must not be null or blank"));
    }

    @Test
    public void testValidateBlankCreatedAt() {
        Transaction transaction = new Transaction("1", 50.0, "USD", "");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).errors().toString().contains("created_at must not be null or blank"));
    }

    @Test
    public void testValidateInvalidEpochTimestampWithLetters() {
        Transaction transaction = new Transaction("1", 50.0, "USD", "175151249abc0");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).errors().toString().contains("created_at must be a valid epoch timestamp"));
    }

    @Test
    public void testValidateInvalidEpochTimestampTooShort() {
        Transaction transaction = new Transaction("1", 50.0, "USD", "123456");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).errors().toString().contains("created_at must be a valid epoch timestamp"));
    }

    @Test
    public void testValidateInvalidEpochTimestampTooLong() {
        Transaction transaction = new Transaction("1", 50.0, "USD", "12345678901234");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).errors().toString().contains("created_at must be a valid epoch timestamp"));
    }

    @Test
    public void testValidate10DigitEpochTimestamp() {
        Transaction transaction = new Transaction("1", 50.0, "USD", "1751512490");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidate13DigitEpochTimestamp() {
        Transaction transaction = new Transaction("1", 50.0, "USD", "1751512490000");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertTrue(errors.isEmpty());
    }

    @Test
    public void testValidateMultipleErrors() {
        Transaction transaction = new Transaction("1", -10.0, null, null);
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertEquals(3, errors.get(0).errors().size());
    }

    @Test
    public void testValidateMultipleTransactions() {
        List<Transaction> transactions = List.of(
            new Transaction("1", 50.0, "USD", "1751512490000"),
            new Transaction("2", -10.0, "USD", "1751512490000"),
            new Transaction("3", 75.0, null, "1751512490000")
        );
        List<ValidateData.ValidationError> errors = validateData.validate(transactions);

        assertEquals(2, errors.size());
    }

    @Test
    public void testValidateNullTransactionId() {
        Transaction transaction = new Transaction(null, 50.0, "USD", "1751512490000");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).errors().toString().contains("transaction_id must not be null or blank"));
    }

    @Test
    public void testValidateBlankTransactionId() {
        Transaction transaction = new Transaction("  ", 50.0, "USD", "1751512490000");
        List<ValidateData.ValidationError> errors = validateData.validate(List.of(transaction));

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).errors().toString().contains("transaction_id must not be null or blank"));
    }
}
