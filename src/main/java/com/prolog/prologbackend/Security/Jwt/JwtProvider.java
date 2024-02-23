package com.prolog.prologbackend.Security.Jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {
    @Value("${jwt.key}")
    private String key;
    private final RedisRepository redisRepository;


    public String createToken(JwtType jwtType, String email){
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
        Date expirationTime = new Date(System.currentTimeMillis()+jwtType.getExpirationTime());
        String newToken = Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setHeaderParam("typ","JWT")
                .setSubject(email)
                .claim("token_type",jwtType.getTokenType())
                .setIssuer("prolog")
                .setExpiration(expirationTime)
                .compact();
        if(jwtType.equals(JwtType.REFRESH_TOKEN)){
            redisRepository.save(newToken, email, expirationTime.getTime());
        }
        return newToken;
    }

    public String substringToken(String token){
        if(!token.startsWith("Bearer "))
            throw new RuntimeException("JWT 토큰 형식이 올바르지 않습니다.");
        return token.replace("Bearer ", "");
    }

    public Claims parseToken(String token){
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void verifyExpiration(Claims claims){
        if(!claims.getIssuer().equals("prolog"))
            throw new RuntimeException("잘못된 토큰. 잘못된 요청");
        if(!(claims.getExpiration().getTime() > Date.from(Instant.now()).getTime()))
            throw new RuntimeException("만료된 토큰. 재발급 필요");
    }

    public String getEmail(Claims claims){
        return claims.getSubject();
    }

    public void verifyType(JwtType type, Claims claims){
        if(!type.getTokenType().equals(claims.get("token_type")))
            throw new RuntimeException("요청에 맞지 않는 토큰");
    }

    public void verifyWithRedisToken(String token, String email){
        String redisToken = redisRepository.findByEmail(email);
        if(redisToken == null || !token.equals(redisToken)){
            throw new RuntimeException("올바르지 않은 레디스 토큰.");
        }
    }
}
