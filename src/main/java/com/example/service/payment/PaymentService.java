package com.example.service.payment;

import com.example.domain.payment.Payment;

public interface PaymentService {
    Payment createPayment(Long reservationId);
}
