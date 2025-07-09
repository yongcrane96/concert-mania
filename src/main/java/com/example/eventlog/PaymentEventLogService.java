package com.example.eventlog;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentEventLogService {

    private final PaymentEventLogRepository repository;
    private final ObjectMapper objectMapper;

    public void savePaymentEventLog(com.example.domain.payment.Payment payment) {
        try {
            String payload = objectMapper.writeValueAsString(payment);
            PaymentEventLog log = PaymentEventLog.fromDomain(payment, payload);
            repository.save(log);
        } catch (JsonProcessingException e) {
            // 로깅 처리
            e.printStackTrace();
        }
    }
}
