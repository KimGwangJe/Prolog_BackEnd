package com.prolog.prologbackend.Security.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.prologbackend.Exception.ErrorResponse;
import com.prolog.prologbackend.Exception.ExceptionType;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Security.ExceptionType.SecurityExceptionType;
import com.prolog.prologbackend.Security.Jwt.JwtProvider;
import com.prolog.prologbackend.Security.Jwt.JwtType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


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

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if(exception instanceof UsernameNotFoundException){
            setErrorResponse(response, SecurityExceptionType.NOT_FOUND);
        }else if(exception instanceof BadCredentialsException) {
            setErrorResponse(response, SecurityExceptionType.BAD_CREDENTIALS);
        }
    }

    private void setErrorResponse(HttpServletResponse response, ExceptionType exceptionType) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");
        response.setStatus(exceptionType.getErrorCode());
        ErrorResponse errorBody = ErrorResponse.of(exceptionType);
        new ObjectMapper().writeValue(response.getWriter(), errorBody);
    }
}
