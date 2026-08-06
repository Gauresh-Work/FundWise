package com.fundwise.investor.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI investorOpenApi() {
        return new OpenAPI().servers(List.of(new Server().url("/").description("FundWise API Gateway")))
                .info(new Info().title("FundWise Investor Service API").version("1.0.0")
                .description("Investor profiles, KYC documents, bank mandates and nominees."));
    }
}
