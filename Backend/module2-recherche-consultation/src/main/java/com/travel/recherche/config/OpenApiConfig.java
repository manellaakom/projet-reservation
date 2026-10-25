package com.travel.recherche.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
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
                .info(new Info()
                        .title("API Module 2 — Gestion & Recherche des Voyages")
                        .version("1.0.0")
                        .description("Microservice dédié à la gestion unique de l'entité Voyage : "
                                + "CRUD complet, recherche multi-critères, filtres avancés, tri, "
                                + "comparateur d'offres et moteur de suggestions intelligentes.")
                        .contact(new Contact()
                                .name("Équipe Projet Réservation - Module 2")
                                .email("recherche@travel-reservation.tn")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Serveur Local Direct (Port 8082)"),
                        new Server().url("http://localhost:8080").description("Via API Gateway (Port 8080)")
                ));
    }
}
