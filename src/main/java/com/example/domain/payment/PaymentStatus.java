package com.example.domain.payment;

public enum PaymentStatus {
    PROCESSING,SUCCESS, FAIL;

    public boolean isSuccess() {
        return this == SUCCESS;
    }
}