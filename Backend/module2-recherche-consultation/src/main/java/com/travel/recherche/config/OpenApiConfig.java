package com.travel.recherche.config;

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
                        .title("API Module 2 — Recherche & Consultation de Voyages et Billets")
                        .version("1.0.0")
                        .description("Microservice permettant de rechercher des voyages et des billets, "
                                + "filtrer et trier les résultats, consulter les offres détaillées, "
                                + "comparer plusieurs offres côte-à-côte et proposer des suggestions intelligentes.")
                        .contact(new Contact()
                                .name("Équipe Projet Réservation - Module 2")
                                .email("recherche@travel-reservation.tn")));
    }
}
