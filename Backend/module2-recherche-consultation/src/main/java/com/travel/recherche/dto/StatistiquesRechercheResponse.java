package com.travel.recherche.dto;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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


    // --- Constructors ---
    public StatistiquesRechercheResponse() {}

    public StatistiquesRechercheResponse(long totalVoyages, long totalOffresBillets, long totalOffresEnPromotion, long totalRecherchesEffectuees, long totalConsultations, BigDecimal prixMinimumCatalogue, BigDecimal prixMaximumCatalogue, Double noteMoyenneGlobale, List<String> villesDepartDisponibles, List<String> villesDestinationDisponibles, List<String> paysDestinationDisponibles, List<String> compagniesDisponibles, Map<String, Long> repartitionParTypeTransport, Map<String, Long> repartitionParCategorie) {
        this.totalVoyages = totalVoyages;
        this.totalOffresBillets = totalOffresBillets;
        this.totalOffresEnPromotion = totalOffresEnPromotion;
        this.totalRecherchesEffectuees = totalRecherchesEffectuees;
        this.totalConsultations = totalConsultations;
        this.prixMinimumCatalogue = prixMinimumCatalogue;
        this.prixMaximumCatalogue = prixMaximumCatalogue;
        this.noteMoyenneGlobale = noteMoyenneGlobale;
        this.villesDepartDisponibles = villesDepartDisponibles;
        this.villesDestinationDisponibles = villesDestinationDisponibles;
        this.paysDestinationDisponibles = paysDestinationDisponibles;
        this.compagniesDisponibles = compagniesDisponibles;
        this.repartitionParTypeTransport = repartitionParTypeTransport;
        this.repartitionParCategorie = repartitionParCategorie;
    }

    // --- Getters & Setters ---
    public long getTotalVoyages() {
        return this.totalVoyages;
    }
    public void setTotalVoyages(long totalVoyages) {
        this.totalVoyages = totalVoyages;
    }
    public long getTotalOffresBillets() {
        return this.totalOffresBillets;
    }
    public void setTotalOffresBillets(long totalOffresBillets) {
        this.totalOffresBillets = totalOffresBillets;
    }
    public long getTotalOffresEnPromotion() {
        return this.totalOffresEnPromotion;
    }
    public void setTotalOffresEnPromotion(long totalOffresEnPromotion) {
        this.totalOffresEnPromotion = totalOffresEnPromotion;
    }
    public long getTotalRecherchesEffectuees() {
        return this.totalRecherchesEffectuees;
    }
    public void setTotalRecherchesEffectuees(long totalRecherchesEffectuees) {
        this.totalRecherchesEffectuees = totalRecherchesEffectuees;
    }
    public long getTotalConsultations() {
        return this.totalConsultations;
    }
    public void setTotalConsultations(long totalConsultations) {
        this.totalConsultations = totalConsultations;
    }
    public BigDecimal getPrixMinimumCatalogue() {
        return this.prixMinimumCatalogue;
    }
    public void setPrixMinimumCatalogue(BigDecimal prixMinimumCatalogue) {
        this.prixMinimumCatalogue = prixMinimumCatalogue;
    }
    public BigDecimal getPrixMaximumCatalogue() {
        return this.prixMaximumCatalogue;
    }
    public void setPrixMaximumCatalogue(BigDecimal prixMaximumCatalogue) {
        this.prixMaximumCatalogue = prixMaximumCatalogue;
    }
    public Double getNoteMoyenneGlobale() {
        return this.noteMoyenneGlobale;
    }
    public void setNoteMoyenneGlobale(Double noteMoyenneGlobale) {
        this.noteMoyenneGlobale = noteMoyenneGlobale;
    }
    public List<String> getVillesDepartDisponibles() {
        return this.villesDepartDisponibles;
    }
    public void setVillesDepartDisponibles(List<String> villesDepartDisponibles) {
        this.villesDepartDisponibles = villesDepartDisponibles;
    }
    public List<String> getVillesDestinationDisponibles() {
        return this.villesDestinationDisponibles;
    }
    public void setVillesDestinationDisponibles(List<String> villesDestinationDisponibles) {
        this.villesDestinationDisponibles = villesDestinationDisponibles;
    }
    public List<String> getPaysDestinationDisponibles() {
        return this.paysDestinationDisponibles;
    }
    public void setPaysDestinationDisponibles(List<String> paysDestinationDisponibles) {
        this.paysDestinationDisponibles = paysDestinationDisponibles;
    }
    public List<String> getCompagniesDisponibles() {
        return this.compagniesDisponibles;
    }
    public void setCompagniesDisponibles(List<String> compagniesDisponibles) {
        this.compagniesDisponibles = compagniesDisponibles;
    }
    public Map<String, Long> getRepartitionParTypeTransport() {
        return this.repartitionParTypeTransport;
    }
    public void setRepartitionParTypeTransport(Map<String, Long> repartitionParTypeTransport) {
        this.repartitionParTypeTransport = repartitionParTypeTransport;
    }
    public Map<String, Long> getRepartitionParCategorie() {
        return this.repartitionParCategorie;
    }
    public void setRepartitionParCategorie(Map<String, Long> repartitionParCategorie) {
        this.repartitionParCategorie = repartitionParCategorie;
    }

    // --- Builder Pattern ---
    public static StatistiquesRechercheResponseBuilder builder() {
        return new StatistiquesRechercheResponseBuilder();
    }

    public static class StatistiquesRechercheResponseBuilder {
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

        public StatistiquesRechercheResponseBuilder() {}

        public StatistiquesRechercheResponseBuilder totalVoyages(long totalVoyages) {
            this.totalVoyages = totalVoyages;
            return this;
        }
        public StatistiquesRechercheResponseBuilder totalOffresBillets(long totalOffresBillets) {
            this.totalOffresBillets = totalOffresBillets;
            return this;
        }
        public StatistiquesRechercheResponseBuilder totalOffresEnPromotion(long totalOffresEnPromotion) {
            this.totalOffresEnPromotion = totalOffresEnPromotion;
            return this;
        }
        public StatistiquesRechercheResponseBuilder totalRecherchesEffectuees(long totalRecherchesEffectuees) {
            this.totalRecherchesEffectuees = totalRecherchesEffectuees;
            return this;
        }
        public StatistiquesRechercheResponseBuilder totalConsultations(long totalConsultations) {
            this.totalConsultations = totalConsultations;
            return this;
        }
        public StatistiquesRechercheResponseBuilder prixMinimumCatalogue(BigDecimal prixMinimumCatalogue) {
            this.prixMinimumCatalogue = prixMinimumCatalogue;
            return this;
        }
        public StatistiquesRechercheResponseBuilder prixMaximumCatalogue(BigDecimal prixMaximumCatalogue) {
            this.prixMaximumCatalogue = prixMaximumCatalogue;
            return this;
        }
        public StatistiquesRechercheResponseBuilder noteMoyenneGlobale(Double noteMoyenneGlobale) {
            this.noteMoyenneGlobale = noteMoyenneGlobale;
            return this;
        }
        public StatistiquesRechercheResponseBuilder villesDepartDisponibles(List<String> villesDepartDisponibles) {
            this.villesDepartDisponibles = villesDepartDisponibles;
            return this;
        }
        public StatistiquesRechercheResponseBuilder villesDestinationDisponibles(List<String> villesDestinationDisponibles) {
            this.villesDestinationDisponibles = villesDestinationDisponibles;
            return this;
        }
        public StatistiquesRechercheResponseBuilder paysDestinationDisponibles(List<String> paysDestinationDisponibles) {
            this.paysDestinationDisponibles = paysDestinationDisponibles;
            return this;
        }
        public StatistiquesRechercheResponseBuilder compagniesDisponibles(List<String> compagniesDisponibles) {
            this.compagniesDisponibles = compagniesDisponibles;
            return this;
        }
        public StatistiquesRechercheResponseBuilder repartitionParTypeTransport(Map<String, Long> repartitionParTypeTransport) {
            this.repartitionParTypeTransport = repartitionParTypeTransport;
            return this;
        }
        public StatistiquesRechercheResponseBuilder repartitionParCategorie(Map<String, Long> repartitionParCategorie) {
            this.repartitionParCategorie = repartitionParCategorie;
            return this;
        }

        public StatistiquesRechercheResponse build() {
            StatistiquesRechercheResponse instance = new StatistiquesRechercheResponse();
            instance.totalVoyages = this.totalVoyages;
            instance.totalOffresBillets = this.totalOffresBillets;
            instance.totalOffresEnPromotion = this.totalOffresEnPromotion;
            instance.totalRecherchesEffectuees = this.totalRecherchesEffectuees;
            instance.totalConsultations = this.totalConsultations;
            instance.prixMinimumCatalogue = this.prixMinimumCatalogue;
            instance.prixMaximumCatalogue = this.prixMaximumCatalogue;
            instance.noteMoyenneGlobale = this.noteMoyenneGlobale;
            instance.villesDepartDisponibles = this.villesDepartDisponibles;
            instance.villesDestinationDisponibles = this.villesDestinationDisponibles;
            instance.paysDestinationDisponibles = this.paysDestinationDisponibles;
            instance.compagniesDisponibles = this.compagniesDisponibles;
            instance.repartitionParTypeTransport = this.repartitionParTypeTransport;
            instance.repartitionParCategorie = this.repartitionParCategorie;
            return instance;
        }
    }

}
