package com.fundwise.scheme.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI schemeOpenApi() {
        return new OpenAPI().servers(List.of(new Server().url("/").description("FundWise API Gateway")))
                .info(new Info().title("FundWise Scheme Service API").version("1.0.0")
                .description("Mutual fund schemes and NAV history management."));
    }
}
