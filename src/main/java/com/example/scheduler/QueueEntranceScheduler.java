package com.example.scheduler;

import com.example.service.reservationqueue.ReservationQueueServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueueEntranceScheduler {
    private final ReservationQueueServiceImpl queueService;

    // 1초에 5명씩 입장
    @Scheduled(fixedRate = 1000)
    public void allowEntrance() {
        Long concertId = 1L; // 실제로는 여러 콘서트 ID 돌면서 수행
        List<String> entrants = queueService.allowEntrance(concertId, 5);
        for (String session : entrants) {
            log.info("입장 허용: sessionId = {}", session);
            // 입장 세션 처리 로직 작성 가능
        }
    }
}
