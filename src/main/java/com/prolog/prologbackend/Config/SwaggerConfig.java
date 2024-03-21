package com.prolog.prologbackend.Config;

import com.prolog.prologbackend.Security.Jwt.JwtType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        String accessKey = JwtType.ACCESS_TOKEN.getTokenType();
        String refreshKey = JwtType.REFRESH_TOKEN.getTokenType();

        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList(accessKey)
                .addList(refreshKey);

        SecurityScheme accessTokenSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .scheme("Bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(accessKey);

        SecurityScheme refreshTokenSecurityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .scheme("Bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name(refreshKey);

        Components components = new Components()
                .addSecuritySchemes(accessKey, accessTokenSecurityScheme)
                .addSecuritySchemes(refreshKey, refreshTokenSecurityScheme);

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(components)
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Prolog API 명세서")
                .version("V 1.0.0");
    }
}

