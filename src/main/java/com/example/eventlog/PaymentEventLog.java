package com.example.eventlog;

import com.example.domain.payment.Payment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_event_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaymentEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long paymentId;

    private String status;

    private LocalDateTime paidAt;

    private LocalDateTime occurredAt;

    private LocalDateTime processedAt;

    @Lob
    private String payload;

    public static PaymentEventLog fromDomain(Payment payment, String payload) {
        return PaymentEventLog.builder()
                .paymentId(payment.getId())
                .status(payment.getStatus().name())
                .paidAt(payment.getPaidAt())
                .occurredAt(LocalDateTime.now())
                .payload(payload)
                .processedAt(null)
                .build();
    }
}
