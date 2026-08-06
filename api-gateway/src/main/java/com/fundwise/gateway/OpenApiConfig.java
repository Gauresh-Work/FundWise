package com.fundwise.gateway;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI gatewayOpenApi() {
        return new OpenAPI().servers(List.of(new Server().url("/").description("FundWise API Gateway")))
                .info(new Info().title("FundWise API Gateway").version("1.0.0")
                .description("Single entry point and aggregated Swagger documentation for FundWise."));
    }
}
