package com.example;

import org.junit.Before;
import org.junit.Test;
import org.junit.Rule;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.util.List;

import static org.junit.Assert.*;

public class CalculateAndPrintResultTest {
    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().enableLog();

    private CalculateAndPrintResult calculator;

    @Before
    public void setUp() {
        calculator = new CalculateAndPrintResult();
    }

    @Test
    public void testPrintSingleCurrency() {
        List<Transaction> transactions = List.of(
            new Transaction("1", 10.0, "USD", "1751512490000"),
            new Transaction("2", 20.0, "USD", "1751512500000"),
            new Transaction("3", 30.0, "USD", "1751512510000")
        );

        calculator.print(transactions);
        String output = systemOutRule.getLog();

        assertTrue(output.contains("USD"));
        assertTrue(output.contains("min: 10.00"));
        assertTrue(output.contains("max: 30.00"));
        assertTrue(output.contains("avg: 20.00"));
        assertTrue(output.contains("count: 3"));
    }

    @Test
    public void testPrintMultipleCurrencies() {
        List<Transaction> transactions = List.of(
            new Transaction("1", 10.0, "USD", "1751512490000"),
            new Transaction("2", 20.0, "USD", "1751512500000"),
            new Transaction("3", 50.0, "THB", "1751512510000"),
            new Transaction("4", 100.0, "THB", "1751512520000")
        );

        calculator.print(transactions);
        String output = systemOutRule.getLog();

        assertTrue(output.contains("USD"));
        assertTrue(output.contains("THB"));
        assertTrue(output.contains("15.00")); // USD avg
        assertTrue(output.contains("75.00")); // THB avg
    }

    @Test
    public void testPrintEmptyList() {
        calculator.print(List.of());
        String output = systemOutRule.getLog();

        assertTrue(output.contains("No valid currency records found"));
    }

    @Test
    public void testPrintSingleTransaction() {
        List<Transaction> transactions = List.of(
            new Transaction("1", 42.5, "EUR", "1751512490000")
        );

        calculator.print(transactions);
        String output = systemOutRule.getLog();

        assertTrue(output.contains("EUR"));
        assertTrue(output.contains("min: 42.50"));
        assertTrue(output.contains("max: 42.50"));
        assertTrue(output.contains("avg: 42.50"));
        assertTrue(output.contains("count: 1"));
    }

    @Test
    public void testPrintCurrenciesSorted() {
        List<Transaction> transactions = List.of(
            new Transaction("1", 10.0, "ZZZ", "1751512490000"),
            new Transaction("2", 20.0, "AAA", "1751512500000"),
            new Transaction("3", 30.0, "MMM", "1751512510000")
        );

        calculator.print(transactions);
        String output = systemOutRule.getLog();

        // Verify all currencies are present
        assertTrue(output.contains("AAA"));
        assertTrue(output.contains("MMM"));
        assertTrue(output.contains("ZZZ"));

        // Verify they appear in alphabetical order
        int aaaIndex = output.indexOf("AAA");
        int mmmIndex = output.indexOf("MMM");
        int zzzIndex = output.indexOf("ZZZ");

        assertTrue(aaaIndex < mmmIndex && mmmIndex < zzzIndex);
    }

    @Test
    public void testPrintLargeValues() {
        List<Transaction> transactions = List.of(
            new Transaction("1", 9999.99, "USD", "1751512490000"),
            new Transaction("2", 0.01, "USD", "1751512500000")
        );

        calculator.print(transactions);
        String output = systemOutRule.getLog();

        assertTrue(output.contains("USD"));
        assertTrue(output.contains("min: 0.01"));
        assertTrue(output.contains("max: 9999.99"));
    }

    @Test
    public void testPrintCaseSensitiveCurrency() {
        List<Transaction> transactions = List.of(
            new Transaction("1", 10.0, "usd", "1751512490000"),
            new Transaction("2", 20.0, "USD", "1751512500000"),
            new Transaction("3", 30.0, "UsD", "1751512510000")
        );

        calculator.print(transactions);
        String output = systemOutRule.getLog();

        // Should be normalized to uppercase and grouped
        assertTrue(output.contains("USD"));
        // Verify average is correct (60/3 = 20)
        assertTrue(output.contains("avg: 20.00"));
        assertTrue(output.contains("count: 3"));
    }
}
