package com.prolog.prologbackend.Security.Authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Exception.ErrorResponse;
import com.prolog.prologbackend.Exception.ExceptionType;
import com.prolog.prologbackend.Security.ExceptionType.SecurityExceptionType;
import com.prolog.prologbackend.Security.Jwt.JwtProvider;
import com.prolog.prologbackend.Security.Jwt.JwtType;
import com.prolog.prologbackend.Security.UserDetails.CustomUserDetails;
import com.prolog.prologbackend.Security.UserDetails.CustomUserDetailsService;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RequiredArgsConstructor
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!request.getServletPath().startsWith("/api")){
            filterChain.doFilter(request, response);
        } else {
            try {
                String token = jwtProvider.substringToken(request.getHeader("Token"));
                if(token == null)
                    throw new BusinessLogicException(SecurityExceptionType.BAD_REQUEST);

                Claims claims = jwtProvider.parseToken(token);
                String email = jwtProvider.getEmail(claims);

                if (request.getServletPath().equals("/api/member/token")) {
                    jwtProvider.verifyType(JwtType.REFRESH_TOKEN, claims);
                    jwtProvider.verifyWithRedisToken(token, email);

                    String accessToken = jwtProvider.createToken(JwtType.ACCESS_TOKEN, email);
                    String refreshToken = jwtProvider.createToken(JwtType.REFRESH_TOKEN, email);

                    response.addHeader(JwtType.ACCESS_TOKEN.getTokenType(), "Bearer " + accessToken);
                    response.addHeader(JwtType.REFRESH_TOKEN.getTokenType(), "Bearer " + refreshToken);
                    response.setStatus(HttpStatus.CREATED.value());
                } else {
                    jwtProvider.verifyType(JwtType.ACCESS_TOKEN, claims);
                    jwtProvider.verifyWithAccessToken(token);

                    CustomUserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    Authentication authResult = new UsernamePasswordAuthenticationToken(userDetails.getMember(), null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authResult);

                    filterChain.doFilter(request, response);
                }
            }catch (BusinessLogicException exception){
                setErrorResponse(response, exception.getExceptionType());
            }catch (UsernameNotFoundException exception){
                setErrorResponse(response, SecurityExceptionType.NOT_FOUND);
            }catch (ExpiredJwtException exception){
                setErrorResponse(response, SecurityExceptionType.JWT_EXPIRED);
            }
        }
    }

    private void setErrorResponse(HttpServletResponse response, ExceptionType exceptionType){
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setStatus(exceptionType.getErrorCode());
        ErrorResponse errorBody = ErrorResponse.of(exceptionType);
        try {
            new ObjectMapper().writeValue(response.getWriter(), errorBody);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
