package com.example.service.reservation;

import com.example.domain.concert.Concert;
import com.example.domain.concert.Seat;
import com.example.domain.reservation.Reservation;
import com.example.domain.user.User;
import com.example.repository.ReservationRepository;
import com.example.repository.SeatRepository;
import com.example.repository.UserRepository;
import com.example.service.TestFixtureFactory;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService 단위 테스트")
public class ReservationServiceImplTest {
    @Mock private SeatRepository seatRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private MeterRegistry meterRegistry;
    @Mock private UserRepository userRepository;
    @InjectMocks private ReservationServiceImpl reservationService;

    private Seat seat;
    private Reservation reservation;
    private User user;
    private Counter mockCounter;

    @BeforeEach
    void setUp() {
        Concert concert = TestFixtureFactory.createTestConcert();
        seat = TestFixtureFactory.createTestSeat(concert);
        user = TestFixtureFactory.createTestUser();
        reservation = TestFixtureFactory.createTestReservation(user, seat);

        mockCounter = mock(Counter.class);
        lenient().when(meterRegistry.counter(anyString())).thenReturn(mockCounter);
        lenient().when(meterRegistry.counter(anyString(), any(String[].class))).thenReturn(mockCounter);
    }

    @Test
    @DisplayName("좌석 임시 점유 성공")
    void reserveSeatSuccess() {
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        when(reservationRepository.save(any())).thenReturn(reservation);

        reservationService.reserveSeat(1L, 10L);

        verify(reservationRepository).save(any(Reservation.class));
        verify(seatRepository).save(any(Seat.class));
        verify(userRepository).findById(10L);
    }

    @Test
    @DisplayName("존재하지 않는 좌석 ID 요청 시 예외 발생")
    void reserveSeatFailNoSeat() {
        when(seatRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.reserveSeat(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 좌석입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID 요청 시 예외 발생")
    void reserveSeatFailNoUser() {
        when(seatRepository.findById(1L)).thenReturn(Optional.of(seat));
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.reserveSeat(1L, 10L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 사용자입니다.");
    }
}
