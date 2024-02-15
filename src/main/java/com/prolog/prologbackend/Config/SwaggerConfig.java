package com.prolog.prologbackend.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Author : Kim
 * date : 2024-02-10
 * description : 단순 테스트입니다~
 * 나중에 JWT를 사용하게 되면 수정 필요함 우선은 ~~
 */


@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Prolog API 명세서")
                .version("V 1.0.0");
    }
}

