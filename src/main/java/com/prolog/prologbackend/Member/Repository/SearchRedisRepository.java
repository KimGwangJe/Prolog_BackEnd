package com.prolog.prologbackend.Member.Repository;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.ExceptionType.MemberExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class SearchRedisRepository {
    private final RedisTemplate redisTemplate;
    private final long CERTIFICATION_TIMEOUT = 300000;

    public void saveCertificationNumber(String email, String code){
        redisTemplate.opsForValue().set("Certification:"+email, code, CERTIFICATION_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void savePasswordCertification(String email){
        redisTemplate.opsForValue().set("Password:"+email, Boolean.toString(true), CERTIFICATION_TIMEOUT, TimeUnit.MILLISECONDS);
    }

    public void validateCertificationNumberByEmail(String user, String code){
        String codeInRedis = redisTemplate.opsForValue().get("Certification:"+user).toString();
        if(Objects.isNull(codeInRedis))
            throw new BusinessLogicException(MemberExceptionType.CODE_NOT_FOUND);
        if(!code.equals(codeInRedis))
            throw new BusinessLogicException(MemberExceptionType.CODE_BAD_REQUEST);
    }

    public boolean findCertificationStatus(String user){
        return redisTemplate.hasKey("Password:"+user).booleanValue();
    }
}
