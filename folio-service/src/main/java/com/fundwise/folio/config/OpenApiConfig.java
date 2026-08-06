package com.fundwise.folio.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean OpenAPI folioOpenApi() {
        return new OpenAPI().servers(List.of(new Server().url("/").description("FundWise API Gateway")))
                .info(new Info().title("FundWise Folio Service API").version("1.0.0")
                .description("Investor folios, holdings, units and portfolio values."));
    }
}
