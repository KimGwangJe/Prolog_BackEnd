package com.prolog.prologbackend.Security.Logout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Exception.ErrorResponse;
import com.prolog.prologbackend.Exception.ExceptionType;
import com.prolog.prologbackend.Security.ExceptionType.SecurityExceptionType;
import com.prolog.prologbackend.Security.Jwt.JwtProvider;
import com.prolog.prologbackend.Security.Jwt.JwtType;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class CustomLogoutFilter extends LogoutFilter {
    private LogoutHandler logoutHandler;
    private LogoutSuccessHandler logoutSuccessHandler;
    private JwtProvider jwtProvider;

    public CustomLogoutFilter(LogoutSuccessHandler logoutSuccessHandler, LogoutHandler logoutHandler, JwtProvider jwtProvider) {
        super(logoutSuccessHandler, logoutHandler);
        this.logoutSuccessHandler = logoutSuccessHandler;
        this.logoutHandler = logoutHandler;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        if(requiresLogout(httpServletRequest, httpServletResponse)) {
            try {
                String accessToken = httpServletRequest.getHeader("Token");
                String subToken = jwtProvider.substringToken(accessToken);
                Claims claims = jwtProvider.parseToken(subToken);
                jwtProvider.verifyType(JwtType.ACCESS_TOKEN, claims);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(subToken, claims, null);

                logoutHandler.logout(httpServletRequest, httpServletResponse, authentication);
                logoutSuccessHandler.onLogoutSuccess(httpServletRequest, httpServletResponse, authentication);
            } catch (BusinessLogicException exception) {
                setErrorResponse(httpServletResponse, exception.getExceptionType());
            } catch (UsernameNotFoundException exception) {
                setErrorResponse(httpServletResponse, SecurityExceptionType.NOT_FOUND);
            }
        } else {
            chain.doFilter(request, response);
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
