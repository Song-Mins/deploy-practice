package com.example.deploy.global.util;


import com.example.deploy.global.type.RedisPrefixType;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j(topic = "Redis Log")
public class RedisUtil<T> {

    private final RedisTemplate redisTemplate;

    /**
     * @param prefixType prefix
     * @param key 키
     * @param value 값 @Auther Domae-back-end
     */
    public void create(RedisPrefixType prefixType, String key, Object value) {
        redisTemplate
                .opsForValue()
                .set(new StringBuilder().append(prefixType).append(key).toString(), value);
    }

    /**
     * @param prefixType prefix
     * @param key 키
     * @param value 값
     * @param timeUnit 시간
     * @param time 시간 단위 @Auther Domae-back-end
     */
    public void create(
            RedisPrefixType prefixType, String key, Object value, Long timeUnit, TimeUnit time) {
        redisTemplate
                .opsForValue()
                .set(
                        new StringBuilder().append(prefixType).append(key).toString(),
                        value,
                        timeUnit,
                        time);
    }

    /**
     * @param prefixType prefix
     * @param key 키
     * @param value 값 @Auther Domae-back-end
     */
    public void update(RedisPrefixType prefixType, String key, Object value) {
        redisTemplate
                .opsForValue()
                .set(new StringBuilder().append(prefixType).append(key).toString(), value);
    }

    /**
     * @param prefixType prefix
     * @param key 키
     * @param value 값
     * @param timeUnit 시간
     * @param time 시간 단위 @Auther Domae-back-end
     */
    public void update(
            RedisPrefixType prefixType, String key, Object value, Long timeUnit, TimeUnit time) {
        redisTemplate
                .opsForValue()
                .set(
                        new StringBuilder().append(prefixType).append(key).toString(),
                        value,
                        timeUnit,
                        time);
    }

    /**
     * @param prefixType prefix
     * @param key 키 @Auther Domae-back-end
     */
    public void delete(RedisPrefixType prefixType, String key) {
        redisTemplate
                .opsForValue()
                .set(
                        new StringBuilder().append(prefixType).append(key).toString(),
                        "",
                        1L,
                        TimeUnit.MICROSECONDS);
    }

    /**
     * @param prefixType prefix
     * @param key 키
     * @param type 클래스 타입
     * @return Optional<T> 반환 @Auther Domae-back-end
     */
    public Optional<T> select(RedisPrefixType prefixType, String key, Class<T> type) {
        Object obj =
                redisTemplate
                        .opsForValue()
                        .get(new StringBuilder().append(prefixType).append(key).toString());
        if (obj == null) return Optional.empty();
        try {
            return Optional.of(type.cast(obj));
        } catch (ClassCastException e) {
            log.error("Casting 실패 : {}", e);
            return Optional.empty();
        }
    }
}
