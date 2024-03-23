package com.prolog.prologbackend.Security.Logout;

import com.prolog.prologbackend.Security.Jwt.JwtProvider;
import com.prolog.prologbackend.Security.Jwt.JwtRedisRepository;
import com.prolog.prologbackend.Security.UserDetails.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;
    private final JwtRedisRepository jwtRedisRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String accessToken = authentication.getPrincipal().toString();
        jwtProvider.verifyWithAccessToken(accessToken); //1. 사용 못하는 토큰인지 확인

        Claims claims = (Claims) authentication.getCredentials();
        String email = jwtProvider.getEmail(claims);
        userDetailsService.loadUserByUsername(email); //2. 올바른 사용자 인지 확인

        long time = claims.getExpiration().getTime() - System.currentTimeMillis();
        jwtRedisRepository.saveAccess(accessToken, email, time); //3. 엑세스 토큰 사용하지 못하도록 저장

        jwtRedisRepository.deleteRefresh(email); //4. 레디스 토큰 삭제
    }
}
