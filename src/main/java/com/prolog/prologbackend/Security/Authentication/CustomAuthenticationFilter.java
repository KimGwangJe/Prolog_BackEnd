package com.prolog.prologbackend.Security.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Security.Jwt.JwtProvider;
import com.prolog.prologbackend.Security.Jwt.JwtType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;


public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final JwtProvider jwtProvider;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        super.setAuthenticationManager(authenticationManager);
        this.jwtProvider = jwtProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            ObjectMapper om = new ObjectMapper();
            Member member = om.readValue(request.getInputStream(), Member.class);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getEmail(), member.getPassword());
            Authentication authResult = this.getAuthenticationManager().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authResult);
            return authResult;
        } catch(IOException e) {
            e.printStackTrace();;
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        String userName = authResult.getPrincipal().toString();
        String accessToken = jwtProvider.createToken(JwtType.ACCESS_TOKEN, userName);
        String refreshToken = jwtProvider.createToken(JwtType.REFRESH_TOKEN, userName);
        response.addHeader(JwtType.ACCESS_TOKEN.getTokenType(), "Bearer "+accessToken);
        response.addHeader(JwtType.REFRESH_TOKEN.getTokenType(), "Bearer "+refreshToken);
        response.setStatus(HttpStatus.CREATED.value());
    }
}
