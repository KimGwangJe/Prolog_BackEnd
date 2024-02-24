package com.prolog.prologbackend.Config;

import com.prolog.prologbackend.Security.Authentication.CustomAuthenticationFilter;
import com.prolog.prologbackend.Security.Authorization.CustomAuthorizationFilter;
import com.prolog.prologbackend.Security.Jwt.JwtProvider;
import com.prolog.prologbackend.Security.UserDetails.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize.requestMatchers("/**").permitAll())
                .addFilter(customAuthenticationFilter())
                .addFilterBefore(customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    PasswordEncoder setPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter =
                new CustomAuthenticationFilter(authenticationConfiguration.getAuthenticationManager(),jwtProvider);
        customAuthenticationFilter.setFilterProcessesUrl("/members/login");
        return customAuthenticationFilter;
    }

    @Bean
    public CustomAuthorizationFilter customAuthorizationFilter() {
        CustomAuthorizationFilter customAuthorizationFilter =
                new CustomAuthorizationFilter(jwtProvider,customUserDetailsService);
        return customAuthorizationFilter;
    }
}
