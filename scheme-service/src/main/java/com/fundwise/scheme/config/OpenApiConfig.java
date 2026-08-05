package com.fundwise.scheme.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI schemeOpenApi() {
        return new OpenAPI().info(new Info().title("FundWise Scheme Service API").version("1.0.0")
                .description("Mutual fund schemes and NAV history management."));
    }
}
