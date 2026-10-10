package com.travel.recherche.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionsResponse {

    private String contexteSuggestion;

    @Builder.Default
    private List<VoyageResponse> offresTendances = new ArrayList<>();

    @Builder.Default
    private List<VoyageResponse> meilleuresPromotions = new ArrayList<>();

    @Builder.Default
    private List<VoyageResponse> petitsBudgets = new ArrayList<>();

    @Builder.Default
    private List<VoyageResponse> offresSimilaires = new ArrayList<>();

    @Builder.Default
    private List<VoyageResponse> alternativesMoinsCheres = new ArrayList<>();

    @Builder.Default
    private List<VoyageResponse> meilleurRapportQualitePrix = new ArrayList<>();
}
