package com.example.locking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RedisLock {

    private final StringRedisTemplate redisTemplate;

    public String lock(String key, long expireMillis, long waitMillis) {
        String value = UUID.randomUUID().toString();
        long end = System.currentTimeMillis() + expireMillis;

        while (System.currentTimeMillis() < end) {
            Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(key, value);

            if(Boolean.TRUE.equals(success)) {
                return value;
            }
            try{
                Thread.sleep(100); // 재시도
            }catch(InterruptedException e){
                Thread.currentThread().interrupt();
                break;
            }
        }

        throw new RuntimeException("락 획득 실패");
    }

    public void unlock(String key, String value) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (value.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }

}
