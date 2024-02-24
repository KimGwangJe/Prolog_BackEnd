package com.prolog.prologbackend.Security.Authentication;

import com.prolog.prologbackend.Security.ExceptionType.SecurityExceptionType;
import com.prolog.prologbackend.Security.UserDetails.CustomUserDetails;
import com.prolog.prologbackend.Security.UserDetails.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String userName = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(userName);
        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException(SecurityExceptionType.BAD_CREDENTIALS.getErrorMessage());
        }

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
