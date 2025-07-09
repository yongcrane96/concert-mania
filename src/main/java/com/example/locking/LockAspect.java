package com.example.locking;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LockAspect {

    private final ExpressionParser parser = new SpelExpressionParser();
    private final RedisLock redisLock;

    public LockAspect(RedisLock redisLock) {
        this.redisLock = redisLock;
    }


    @Around("@annotation(redisLockAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint,
                         com.example.locking.annotation.RedisLock redisLockAnnotation) throws Throwable {
        //SpEL key 파싱
        String keyExpression = redisLockAnnotation.key();
        StandardEvaluationContext context = new StandardEvaluationContext();

        Object[] args = joinPoint.getArgs();
        String[] paramNames = ((org.aspectj.lang.reflect.CodeSignature) joinPoint.getSignature()).getParameterNames();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        String key = parser.parseExpression(keyExpression).getValue(context, String.class);

        String lockValue = null;

        try{
            lockValue = redisLock.lock(key, redisLockAnnotation.expire(), redisLockAnnotation.waitTime());
            return joinPoint.proceed();
        } finally {
            if(lockValue != null){
                redisLock.unlock(key, lockValue);
            }
        }

    }
}
