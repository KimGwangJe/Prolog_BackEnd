package com.prolog.prologbackend.Security.Jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.key}")
    private String key;

    public String createToken(JwtType type, String email){
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(key));
        return Jwts.builder()
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .setHeaderParam("typ","JWT")
                .setSubject(type.getTokenType())
                .claim("email",email)
                .setExpiration(new Date(System.currentTimeMillis()+type.getExpirationTime()))
                .compact();
    }
}
