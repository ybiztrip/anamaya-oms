package ai.anamaya.service.oms.core.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisLockManager {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RedisLockManager(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean acquireLock(String key, Duration ttl) {
        Boolean success = redisTemplate.opsForValue()
            .setIfAbsent(key, "1", ttl);
        return !Boolean.TRUE.equals(success);
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }

    public String bookingLockKey(String bookingCode) {
        return "booking:lock:" + bookingCode;
    }
}

