package com.prolog.prologbackend.Security.Jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class RedisRepository {
    private final String tokenType = "Refresh:";
    private final RedisTemplate redisTemplate;

    public void save(String jwt, String email, Long exp){
        redisTemplate.opsForValue().set(email, jwt, exp, TimeUnit.MILLISECONDS);
    }

    public String findByEmail(String user){
        String refreshToken = redisTemplate.opsForValue().get(user).toString();
        if(Objects.isNull(refreshToken))
            throw new RuntimeException("Refresh Token이 존재하지 않습니다. 재로그인이 필요합니다.");
        return refreshToken;
    }
}
