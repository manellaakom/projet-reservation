package com.travel.recherche.dto;

import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.StatutOffre;
import com.travel.recherche.enums.TypeTransport;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VoyageRequest {

    @NotBlank(message = "Le code du voyage est obligatoire")
    private String codeVoyage;

    @NotBlank(message = "Le titre de l'offre est obligatoire")
    private String titre;

    private String description;

    @NotBlank(message = "La ville de départ est obligatoire")
    private String villeDepart;

    @NotBlank(message = "La ville de destination est obligatoire")
    private String villeDestination;

    @NotBlank(message = "Le pays de destination est obligatoire")
    private String paysDestination;

    @NotBlank(message = "La compagnie est obligatoire")
    private String compagnie;

    @NotNull(message = "Le type de transport est obligatoire")
    private TypeTransport typeTransport;

    @NotNull(message = "La catégorie de l'offre est obligatoire")
    private CategorieOffre categorie;

    @NotNull(message = "La date de départ est obligatoire")
    private LocalDateTime dateDepart;

    @NotNull(message = "La date d'arrivée est obligatoire")
    private LocalDateTime dateArrivee;

    @NotNull(message = "La durée en minutes est obligatoire")
    @Min(value = 15, message = "La durée minimale est de 15 minutes")
    private Integer dureeMinutes;
    private Integer escales = 0;

    @NotNull(message = "Le prix de base est obligatoire")
    @Min(value = 1, message = "Le prix de base doit être positif")
    private BigDecimal prixBase;

    private BigDecimal prixPromo;
    private Boolean enPromotion = false;

    @NotNull(message = "Le nombre de places disponibles est obligatoire")
    @Min(value = 0, message = "Le nombre de places ne peut pas être négatif")
    private Integer placesDisponibles;
    private Double noteMoyenne = 4.5;
    private Integer nombreAvis = 10;
    private Boolean bagageCabineInclus = true;
    private Boolean bagageSouteInclus = false;
    private Boolean annulationGratuite = false;
    private Boolean repasInclus = false;
    private Boolean wifiDisponible = false;

    private String imageUrl;
    private StatutOffre statut = StatutOffre.DISPONIBLE;

    // --- Constructors ---
    public VoyageRequest() {}

    public VoyageRequest(String codeVoyage, String titre, String description, String villeDepart, String villeDestination, String paysDestination, String compagnie, TypeTransport typeTransport, CategorieOffre categorie, LocalDateTime dateDepart, LocalDateTime dateArrivee, Integer dureeMinutes, Integer escales, BigDecimal prixBase, BigDecimal prixPromo, Boolean enPromotion, Integer placesDisponibles, Double noteMoyenne, Integer nombreAvis, Boolean bagageCabineInclus, Boolean bagageSouteInclus, Boolean annulationGratuite, Boolean repasInclus, Boolean wifiDisponible, String imageUrl, StatutOffre statut) {
        this.codeVoyage = codeVoyage;
        this.titre = titre;
        this.description = description;
        this.villeDepart = villeDepart;
        this.villeDestination = villeDestination;
        this.paysDestination = paysDestination;
        this.compagnie = compagnie;
        this.typeTransport = typeTransport;
        this.categorie = categorie;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.dureeMinutes = dureeMinutes;
        this.escales = escales;
        this.prixBase = prixBase;
        this.prixPromo = prixPromo;
        this.enPromotion = enPromotion;
        this.placesDisponibles = placesDisponibles;
        this.noteMoyenne = noteMoyenne;
        this.nombreAvis = nombreAvis;
        this.bagageCabineInclus = bagageCabineInclus;
        this.bagageSouteInclus = bagageSouteInclus;
        this.annulationGratuite = annulationGratuite;
        this.repasInclus = repasInclus;
        this.wifiDisponible = wifiDisponible;
        this.imageUrl = imageUrl;
        this.statut = statut;
    }

    // --- Getters & Setters ---
    public String getCodeVoyage() {
        return this.codeVoyage;
    }
    public void setCodeVoyage(String codeVoyage) {
        this.codeVoyage = codeVoyage;
    }
    public String getTitre() {
        return this.titre;
    }
    public void setTitre(String titre) {
        this.titre = titre;
    }
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
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
    public LocalDateTime getDateDepart() {
        return this.dateDepart;
    }
    public void setDateDepart(LocalDateTime dateDepart) {
        this.dateDepart = dateDepart;
    }
    public LocalDateTime getDateArrivee() {
        return this.dateArrivee;
    }
    public void setDateArrivee(LocalDateTime dateArrivee) {
        this.dateArrivee = dateArrivee;
    }
    public Integer getDureeMinutes() {
        return this.dureeMinutes;
    }
    public void setDureeMinutes(Integer dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }
    public Integer getEscales() {
        return this.escales;
    }
    public void setEscales(Integer escales) {
        this.escales = escales;
    }
    public BigDecimal getPrixBase() {
        return this.prixBase;
    }
    public void setPrixBase(BigDecimal prixBase) {
        this.prixBase = prixBase;
    }
    public BigDecimal getPrixPromo() {
        return this.prixPromo;
    }
    public void setPrixPromo(BigDecimal prixPromo) {
        this.prixPromo = prixPromo;
    }
    public Boolean getEnPromotion() {
        return this.enPromotion;
    }
    public Boolean isEnPromotion() {
        return this.enPromotion;
    }
    public void setEnPromotion(Boolean enPromotion) {
        this.enPromotion = enPromotion;
    }
    public Integer getPlacesDisponibles() {
        return this.placesDisponibles;
    }
    public void setPlacesDisponibles(Integer placesDisponibles) {
        this.placesDisponibles = placesDisponibles;
    }
    public Double getNoteMoyenne() {
        return this.noteMoyenne;
    }
    public void setNoteMoyenne(Double noteMoyenne) {
        this.noteMoyenne = noteMoyenne;
    }
    public Integer getNombreAvis() {
        return this.nombreAvis;
    }
    public void setNombreAvis(Integer nombreAvis) {
        this.nombreAvis = nombreAvis;
    }
    public Boolean getBagageCabineInclus() {
        return this.bagageCabineInclus;
    }
    public Boolean isBagageCabineInclus() {
        return this.bagageCabineInclus;
    }
    public void setBagageCabineInclus(Boolean bagageCabineInclus) {
        this.bagageCabineInclus = bagageCabineInclus;
    }
    public Boolean getBagageSouteInclus() {
        return this.bagageSouteInclus;
    }
    public Boolean isBagageSouteInclus() {
        return this.bagageSouteInclus;
    }
    public void setBagageSouteInclus(Boolean bagageSouteInclus) {
        this.bagageSouteInclus = bagageSouteInclus;
    }
    public Boolean getAnnulationGratuite() {
        return this.annulationGratuite;
    }
    public Boolean isAnnulationGratuite() {
        return this.annulationGratuite;
    }
    public void setAnnulationGratuite(Boolean annulationGratuite) {
        this.annulationGratuite = annulationGratuite;
    }
    public Boolean getRepasInclus() {
        return this.repasInclus;
    }
    public Boolean isRepasInclus() {
        return this.repasInclus;
    }
    public void setRepasInclus(Boolean repasInclus) {
        this.repasInclus = repasInclus;
    }
    public Boolean getWifiDisponible() {
        return this.wifiDisponible;
    }
    public Boolean isWifiDisponible() {
        return this.wifiDisponible;
    }
    public void setWifiDisponible(Boolean wifiDisponible) {
        this.wifiDisponible = wifiDisponible;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public StatutOffre getStatut() {
        return this.statut;
    }
    public void setStatut(StatutOffre statut) {
        this.statut = statut;
    }

    // --- Builder Pattern ---
    public static VoyageRequestBuilder builder() {
        return new VoyageRequestBuilder();
    }

    public static class VoyageRequestBuilder {
        private String codeVoyage;
        private String titre;
        private String description;
        private String villeDepart;
        private String villeDestination;
        private String paysDestination;
        private String compagnie;
        private TypeTransport typeTransport;
        private CategorieOffre categorie;
        private LocalDateTime dateDepart;
        private LocalDateTime dateArrivee;
        private Integer dureeMinutes;
        private Integer escales = 0;
        private BigDecimal prixBase;
        private BigDecimal prixPromo;
        private Boolean enPromotion = false;
        private Integer placesDisponibles;
        private Double noteMoyenne = 4.5;
        private Integer nombreAvis = 10;
        private Boolean bagageCabineInclus = true;
        private Boolean bagageSouteInclus = false;
        private Boolean annulationGratuite = false;
        private Boolean repasInclus = false;
        private Boolean wifiDisponible = false;
        private String imageUrl;
        private StatutOffre statut = StatutOffre.DISPONIBLE;

        public VoyageRequestBuilder() {}

        public VoyageRequestBuilder codeVoyage(String codeVoyage) {
            this.codeVoyage = codeVoyage;
            return this;
        }
        public VoyageRequestBuilder titre(String titre) {
            this.titre = titre;
            return this;
        }
        public VoyageRequestBuilder description(String description) {
            this.description = description;
            return this;
        }
        public VoyageRequestBuilder villeDepart(String villeDepart) {
            this.villeDepart = villeDepart;
            return this;
        }
        public VoyageRequestBuilder villeDestination(String villeDestination) {
            this.villeDestination = villeDestination;
            return this;
        }
        public VoyageRequestBuilder paysDestination(String paysDestination) {
            this.paysDestination = paysDestination;
            return this;
        }
        public VoyageRequestBuilder compagnie(String compagnie) {
            this.compagnie = compagnie;
            return this;
        }
        public VoyageRequestBuilder typeTransport(TypeTransport typeTransport) {
            this.typeTransport = typeTransport;
            return this;
        }
        public VoyageRequestBuilder categorie(CategorieOffre categorie) {
            this.categorie = categorie;
            return this;
        }
        public VoyageRequestBuilder dateDepart(LocalDateTime dateDepart) {
            this.dateDepart = dateDepart;
            return this;
        }
        public VoyageRequestBuilder dateArrivee(LocalDateTime dateArrivee) {
            this.dateArrivee = dateArrivee;
            return this;
        }
        public VoyageRequestBuilder dureeMinutes(Integer dureeMinutes) {
            this.dureeMinutes = dureeMinutes;
            return this;
        }
        public VoyageRequestBuilder escales(Integer escales) {
            this.escales = escales;
            return this;
        }
        public VoyageRequestBuilder prixBase(BigDecimal prixBase) {
            this.prixBase = prixBase;
            return this;
        }
        public VoyageRequestBuilder prixPromo(BigDecimal prixPromo) {
            this.prixPromo = prixPromo;
            return this;
        }
        public VoyageRequestBuilder enPromotion(Boolean enPromotion) {
            this.enPromotion = enPromotion;
            return this;
        }
        public VoyageRequestBuilder placesDisponibles(Integer placesDisponibles) {
            this.placesDisponibles = placesDisponibles;
            return this;
        }
        public VoyageRequestBuilder noteMoyenne(Double noteMoyenne) {
            this.noteMoyenne = noteMoyenne;
            return this;
        }
        public VoyageRequestBuilder nombreAvis(Integer nombreAvis) {
            this.nombreAvis = nombreAvis;
            return this;
        }
        public VoyageRequestBuilder bagageCabineInclus(Boolean bagageCabineInclus) {
            this.bagageCabineInclus = bagageCabineInclus;
            return this;
        }
        public VoyageRequestBuilder bagageSouteInclus(Boolean bagageSouteInclus) {
            this.bagageSouteInclus = bagageSouteInclus;
            return this;
        }
        public VoyageRequestBuilder annulationGratuite(Boolean annulationGratuite) {
            this.annulationGratuite = annulationGratuite;
            return this;
        }
        public VoyageRequestBuilder repasInclus(Boolean repasInclus) {
            this.repasInclus = repasInclus;
            return this;
        }
        public VoyageRequestBuilder wifiDisponible(Boolean wifiDisponible) {
            this.wifiDisponible = wifiDisponible;
            return this;
        }
        public VoyageRequestBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }
        public VoyageRequestBuilder statut(StatutOffre statut) {
            this.statut = statut;
            return this;
        }

        public VoyageRequest build() {
            VoyageRequest instance = new VoyageRequest();
            instance.codeVoyage = this.codeVoyage;
            instance.titre = this.titre;
            instance.description = this.description;
            instance.villeDepart = this.villeDepart;
            instance.villeDestination = this.villeDestination;
            instance.paysDestination = this.paysDestination;
            instance.compagnie = this.compagnie;
            instance.typeTransport = this.typeTransport;
            instance.categorie = this.categorie;
            instance.dateDepart = this.dateDepart;
            instance.dateArrivee = this.dateArrivee;
            instance.dureeMinutes = this.dureeMinutes;
            instance.escales = this.escales;
            instance.prixBase = this.prixBase;
            instance.prixPromo = this.prixPromo;
            instance.enPromotion = this.enPromotion;
            instance.placesDisponibles = this.placesDisponibles;
            instance.noteMoyenne = this.noteMoyenne;
            instance.nombreAvis = this.nombreAvis;
            instance.bagageCabineInclus = this.bagageCabineInclus;
            instance.bagageSouteInclus = this.bagageSouteInclus;
            instance.annulationGratuite = this.annulationGratuite;
            instance.repasInclus = this.repasInclus;
            instance.wifiDisponible = this.wifiDisponible;
            instance.imageUrl = this.imageUrl;
            instance.statut = this.statut;
            return instance;
        }
    }
}
