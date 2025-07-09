package com.example.scheduler;

import com.example.domain.concert.Seat;
import com.example.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatReleaseScheduler {

    private final SeatRepository seatRepository;

    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void releaseExpiredSeats() {
        LocalDateTime limitTime = LocalDateTime.now().minusMinutes(10);
        List<Seat> expiredSeats = seatRepository.findExpiredOccupiedSeats(limitTime);

        for (Seat seat : expiredSeats) {
            seat.makeAvailable();
        }

        log.info("해제된 좌석 수 : {}", expiredSeats.size());
    }
}
