package com.example.locking.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {
    String key();
    long expire() default 10000; // 락 만료 기간
    long waitTime() default 5000; // 락 대기 시간
}
