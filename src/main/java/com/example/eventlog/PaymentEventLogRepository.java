package com.example.eventlog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentEventLogRepository extends JpaRepository<PaymentEventLog, Long> {
}