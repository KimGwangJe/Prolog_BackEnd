package com.prolog.prologbackend.Config;

import com.prolog.prologbackend.Security.Authentication.CustomAuthenticationFilter;
import com.prolog.prologbackend.Security.Authorization.CustomAccessDeniedHandler;
import com.prolog.prologbackend.Security.Authorization.CustomAuthorizationFilter;
import com.prolog.prologbackend.Security.Jwt.JwtProvider;
import com.prolog.prologbackend.Security.Logout.CustomLogoutFilter;
import com.prolog.prologbackend.Security.Logout.CustomLogoutHandler;
import com.prolog.prologbackend.Security.Logout.CustomLogoutSuccessHandler;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {
    private final JwtProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomLogoutHandler customLogoutHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.formLogin(AbstractHttpConfigurer::disable)
                .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/api/**").hasRole("USER")
                                .anyRequest().permitAll())
                .addFilter(customAuthenticationFilter())
                .addFilter(customLogoutFilter())
                .addFilterBefore(customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling.accessDeniedHandler(customAccessDeniedHandler));

        return http.build();
    }

    CorsConfigurationSource corsConfigurationSource() {
        return request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
            config.setAllowedOriginPatterns(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            return config;
        };
    }

    @Bean
    PasswordEncoder setPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter =
                new CustomAuthenticationFilter(authenticationConfiguration.getAuthenticationManager(),jwtProvider);
        customAuthenticationFilter.setFilterProcessesUrl("/members/login");
        return customAuthenticationFilter;
    }

    public CustomAuthorizationFilter customAuthorizationFilter() {
        CustomAuthorizationFilter customAuthorizationFilter =
                new CustomAuthorizationFilter(jwtProvider,customUserDetailsService);
        return customAuthorizationFilter;
    }

    public CustomLogoutFilter customLogoutFilter() {
        CustomLogoutFilter customLogoutFilter =
                new CustomLogoutFilter(customLogoutSuccessHandler, customLogoutHandler, jwtProvider);
        customLogoutFilter.setFilterProcessesUrl("/members/logout");
        return customLogoutFilter;
    }

}
