package com.travel.bagage.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("API Gateway (Port 8080)"),
                        new Server().url("http://localhost:8081").description("Microservice Bagages direct (Port 8081)")
                ))
                .info(new Info()
                        .title("API de Gestion des Bagages")
                        .version("1.0")
                        .description("Microservice pour la gestion des bagages et leur association aux réservations."));
    }
}
