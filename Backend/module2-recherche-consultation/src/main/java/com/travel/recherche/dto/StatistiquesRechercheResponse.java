package com.travel.recherche.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatistiquesRechercheResponse {

    private long totalVoyages;
    private long totalOffresBillets;
    private long totalOffresEnPromotion;
    private long totalRecherchesEffectuees;
    private long totalConsultations;

    private BigDecimal prixMinimumCatalogue;
    private BigDecimal prixMaximumCatalogue;
    private Double noteMoyenneGlobale;

    private List<String> villesDepartDisponibles;
    private List<String> villesDestinationDisponibles;
    private List<String> paysDestinationDisponibles;
    private List<String> compagniesDisponibles;

    private Map<String, Long> repartitionParTypeTransport;
    private Map<String, Long> repartitionParCategorie;
}
