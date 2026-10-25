package com.travel.recherche.dto;

import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.StatutOffre;
import com.travel.recherche.enums.TypeTransport;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class VoyageResponse {

    private Long id;
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
    private String dureeFormatee;
    private Integer escales;
    private BigDecimal prixBase;
    private BigDecimal prixPromo;
    private BigDecimal prixEffectif;
    private Integer pourcentageReduction;
    private Boolean enPromotion;
    private Integer placesDisponibles;
    private Double noteMoyenne;
    private Integer nombreAvis;
    private Integer nombreConsultations;
    private Boolean bagageCabineInclus;
    private Boolean bagageSouteInclus;
    private Boolean annulationGratuite;
    private Boolean repasInclus;
    private Boolean wifiDisponible;
    private String imageUrl;
    private StatutOffre statut;

    // Indicateurs calculés pour la comparaison et les suggestions
    private Integer scoreQualitePrix;
    private List<String> badgesRecommandation = new ArrayList<>();
    private List<String> avantagesCles = new ArrayList<>();
    private List<OffreBilletDto> billets = new ArrayList<>();


    // --- Constructors ---
    public VoyageResponse() {}

    public VoyageResponse(Long id, String codeVoyage, String titre, String description, String villeDepart, String villeDestination, String paysDestination, String compagnie, TypeTransport typeTransport, CategorieOffre categorie, LocalDateTime dateDepart, LocalDateTime dateArrivee, Integer dureeMinutes, String dureeFormatee, Integer escales, BigDecimal prixBase, BigDecimal prixPromo, BigDecimal prixEffectif, Integer pourcentageReduction, Boolean enPromotion, Integer placesDisponibles, Double noteMoyenne, Integer nombreAvis, Integer nombreConsultations, Boolean bagageCabineInclus, Boolean bagageSouteInclus, Boolean annulationGratuite, Boolean repasInclus, Boolean wifiDisponible, String imageUrl, StatutOffre statut, Integer scoreQualitePrix, List<String> badgesRecommandation, List<String> avantagesCles, List<OffreBilletDto> billets) {
        this.id = id;
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
        this.dureeFormatee = dureeFormatee;
        this.escales = escales;
        this.prixBase = prixBase;
        this.prixPromo = prixPromo;
        this.prixEffectif = prixEffectif;
        this.pourcentageReduction = pourcentageReduction;
        this.enPromotion = enPromotion;
        this.placesDisponibles = placesDisponibles;
        this.noteMoyenne = noteMoyenne;
        this.nombreAvis = nombreAvis;
        this.nombreConsultations = nombreConsultations;
        this.bagageCabineInclus = bagageCabineInclus;
        this.bagageSouteInclus = bagageSouteInclus;
        this.annulationGratuite = annulationGratuite;
        this.repasInclus = repasInclus;
        this.wifiDisponible = wifiDisponible;
        this.imageUrl = imageUrl;
        this.statut = statut;
        this.scoreQualitePrix = scoreQualitePrix;
        this.badgesRecommandation = badgesRecommandation;
        this.avantagesCles = avantagesCles;
        this.billets = billets;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
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
    public String getDureeFormatee() {
        return this.dureeFormatee;
    }
    public void setDureeFormatee(String dureeFormatee) {
        this.dureeFormatee = dureeFormatee;
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
    public BigDecimal getPrixEffectif() {
        return this.prixEffectif;
    }
    public void setPrixEffectif(BigDecimal prixEffectif) {
        this.prixEffectif = prixEffectif;
    }
    public Integer getPourcentageReduction() {
        return this.pourcentageReduction;
    }
    public void setPourcentageReduction(Integer pourcentageReduction) {
        this.pourcentageReduction = pourcentageReduction;
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
    public Integer getNombreConsultations() {
        return this.nombreConsultations;
    }
    public void setNombreConsultations(Integer nombreConsultations) {
        this.nombreConsultations = nombreConsultations;
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
    public Integer getScoreQualitePrix() {
        return this.scoreQualitePrix;
    }
    public void setScoreQualitePrix(Integer scoreQualitePrix) {
        this.scoreQualitePrix = scoreQualitePrix;
    }
    public List<String> getBadgesRecommandation() {
        return this.badgesRecommandation;
    }
    public void setBadgesRecommandation(List<String> badgesRecommandation) {
        this.badgesRecommandation = badgesRecommandation;
    }
    public List<String> getAvantagesCles() {
        return this.avantagesCles;
    }
    public void setAvantagesCles(List<String> avantagesCles) {
        this.avantagesCles = avantagesCles;
    }
    public List<OffreBilletDto> getBillets() {
        return this.billets;
    }
    public void setBillets(List<OffreBilletDto> billets) {
        this.billets = billets;
    }

    // --- Builder Pattern ---
    public static VoyageResponseBuilder builder() {
        return new VoyageResponseBuilder();
    }

    public static class VoyageResponseBuilder {
        private Long id;
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
        private String dureeFormatee;
        private Integer escales;
        private BigDecimal prixBase;
        private BigDecimal prixPromo;
        private BigDecimal prixEffectif;
        private Integer pourcentageReduction;
        private Boolean enPromotion;
        private Integer placesDisponibles;
        private Double noteMoyenne;
        private Integer nombreAvis;
        private Integer nombreConsultations;
        private Boolean bagageCabineInclus;
        private Boolean bagageSouteInclus;
        private Boolean annulationGratuite;
        private Boolean repasInclus;
        private Boolean wifiDisponible;
        private String imageUrl;
        private StatutOffre statut;
        private Integer scoreQualitePrix;
        private List<String> badgesRecommandation = new ArrayList<>();
        private List<String> avantagesCles = new ArrayList<>();
        private List<OffreBilletDto> billets = new ArrayList<>();

        public VoyageResponseBuilder() {}

        public VoyageResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public VoyageResponseBuilder codeVoyage(String codeVoyage) {
            this.codeVoyage = codeVoyage;
            return this;
        }
        public VoyageResponseBuilder titre(String titre) {
            this.titre = titre;
            return this;
        }
        public VoyageResponseBuilder description(String description) {
            this.description = description;
            return this;
        }
        public VoyageResponseBuilder villeDepart(String villeDepart) {
            this.villeDepart = villeDepart;
            return this;
        }
        public VoyageResponseBuilder villeDestination(String villeDestination) {
            this.villeDestination = villeDestination;
            return this;
        }
        public VoyageResponseBuilder paysDestination(String paysDestination) {
            this.paysDestination = paysDestination;
            return this;
        }
        public VoyageResponseBuilder compagnie(String compagnie) {
            this.compagnie = compagnie;
            return this;
        }
        public VoyageResponseBuilder typeTransport(TypeTransport typeTransport) {
            this.typeTransport = typeTransport;
            return this;
        }
        public VoyageResponseBuilder categorie(CategorieOffre categorie) {
            this.categorie = categorie;
            return this;
        }
        public VoyageResponseBuilder dateDepart(LocalDateTime dateDepart) {
            this.dateDepart = dateDepart;
            return this;
        }
        public VoyageResponseBuilder dateArrivee(LocalDateTime dateArrivee) {
            this.dateArrivee = dateArrivee;
            return this;
        }
        public VoyageResponseBuilder dureeMinutes(Integer dureeMinutes) {
            this.dureeMinutes = dureeMinutes;
            return this;
        }
        public VoyageResponseBuilder dureeFormatee(String dureeFormatee) {
            this.dureeFormatee = dureeFormatee;
            return this;
        }
        public VoyageResponseBuilder escales(Integer escales) {
            this.escales = escales;
            return this;
        }
        public VoyageResponseBuilder prixBase(BigDecimal prixBase) {
            this.prixBase = prixBase;
            return this;
        }
        public VoyageResponseBuilder prixPromo(BigDecimal prixPromo) {
            this.prixPromo = prixPromo;
            return this;
        }
        public VoyageResponseBuilder prixEffectif(BigDecimal prixEffectif) {
            this.prixEffectif = prixEffectif;
            return this;
        }
        public VoyageResponseBuilder pourcentageReduction(Integer pourcentageReduction) {
            this.pourcentageReduction = pourcentageReduction;
            return this;
        }
        public VoyageResponseBuilder enPromotion(Boolean enPromotion) {
            this.enPromotion = enPromotion;
            return this;
        }
        public VoyageResponseBuilder placesDisponibles(Integer placesDisponibles) {
            this.placesDisponibles = placesDisponibles;
            return this;
        }
        public VoyageResponseBuilder noteMoyenne(Double noteMoyenne) {
            this.noteMoyenne = noteMoyenne;
            return this;
        }
        public VoyageResponseBuilder nombreAvis(Integer nombreAvis) {
            this.nombreAvis = nombreAvis;
            return this;
        }
        public VoyageResponseBuilder nombreConsultations(Integer nombreConsultations) {
            this.nombreConsultations = nombreConsultations;
            return this;
        }
        public VoyageResponseBuilder bagageCabineInclus(Boolean bagageCabineInclus) {
            this.bagageCabineInclus = bagageCabineInclus;
            return this;
        }
        public VoyageResponseBuilder bagageSouteInclus(Boolean bagageSouteInclus) {
            this.bagageSouteInclus = bagageSouteInclus;
            return this;
        }
        public VoyageResponseBuilder annulationGratuite(Boolean annulationGratuite) {
            this.annulationGratuite = annulationGratuite;
            return this;
        }
        public VoyageResponseBuilder repasInclus(Boolean repasInclus) {
            this.repasInclus = repasInclus;
            return this;
        }
        public VoyageResponseBuilder wifiDisponible(Boolean wifiDisponible) {
            this.wifiDisponible = wifiDisponible;
            return this;
        }
        public VoyageResponseBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }
        public VoyageResponseBuilder statut(StatutOffre statut) {
            this.statut = statut;
            return this;
        }
        public VoyageResponseBuilder scoreQualitePrix(Integer scoreQualitePrix) {
            this.scoreQualitePrix = scoreQualitePrix;
            return this;
        }
        public VoyageResponseBuilder badgesRecommandation(List<String> badgesRecommandation) {
            this.badgesRecommandation = badgesRecommandation;
            return this;
        }
        public VoyageResponseBuilder avantagesCles(List<String> avantagesCles) {
            this.avantagesCles = avantagesCles;
            return this;
        }
        public VoyageResponseBuilder billets(List<OffreBilletDto> billets) {
            this.billets = billets;
            return this;
        }

        public VoyageResponse build() {
            VoyageResponse instance = new VoyageResponse();
            instance.id = this.id;
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
            instance.dureeFormatee = this.dureeFormatee;
            instance.escales = this.escales;
            instance.prixBase = this.prixBase;
            instance.prixPromo = this.prixPromo;
            instance.prixEffectif = this.prixEffectif;
            instance.pourcentageReduction = this.pourcentageReduction;
            instance.enPromotion = this.enPromotion;
            instance.placesDisponibles = this.placesDisponibles;
            instance.noteMoyenne = this.noteMoyenne;
            instance.nombreAvis = this.nombreAvis;
            instance.nombreConsultations = this.nombreConsultations;
            instance.bagageCabineInclus = this.bagageCabineInclus;
            instance.bagageSouteInclus = this.bagageSouteInclus;
            instance.annulationGratuite = this.annulationGratuite;
            instance.repasInclus = this.repasInclus;
            instance.wifiDisponible = this.wifiDisponible;
            instance.imageUrl = this.imageUrl;
            instance.statut = this.statut;
            instance.scoreQualitePrix = this.scoreQualitePrix;
            instance.badgesRecommandation = this.badgesRecommandation;
            instance.avantagesCles = this.avantagesCles;
            instance.billets = this.billets;
            return instance;
        }
    }

}

