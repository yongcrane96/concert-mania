package com.example.service.payment;

import com.example.domain.concert.Seat;
import com.example.domain.payment.Payment;
import com.example.domain.payment.PaymentStatus;
import com.example.domain.reservation.Reservation;
import com.example.eventlog.PaymentEventLogService;
import com.example.repository.PaymentRepository;
import com.example.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentEventLogService paymentEventLogService;
    private final PaymentProcessor paymentProcessor;

    @Override
    @Transactional
    public Payment createPayment(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        // 외부 결제 API 모킹
        boolean success = paymentProcessor.process();

        Payment payment = Payment.builder()
                .reservation(reservation)
                .status(PaymentStatus.PROCESSING)
                .paidAt(null)
                .build();

        paymentRepository.save(payment);

        if(success) {
            payment.markSuccess();
            reservation.confirm();
            log.info("결제 성공 - reservationId={}, paymentId={}", reservationId, payment.getId());
        }else {
            payment.markFail();
            reservation.cancel();

            Seat seat = reservation.getSeat();
            seat.makeAvailable();
            log.warn("결제 실패 - reservationId={}, paymentId={}", reservationId, payment.getId());
        }

        paymentEventLogService.savePaymentEventLog(payment);

        return payment;
    }
}
