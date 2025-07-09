package com.example.service.reservationqueue;

import java.util.List;

public interface ReservationQueueService {
    void joinQueue(Long concertId, String sessionId);
    long getMyPosition(Long concertId, String sessionId);
    List<String> allowEntrance(Long concertId, int count);
    Long getTotalWaitingCount(Long concertId);
}