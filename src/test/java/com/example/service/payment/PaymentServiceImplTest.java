package com.example.service.payment;


import com.example.domain.payment.Payment;
import com.example.domain.payment.PaymentStatus;
import com.example.domain.reservation.Reservation;
import com.example.eventlog.PaymentEventLogService;
import com.example.repository.PaymentRepository;
import com.example.repository.ReservationRepository;
import com.example.service.TestFixtureFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentServiceImpl 단위 테스트")
class PaymentServiceImplTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private PaymentEventLogService paymentEventLogService;
    @Mock private PaymentProcessor paymentProcessor;

    @InjectMocks private PaymentServiceImpl paymentService;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = TestFixtureFactory.createPendingReservation();
    }

    @Nested
    @DisplayName("createPayment() 테스트")
    class CreatePayment {

        @Test
        @DisplayName("결제 성공 시 Payment 저장 및 예약 상태 CONFIRMED로 변경")
        void createPaymentSuccess() {
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(paymentProcessor.process()).thenReturn(true);

            Payment result = paymentService.createPayment(1L);

            assertThat(result.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
            verify(reservationRepository).findById(1L);
            verify(paymentRepository).save(any(Payment.class));
            verify(paymentEventLogService).savePaymentEventLog(any(Payment.class));
            assertThat(reservation.getStatus().name()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("결제 실패 시 예약 상태 CANCELLED 및 좌석 AVAILABLE")
        void createPaymentFail() {
            when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
            when(paymentProcessor.process()).thenReturn(false);

            Payment result = paymentService.createPayment(1L);

            assertThat(result.getStatus()).isEqualTo(PaymentStatus.FAIL);
            verify(paymentRepository).save(any(Payment.class));
            verify(paymentEventLogService).savePaymentEventLog(any(Payment.class));
            assertThat(reservation.getStatus().name()).isEqualTo("CANCELLED");
            assertThat(reservation.getSeat().getStatus().name()).isEqualTo("AVAILABLE");

            verify(paymentRepository).save(any(Payment.class));
            verify(paymentEventLogService).savePaymentEventLog(any(Payment.class));
        }

        @Test
        @DisplayName("예약 ID가 존재하지 않으면 예외 발생")
        void reservationNotFound() {
            when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.createPayment(999L))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("예약을 찾을 수 없습니다.");
        }
    }
}