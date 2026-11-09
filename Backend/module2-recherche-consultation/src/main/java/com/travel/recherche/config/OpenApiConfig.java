package com.travel.recherche.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration OpenAPI / Swagger UI avec intégration de la Sécurité JWT (Note 2)
 */
@Configuration
public class OpenApiConfig {

    public static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Module 2 — Gestion, Recherche & Sécurité JWT des Voyages")
                        .version("1.0.0")
                        .description("Microservice dédié à la gestion unique de l'entité Voyage : "
                                + "CRUD complet, recherche multi-critères, filtres avancés, tri, "
                                + "comparateur d'offres, suggestions intelligentes, Feign, RabbitMQ et Sécurité JWT.")
                        .contact(new Contact()
                                .name("Équipe Projet Réservation - Module 2")
                                .email("recherche@travel-reservation.tn")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Serveur Local Direct (Port 8082)"),
                        new Server().url("http://localhost:8080").description("Via API Gateway (Port 8080)")
                ))
                // Configuration du cadenas de sécurité JWT dans Swagger UI
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Entrez votre Token JWT généré via /api/recherche/auth/login ou /api/recherche/auth/demo-tokens")));
    }
}
