package com.travel.recherche.dto;

import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.TypeTransport;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CritereRechercheRequest {

    private String motCle;
    private String villeDepart;
    private String villeDestination;
    private String paysDestination;
    private String compagnie;
    private TypeTransport typeTransport;
    private CategorieOffre categorie;
    private LocalDate dateDepartMin;
    private LocalDate dateDepartMax;
    private BigDecimal prixMin;
    private BigDecimal prixMax;
    private Integer dureeMaxMinutes;
    private Boolean volDirectUniquement;
    private Boolean enPromotionUniquement;
    private Boolean bagageSouteInclus;
    private Boolean annulationGratuite;
    private Integer placesMin;
    private Double noteMin;

    // Tri : prix, duree, dateDepart, note, popularite, score
    private String sortBy = "prix";

    // Direction : asc, desc
    private String sortDir = "asc";

    // --- Constructors ---
    public CritereRechercheRequest() {}

    public CritereRechercheRequest(String motCle, String villeDepart, String villeDestination, String paysDestination, String compagnie, TypeTransport typeTransport, CategorieOffre categorie, LocalDate dateDepartMin, LocalDate dateDepartMax, BigDecimal prixMin, BigDecimal prixMax, Integer dureeMaxMinutes, Boolean volDirectUniquement, Boolean enPromotionUniquement, Boolean bagageSouteInclus, Boolean annulationGratuite, Integer placesMin, Double noteMin, String sortBy, String sortDir) {
        this.motCle = motCle;
        this.villeDepart = villeDepart;
        this.villeDestination = villeDestination;
        this.paysDestination = paysDestination;
        this.compagnie = compagnie;
        this.typeTransport = typeTransport;
        this.categorie = categorie;
        this.dateDepartMin = dateDepartMin;
        this.dateDepartMax = dateDepartMax;
        this.prixMin = prixMin;
        this.prixMax = prixMax;
        this.dureeMaxMinutes = dureeMaxMinutes;
        this.volDirectUniquement = volDirectUniquement;
        this.enPromotionUniquement = enPromotionUniquement;
        this.bagageSouteInclus = bagageSouteInclus;
        this.annulationGratuite = annulationGratuite;
        this.placesMin = placesMin;
        this.noteMin = noteMin;
        this.sortBy = sortBy;
        this.sortDir = sortDir;
    }

    // --- Getters & Setters ---
    public String getMotCle() {
        return this.motCle;
    }
    public void setMotCle(String motCle) {
        this.motCle = motCle;
    }
    public String getVilleDepart() {
        return this.villeDepart;
    }
    public void setVilleDepart(String villeDepart) {
        this.villeDepart = villeDepart;
    }
    public String getVilleDestination() {
        return this.villeDestination;
    }
    public void setVilleDestination(String villeDestination) {
        this.villeDestination = villeDestination;
    }
    public String getPaysDestination() {
        return this.paysDestination;
    }
    public void setPaysDestination(String paysDestination) {
        this.paysDestination = paysDestination;
    }
    public String getCompagnie() {
        return this.compagnie;
    }
    public void setCompagnie(String compagnie) {
        this.compagnie = compagnie;
    }
    public TypeTransport getTypeTransport() {
        return this.typeTransport;
    }
    public void setTypeTransport(TypeTransport typeTransport) {
        this.typeTransport = typeTransport;
    }
    public CategorieOffre getCategorie() {
        return this.categorie;
    }
    public void setCategorie(CategorieOffre categorie) {
        this.categorie = categorie;
    }
    public LocalDate getDateDepartMin() {
        return this.dateDepartMin;
    }
    public void setDateDepartMin(LocalDate dateDepartMin) {
        this.dateDepartMin = dateDepartMin;
    }
    public LocalDate getDateDepartMax() {
        return this.dateDepartMax;
    }
    public void setDateDepartMax(LocalDate dateDepartMax) {
        this.dateDepartMax = dateDepartMax;
    }
    public BigDecimal getPrixMin() {
        return this.prixMin;
    }
    public void setPrixMin(BigDecimal prixMin) {
        this.prixMin = prixMin;
    }
    public BigDecimal getPrixMax() {
        return this.prixMax;
    }
    public void setPrixMax(BigDecimal prixMax) {
        this.prixMax = prixMax;
    }
    public Integer getDureeMaxMinutes() {
        return this.dureeMaxMinutes;
    }
    public void setDureeMaxMinutes(Integer dureeMaxMinutes) {
        this.dureeMaxMinutes = dureeMaxMinutes;
    }
    public Boolean getVolDirectUniquement() {
        return this.volDirectUniquement;
    }
    public void setVolDirectUniquement(Boolean volDirectUniquement) {
        this.volDirectUniquement = volDirectUniquement;
    }
    public Boolean getEnPromotionUniquement() {
        return this.enPromotionUniquement;
    }
    public void setEnPromotionUniquement(Boolean enPromotionUniquement) {
        this.enPromotionUniquement = enPromotionUniquement;
    }
    public Boolean getBagageSouteInclus() {
        return this.bagageSouteInclus;
    }
    public void setBagageSouteInclus(Boolean bagageSouteInclus) {
        this.bagageSouteInclus = bagageSouteInclus;
    }
    public Boolean getAnnulationGratuite() {
        return this.annulationGratuite;
    }
    public void setAnnulationGratuite(Boolean annulationGratuite) {
        this.annulationGratuite = annulationGratuite;
    }
    public Integer getPlacesMin() {
        return this.placesMin;
    }
    public void setPlacesMin(Integer placesMin) {
        this.placesMin = placesMin;
    }
    public Double getNoteMin() {
        return this.noteMin;
    }
    public void setNoteMin(Double noteMin) {
        this.noteMin = noteMin;
    }
    public String getSortBy() {
        return this.sortBy;
    }
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    public String getSortDir() {
        return this.sortDir;
    }
    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    // --- Builder Pattern ---
    public static CritereRechercheRequestBuilder builder() {
        return new CritereRechercheRequestBuilder();
    }

    public static class CritereRechercheRequestBuilder {
        private String motCle;
        private String villeDepart;
        private String villeDestination;
        private String paysDestination;
        private String compagnie;
        private TypeTransport typeTransport;
        private CategorieOffre categorie;
        private LocalDate dateDepartMin;
        private LocalDate dateDepartMax;
        private BigDecimal prixMin;
        private BigDecimal prixMax;
        private Integer dureeMaxMinutes;
        private Boolean volDirectUniquement;
        private Boolean enPromotionUniquement;
        private Boolean bagageSouteInclus;
        private Boolean annulationGratuite;
        private Integer placesMin;
        private Double noteMin;
        private String sortBy = "prix";
        private String sortDir = "asc";

        public CritereRechercheRequestBuilder() {}

        public CritereRechercheRequestBuilder motCle(String motCle) {
            this.motCle = motCle;
            return this;
        }
        public CritereRechercheRequestBuilder villeDepart(String villeDepart) {
            this.villeDepart = villeDepart;
            return this;
        }
        public CritereRechercheRequestBuilder villeDestination(String villeDestination) {
            this.villeDestination = villeDestination;
            return this;
        }
        public CritereRechercheRequestBuilder paysDestination(String paysDestination) {
            this.paysDestination = paysDestination;
            return this;
        }
        public CritereRechercheRequestBuilder compagnie(String compagnie) {
            this.compagnie = compagnie;
            return this;
        }
        public CritereRechercheRequestBuilder typeTransport(TypeTransport typeTransport) {
            this.typeTransport = typeTransport;
            return this;
        }
        public CritereRechercheRequestBuilder categorie(CategorieOffre categorie) {
            this.categorie = categorie;
            return this;
        }
        public CritereRechercheRequestBuilder dateDepartMin(LocalDate dateDepartMin) {
            this.dateDepartMin = dateDepartMin;
            return this;
        }
        public CritereRechercheRequestBuilder dateDepartMax(LocalDate dateDepartMax) {
            this.dateDepartMax = dateDepartMax;
            return this;
        }
        public CritereRechercheRequestBuilder prixMin(BigDecimal prixMin) {
            this.prixMin = prixMin;
            return this;
        }
        public CritereRechercheRequestBuilder prixMax(BigDecimal prixMax) {
            this.prixMax = prixMax;
            return this;
        }
        public CritereRechercheRequestBuilder dureeMaxMinutes(Integer dureeMaxMinutes) {
            this.dureeMaxMinutes = dureeMaxMinutes;
            return this;
        }
        public CritereRechercheRequestBuilder volDirectUniquement(Boolean volDirectUniquement) {
            this.volDirectUniquement = volDirectUniquement;
            return this;
        }
        public CritereRechercheRequestBuilder enPromotionUniquement(Boolean enPromotionUniquement) {
            this.enPromotionUniquement = enPromotionUniquement;
            return this;
        }
        public CritereRechercheRequestBuilder bagageSouteInclus(Boolean bagageSouteInclus) {
            this.bagageSouteInclus = bagageSouteInclus;
            return this;
        }
        public CritereRechercheRequestBuilder annulationGratuite(Boolean annulationGratuite) {
            this.annulationGratuite = annulationGratuite;
            return this;
        }
        public CritereRechercheRequestBuilder placesMin(Integer placesMin) {
            this.placesMin = placesMin;
            return this;
        }
        public CritereRechercheRequestBuilder noteMin(Double noteMin) {
            this.noteMin = noteMin;
            return this;
        }
        public CritereRechercheRequestBuilder sortBy(String sortBy) {
            this.sortBy = sortBy;
            return this;
        }
        public CritereRechercheRequestBuilder sortDir(String sortDir) {
            this.sortDir = sortDir;
            return this;
        }

        public CritereRechercheRequest build() {
            CritereRechercheRequest instance = new CritereRechercheRequest();
            instance.motCle = this.motCle;
            instance.villeDepart = this.villeDepart;
            instance.villeDestination = this.villeDestination;
            instance.paysDestination = this.paysDestination;
            instance.compagnie = this.compagnie;
            instance.typeTransport = this.typeTransport;
            instance.categorie = this.categorie;
            instance.dateDepartMin = this.dateDepartMin;
            instance.dateDepartMax = this.dateDepartMax;
            instance.prixMin = this.prixMin;
            instance.prixMax = this.prixMax;
            instance.dureeMaxMinutes = this.dureeMaxMinutes;
            instance.volDirectUniquement = this.volDirectUniquement;
            instance.enPromotionUniquement = this.enPromotionUniquement;
            instance.bagageSouteInclus = this.bagageSouteInclus;
            instance.annulationGratuite = this.annulationGratuite;
            instance.placesMin = this.placesMin;
            instance.noteMin = this.noteMin;
            instance.sortBy = this.sortBy;
            instance.sortDir = this.sortDir;
            return instance;
        }
    }
}
