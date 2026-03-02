package com.example;

public record Transaction(
    String transaction_id,
    double payment_amount,
    String currency,
    String created_at
) {
    public double paymentAmount() {
        return payment_amount;
    }
}
