package com.example.controller.user;

import com.example.service.reservationqueue.ReservationQueueServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Queue", description = "대기열 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/queue")
public class ReservationQueueController {

    private final ReservationQueueServiceImpl queueService;

    // 대기열 등록
    @Operation(summary = "대기열 등록", description = "콘서트 ID와 세션 ID로 대기열에 등록합니다.")
    @PostMapping("/{concertId}")
    public ResponseEntity<String> join(@PathVariable Long concertId,
                                       @RequestParam String sessionId) {
        queueService.joinQueue(concertId, sessionId);
        return ResponseEntity.ok("대기열에 등록되었습니다.");
    }

    // 순번 조회
    @Operation(summary = "대기 순번 조회", description = "자신의 대기 순번을 조회합니다.")
    @GetMapping("/{concertId}/position")
    public ResponseEntity<String> position(@PathVariable Long concertId,
                                           @RequestParam String sessionId) {
        Long pos = queueService.getMyPosition(concertId, sessionId);
        return ResponseEntity.ok("당신의 대기 순번은 " + pos + "번입니다.");
    }
}
