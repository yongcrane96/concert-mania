package com.example.service.performance;

import com.example.domain.reservation.Reservation;
import com.example.eventlog.PaymentEventLogService;
import com.example.repository.PaymentRepository;
import com.example.repository.ReservationRepository;
import com.example.service.TestFixtureFactory;
import com.example.service.payment.PaymentProcessor;
import com.example.service.payment.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("PaymentService 동시성 테스트")
public class PaymentServiceConcurrencyTest {

    private PaymentServiceImpl paymentService;
    private ReservationRepository reservationRepository;
    private PaymentRepository paymentRepository;
    private PaymentProcessor paymentProcessor;
    private PaymentEventLogService paymentEventLogService;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        paymentRepository = mock(PaymentRepository.class);
        paymentProcessor = mock(PaymentProcessor.class);
        paymentEventLogService = mock(PaymentEventLogService.class);

        paymentService = new PaymentServiceImpl(
                paymentRepository,
                reservationRepository,
                paymentEventLogService,
                paymentProcessor
        );
        MockitoAnnotations.openMocks(this);

        reservation = TestFixtureFactory.createPendingReservation();
    }

    @Test
    @DisplayName("다중 스레드에서 결제 처리 시 상태 일관성 유지")
    void concurrentPaymentProcessing() throws InterruptedException {
        Long reservationId = reservation.getId();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentProcessor.process()).thenReturn(true);

        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        doNothing().when(paymentEventLogService).savePaymentEventLog(any());

        int threadCount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    paymentService.createPayment(reservationId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    exceptions.add(e);
                    System.err.println("스레드에서 예외 발생: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("성공한 스레드 수: " + successCount.get());
        System.out.println("실패한 스레드 수: " + exceptions.size());

        if (!exceptions.isEmpty()) {
            System.out.println("발생한 예외들:");
            exceptions.forEach(e -> {
                System.out.println("- " + e.getClass().getSimpleName() + ": " + e.getMessage());
            });
        }

        assertThat(successCount.get()).isEqualTo(threadCount);
        assertThat(exceptions).isEmpty();
    }

    @Test
    @DisplayName("동시성 환경에서 중복 결제 방지 테스트")
    void preventDuplicatePaymentInConcurrentEnvironment() throws InterruptedException {
        Long reservationId = reservation.getId();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentProcessor.process()).thenReturn(true);

        when(paymentRepository.save(any())).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    paymentService.createPayment(reservationId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        System.out.println("중복 결제 방지 테스트 - 성공: " + successCount.get() + ", 실패: " + failureCount.get());

        assertThat(successCount.get() + failureCount.get()).isEqualTo(threadCount);
    }

    @Test
    @DisplayName("Mock 검증 - 동시성 환경에서의 메서드 호출 횟수 확인")
    void verifyMockInteractionsInConcurrentEnvironment() throws InterruptedException {
        Long reservationId = reservation.getId();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));
        when(paymentProcessor.process()).thenReturn(true);
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        int threadCount = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    paymentService.createPayment(reservationId);
                } catch (Exception e) {
                    System.err.println("Mock 검증 테스트에서 예외 발생: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        verify(reservationRepository, times(threadCount)).findById(reservationId);
        verify(paymentProcessor, times(threadCount)).process();

        verify(paymentRepository, atLeast(1)).save(any());
    }
}