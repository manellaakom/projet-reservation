package com.travel.recherche.dto;


import java.util.ArrayList;
import java.util.List;

public class SuggestionsResponse {

    private String contexteSuggestion;
    private List<VoyageResponse> offresTendances = new ArrayList<>();
    private List<VoyageResponse> meilleuresPromotions = new ArrayList<>();
    private List<VoyageResponse> petitsBudgets = new ArrayList<>();
    private List<VoyageResponse> offresSimilaires = new ArrayList<>();
    private List<VoyageResponse> alternativesMoinsCheres = new ArrayList<>();
    private List<VoyageResponse> meilleurRapportQualitePrix = new ArrayList<>();


    // --- Constructors ---
    public SuggestionsResponse() {}

    public SuggestionsResponse(String contexteSuggestion, List<VoyageResponse> offresTendances, List<VoyageResponse> meilleuresPromotions, List<VoyageResponse> petitsBudgets, List<VoyageResponse> offresSimilaires, List<VoyageResponse> alternativesMoinsCheres, List<VoyageResponse> meilleurRapportQualitePrix) {
        this.contexteSuggestion = contexteSuggestion;
        this.offresTendances = offresTendances;
        this.meilleuresPromotions = meilleuresPromotions;
        this.petitsBudgets = petitsBudgets;
        this.offresSimilaires = offresSimilaires;
        this.alternativesMoinsCheres = alternativesMoinsCheres;
        this.meilleurRapportQualitePrix = meilleurRapportQualitePrix;
    }

    // --- Getters & Setters ---
    public String getContexteSuggestion() {
        return this.contexteSuggestion;
    }
    public void setContexteSuggestion(String contexteSuggestion) {
        this.contexteSuggestion = contexteSuggestion;
    }
    public List<VoyageResponse> getOffresTendances() {
        return this.offresTendances;
    }
    public void setOffresTendances(List<VoyageResponse> offresTendances) {
        this.offresTendances = offresTendances;
    }
    public List<VoyageResponse> getMeilleuresPromotions() {
        return this.meilleuresPromotions;
    }
    public void setMeilleuresPromotions(List<VoyageResponse> meilleuresPromotions) {
        this.meilleuresPromotions = meilleuresPromotions;
    }
    public List<VoyageResponse> getPetitsBudgets() {
        return this.petitsBudgets;
    }
    public void setPetitsBudgets(List<VoyageResponse> petitsBudgets) {
        this.petitsBudgets = petitsBudgets;
    }
    public List<VoyageResponse> getOffresSimilaires() {
        return this.offresSimilaires;
    }
    public void setOffresSimilaires(List<VoyageResponse> offresSimilaires) {
        this.offresSimilaires = offresSimilaires;
    }
    public List<VoyageResponse> getAlternativesMoinsCheres() {
        return this.alternativesMoinsCheres;
    }
    public void setAlternativesMoinsCheres(List<VoyageResponse> alternativesMoinsCheres) {
        this.alternativesMoinsCheres = alternativesMoinsCheres;
    }
    public List<VoyageResponse> getMeilleurRapportQualitePrix() {
        return this.meilleurRapportQualitePrix;
    }
    public void setMeilleurRapportQualitePrix(List<VoyageResponse> meilleurRapportQualitePrix) {
        this.meilleurRapportQualitePrix = meilleurRapportQualitePrix;
    }

    // --- Builder Pattern ---
    public static SuggestionsResponseBuilder builder() {
        return new SuggestionsResponseBuilder();
    }

    public static class SuggestionsResponseBuilder {
        private String contexteSuggestion;
        private List<VoyageResponse> offresTendances = new ArrayList<>();
        private List<VoyageResponse> meilleuresPromotions = new ArrayList<>();
        private List<VoyageResponse> petitsBudgets = new ArrayList<>();
        private List<VoyageResponse> offresSimilaires = new ArrayList<>();
        private List<VoyageResponse> alternativesMoinsCheres = new ArrayList<>();
        private List<VoyageResponse> meilleurRapportQualitePrix = new ArrayList<>();

        public SuggestionsResponseBuilder() {}

        public SuggestionsResponseBuilder contexteSuggestion(String contexteSuggestion) {
            this.contexteSuggestion = contexteSuggestion;
            return this;
        }
        public SuggestionsResponseBuilder offresTendances(List<VoyageResponse> offresTendances) {
            this.offresTendances = offresTendances;
            return this;
        }
        public SuggestionsResponseBuilder meilleuresPromotions(List<VoyageResponse> meilleuresPromotions) {
            this.meilleuresPromotions = meilleuresPromotions;
            return this;
        }
        public SuggestionsResponseBuilder petitsBudgets(List<VoyageResponse> petitsBudgets) {
            this.petitsBudgets = petitsBudgets;
            return this;
        }
        public SuggestionsResponseBuilder offresSimilaires(List<VoyageResponse> offresSimilaires) {
            this.offresSimilaires = offresSimilaires;
            return this;
        }
        public SuggestionsResponseBuilder alternativesMoinsCheres(List<VoyageResponse> alternativesMoinsCheres) {
            this.alternativesMoinsCheres = alternativesMoinsCheres;
            return this;
        }
        public SuggestionsResponseBuilder meilleurRapportQualitePrix(List<VoyageResponse> meilleurRapportQualitePrix) {
            this.meilleurRapportQualitePrix = meilleurRapportQualitePrix;
            return this;
        }

        public SuggestionsResponse build() {
            SuggestionsResponse instance = new SuggestionsResponse();
            instance.contexteSuggestion = this.contexteSuggestion;
            instance.offresTendances = this.offresTendances;
            instance.meilleuresPromotions = this.meilleuresPromotions;
            instance.petitsBudgets = this.petitsBudgets;
            instance.offresSimilaires = this.offresSimilaires;
            instance.alternativesMoinsCheres = this.alternativesMoinsCheres;
            instance.meilleurRapportQualitePrix = this.meilleurRapportQualitePrix;
            return instance;
        }
    }

}

