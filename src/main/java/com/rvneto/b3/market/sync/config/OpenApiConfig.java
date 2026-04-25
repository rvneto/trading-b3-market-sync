package com.rvneto.b3.market.sync.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("B3 Market Sync API")
                        .version("1.0.0")
                        .description("Synchronizes real market prices from brapi.dev and stores them in Redis. " +
                                "Prices are consumed by the B3 Matching Engine for order execution decisions.")
                        .contact(new Contact()
                                .name("Roberto de Vargas Neto")
                                .url("https://www.linkedin.com/in/roberto-de-vargas/")));
    }
}
