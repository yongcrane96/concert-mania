package com.example.service.payment;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomPaymentProcessor implements PaymentProcessor {
    private final Random rand = new Random();

    @Override
    public boolean process(){
        return rand.nextBoolean();
    }
}
