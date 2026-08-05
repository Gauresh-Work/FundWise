package com.fundwise.folio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI folioOpenApi() {
        return new OpenAPI().info(new Info().title("FundWise Folio Service API").version("1.0.0")
                .description("Investor folios, holdings, units and portfolio values."));
    }
}
