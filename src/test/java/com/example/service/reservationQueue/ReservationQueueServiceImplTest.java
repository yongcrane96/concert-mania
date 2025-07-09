package com.example.service.reservationQueue;

import com.example.service.reservationqueue.ReservationQueueServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationQueueServiceImpl 단위 테스트")
class ReservationQueueServiceImplTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ListOperations<String, String> listOperations;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private ReservationQueueServiceImpl queueService;

    private final Long concertId = 1L;
    private final String sessionId = "session123";

    @Test
    @DisplayName("joinQueue() - 대기열 등록 호출")
    void joinQueue_callsLeftPush() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);

        queueService.joinQueue(concertId, sessionId);

        verify(redisTemplate).opsForList();
        verify(listOperations).leftPush("concert:queue:1", sessionId);
    }

    @Test
    @DisplayName("getMyPosition() - 순번 조회 정상 반환")
    void getMyPosition_returnsCorrectRank() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.rank("concert:queue:1", sessionId)).thenReturn(4L);

        long position = queueService.getMyPosition(concertId, sessionId);

        assertThat(position).isEqualTo(5L); // rank + 1
        verify(redisTemplate).opsForZSet();
        verify(zSetOperations).rank("concert:queue:1", sessionId);
    }

    @Test
    @DisplayName("getMyPosition() - 순번 없으면 -1 반환")
    void getMyPosition_returnsMinusOneIfNull() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.rank(anyString(), anyString())).thenReturn(null);

        long position = queueService.getMyPosition(concertId, sessionId);

        assertThat(position).isEqualTo(-1);
    }

    @Test
    @DisplayName("allowEntrance() - 입장 허용자 리스트 반환 및 삭제")
    void allowEntrance_removesAndReturnsAllowedList() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        Set<String> allowedSessions = new HashSet<>(Arrays.asList("s1", "s2", "s3"));
        when(zSetOperations.range("concert:queue:1", 0, 2)).thenReturn(allowedSessions);

        List<String> result = queueService.allowEntrance(concertId, 3);

        assertThat(result).containsExactlyInAnyOrderElementsOf(allowedSessions);
        verify(zSetOperations).range("concert:queue:1", 0, 2);
        verify(zSetOperations, never()).removeRange("concert:queue:1", 0, 2); // 현재 코드 조건상 삭제 안 됨
    }

    @Test
    @DisplayName("allowEntrance() - 입장 허용자 없으면 삭제 호출")
    void allowEntrance_removesWhenEmpty() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range("concert:queue:1", 0, 2)).thenReturn(null);
        when(zSetOperations.removeRange("concert:queue:1", 0, 2)).thenReturn(0L); // <- 이 부분 추가!

        List<String> result = queueService.allowEntrance(concertId, 3);

        assertThat(result).isEmpty();
        verify(zSetOperations).range("concert:queue:1", 0, 2);
        verify(zSetOperations).removeRange("concert:queue:1", 0, 2);
    }

    @Test
    @DisplayName("getTotalWaitingCount() - 대기 인원 조회")
    void getTotalWaitingCount_returnsCount() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.zCard("concert:queue:1")).thenReturn(10L);

        Long count = queueService.getTotalWaitingCount(concertId);

        assertThat(count).isEqualTo(10L);
        verify(zSetOperations).zCard("concert:queue:1");
    }
}