package com.fundwise.discovery;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI discoveryOpenApi() {
        return new OpenAPI().info(new Info().title("FundWise Discovery Server API").version("1.0.0")
                .description("Eureka service discovery and registry application."));
    }
}
