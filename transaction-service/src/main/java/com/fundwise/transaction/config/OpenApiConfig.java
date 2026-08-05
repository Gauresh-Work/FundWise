package com.fundwise.transaction.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI transactionOpenApi() {
        return new OpenAPI().info(new Info().title("FundWise Transaction Service API").version("1.0.0")
                .description("Purchases, redemptions, SIPs and fund switches."));
    }
}
