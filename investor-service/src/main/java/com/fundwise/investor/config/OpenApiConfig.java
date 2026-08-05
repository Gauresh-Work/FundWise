package com.fundwise.investor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI investorOpenApi() {
        return new OpenAPI().info(new Info().title("FundWise Investor Service API").version("1.0.0")
                .description("Investor profiles, KYC documents, bank mandates and nominees."));
    }
}
