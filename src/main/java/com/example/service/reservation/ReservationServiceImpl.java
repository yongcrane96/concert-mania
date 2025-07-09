package com.example.service.reservation;

import com.example.domain.concert.Concert;
import com.example.domain.concert.Seat;
import com.example.domain.concert.SeatStatus;
import com.example.domain.reservation.Reservation;
import com.example.domain.reservation.ReservationStatus;
import com.example.domain.user.User;
import com.example.locking.annotation.RedisLock;
import com.example.repository.ReservationRepository;
import com.example.repository.SeatRepository;
import com.example.repository.UserRepository;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;
    private final MeterRegistry meterRegistry;

    public void incrementSeatOccupied() {
        meterRegistry.counter("reservation.seat.occupied").increment();
    }

    public void incrementReservationConfirmed() {
        meterRegistry.counter("reservation.confirmed").increment();
    }

    @Override
    @Transactional
    @RedisLock(key = "'seat:' + #seatId", expire = 10000, waitTime = 3000)
    public void reserveSeat(Long seatId, Long userId) {
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 좌석입니다."));

        Concert concert = seat.getConcert();
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(concert.getOpenAt()) || now.isAfter(concert.getCloseAt())) {
            throw new IllegalStateException("예매 가능한 시간이 아닙니다.");
        }

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new IllegalStateException("이미 점유되었거나 예약된 좌석입니다.");
        }

        seat.occupy(); // 상태 OCCUPIED, 점유 시간 기록
        seatRepository.save(seat);
        incrementSeatOccupied();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Reservation reservation = Reservation.builder()
                .seat(seat)
                .user(user)
                .build();

        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    @CacheEvict(value = "concerts", key = "#reservation.getSeat().concert.id")
    public void payReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("예약을 찾을 수 없습니다."));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("이미 결제된 예약입니다.");
        }

        Seat seat = reservation.getSeat();

        if (seat.getStatus() != SeatStatus.OCCUPIED) {
            throw new IllegalStateException("좌석 상태가 결제 가능한 상태가 아닙니다.");
        }

        // 결제 성공 처리
        reservation.confirm();
        seat.reserve();

        reservationRepository.save(reservation);
        seatRepository.save(seat);

        incrementReservationConfirmed();
    }
}
