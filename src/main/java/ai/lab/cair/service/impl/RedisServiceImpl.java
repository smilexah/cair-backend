package ai.lab.cair.service.impl;

import ai.lab.cair.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void setTokenWithTTL(String key, String value, long ttl, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, ttl, timeUnit);
    }

    @Override
    public boolean hasToken(String token) {
        return stringRedisTemplate.hasKey(token);
    }
}