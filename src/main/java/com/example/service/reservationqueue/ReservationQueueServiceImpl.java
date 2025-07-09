package com.example.service.reservationqueue;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReservationQueueServiceImpl implements ReservationQueueService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String RESERVATION_QUEUE = "concert:queue";

    private String getQueueKey(Long concertId) {
        return RESERVATION_QUEUE + ":" + concertId;
    }

    // 대기열 등록
    @Override
    public void joinQueue(Long concertId, String sessionId) {
        String key = getQueueKey(concertId);
        long now = System.currentTimeMillis();
        redisTemplate.opsForList().leftPush(key, sessionId);
    }

    // 나의 순번 조회
    @Override
    public long getMyPosition(Long concertId, String sessionId) {
        String key = getQueueKey(concertId);
        Long rank = redisTemplate.opsForZSet().rank(key, sessionId);
        return rank == null ? -1 : rank + 1;
    }

    // 입장 허용 : 앞 n명 반환 후 삭제
    @Override
    public List<String> allowEntrance(Long concertId, int count) {
        String key = getQueueKey(concertId);
        Set<String> allowList = redisTemplate.opsForZSet().range(key, 0, count -1);

        if(allowList == null || allowList.isEmpty()){
            redisTemplate.opsForZSet().removeRange(key, 0, count - 1);
            return Collections.emptyList();
        }

        return new ArrayList<>(allowList);
    }

    // 전체 대기 인원 조회
    @Override
    public Long getTotalWaitingCount(Long concertId) {
        String key = getQueueKey(concertId);
        return redisTemplate.opsForZSet().zCard(key);
    }
}
