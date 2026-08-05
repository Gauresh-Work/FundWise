package com.fundwise.statement.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI statementOpenApi() {
        return new OpenAPI().info(new Info().title("FundWise Statement Service API").version("1.0.0")
                .description("Statement generation records and consolidated folio statements."));
    }
}
