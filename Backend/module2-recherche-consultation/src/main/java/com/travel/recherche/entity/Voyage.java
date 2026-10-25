package com.travel.recherche.entity;

import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.StatutOffre;
import com.travel.recherche.enums.TypeTransport;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "voyages")
public class Voyage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codeVoyage;

    @Column(nullable = false, length = 160)
    private String titre;

    @Column(length = 1200)
    private String description;

    @Column(nullable = false, length = 80)
    private String villeDepart;

    @Column(nullable = false, length = 80)
    private String villeDestination;

    @Column(nullable = false, length = 80)
    private String paysDestination;

    @Column(nullable = false, length = 100)
    private String compagnie;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TypeTransport typeTransport;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CategorieOffre categorie;

    @Column(nullable = false)
    private LocalDateTime dateDepart;

    @Column(nullable = false)
    private LocalDateTime dateArrivee;

    @Column(nullable = false)
    private Integer dureeMinutes;

    @Column(nullable = false)
    private Integer escales = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixBase;

    @Column(precision = 10, scale = 2)
    private BigDecimal prixPromo;

    @Column(nullable = false)
    private Boolean enPromotion = false;

    @Column(nullable = false)
    private Integer placesDisponibles;

    @Column(nullable = false)
    private Double noteMoyenne = 4.5;

    @Column(nullable = false)
    private Integer nombreAvis = 0;

    @Column(nullable = false)
    private Integer nombreConsultations = 0;

    @Column(nullable = false)
    private Boolean bagageCabineInclus = true;

    @Column(nullable = false)
    private Boolean bagageSouteInclus = false;

    @Column(nullable = false)
    private Boolean annulationGratuite = false;

    @Column(nullable = false)
    private Boolean repasInclus = false;

    @Column(nullable = false)
    private Boolean wifiDisponible = false;

    @Column(length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StatutOffre statut = StatutOffre.DISPONIBLE;

    @OneToMany(mappedBy = "voyage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OffreBillet> billets = new ArrayList<>();

    public BigDecimal getPrixEffectif() {
        if (Boolean.TRUE.equals(enPromotion) && prixPromo != null && prixPromo.compareTo(BigDecimal.ZERO) > 0) {
            return prixPromo;
        }
        return prixBase;
    }

    public void addBillet(OffreBillet billet) {
        billets.add(billet);
        billet.setVoyage(this);
    }


    // --- Constructors ---
    public Voyage() {}

    public Voyage(Long id, String codeVoyage, String titre, String description, String villeDepart, String villeDestination, String paysDestination, String compagnie, TypeTransport typeTransport, CategorieOffre categorie, LocalDateTime dateDepart, LocalDateTime dateArrivee, Integer dureeMinutes, Integer escales, BigDecimal prixBase, BigDecimal prixPromo, Boolean enPromotion, Integer placesDisponibles, Double noteMoyenne, Integer nombreAvis, Integer nombreConsultations, Boolean bagageCabineInclus, Boolean bagageSouteInclus, Boolean annulationGratuite, Boolean repasInclus, Boolean wifiDisponible, String imageUrl, StatutOffre statut, List<OffreBillet> billets) {
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
        this.escales = escales;
        this.prixBase = prixBase;
        this.prixPromo = prixPromo;
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
    public List<OffreBillet> getBillets() {
        return this.billets;
    }
    public void setBillets(List<OffreBillet> billets) {
        this.billets = billets;
    }

    // --- Builder Pattern ---
    public static VoyageBuilder builder() {
        return new VoyageBuilder();
    }

    public static class VoyageBuilder {
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
        private Integer escales = 0;
        private BigDecimal prixBase;
        private BigDecimal prixPromo;
        private Boolean enPromotion = false;
        private Integer placesDisponibles;
        private Double noteMoyenne = 4.5;
        private Integer nombreAvis = 0;
        private Integer nombreConsultations = 0;
        private Boolean bagageCabineInclus = true;
        private Boolean bagageSouteInclus = false;
        private Boolean annulationGratuite = false;
        private Boolean repasInclus = false;
        private Boolean wifiDisponible = false;
        private String imageUrl;
        private StatutOffre statut = StatutOffre.DISPONIBLE;
        private List<OffreBillet> billets = new ArrayList<>();

        public VoyageBuilder() {}

        public VoyageBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public VoyageBuilder codeVoyage(String codeVoyage) {
            this.codeVoyage = codeVoyage;
            return this;
        }
        public VoyageBuilder titre(String titre) {
            this.titre = titre;
            return this;
        }
        public VoyageBuilder description(String description) {
            this.description = description;
            return this;
        }
        public VoyageBuilder villeDepart(String villeDepart) {
            this.villeDepart = villeDepart;
            return this;
        }
        public VoyageBuilder villeDestination(String villeDestination) {
            this.villeDestination = villeDestination;
            return this;
        }
        public VoyageBuilder paysDestination(String paysDestination) {
            this.paysDestination = paysDestination;
            return this;
        }
        public VoyageBuilder compagnie(String compagnie) {
            this.compagnie = compagnie;
            return this;
        }
        public VoyageBuilder typeTransport(TypeTransport typeTransport) {
            this.typeTransport = typeTransport;
            return this;
        }
        public VoyageBuilder categorie(CategorieOffre categorie) {
            this.categorie = categorie;
            return this;
        }
        public VoyageBuilder dateDepart(LocalDateTime dateDepart) {
            this.dateDepart = dateDepart;
            return this;
        }
        public VoyageBuilder dateArrivee(LocalDateTime dateArrivee) {
            this.dateArrivee = dateArrivee;
            return this;
        }
        public VoyageBuilder dureeMinutes(Integer dureeMinutes) {
            this.dureeMinutes = dureeMinutes;
            return this;
        }
        public VoyageBuilder escales(Integer escales) {
            this.escales = escales;
            return this;
        }
        public VoyageBuilder prixBase(BigDecimal prixBase) {
            this.prixBase = prixBase;
            return this;
        }
        public VoyageBuilder prixPromo(BigDecimal prixPromo) {
            this.prixPromo = prixPromo;
            return this;
        }
        public VoyageBuilder enPromotion(Boolean enPromotion) {
            this.enPromotion = enPromotion;
            return this;
        }
        public VoyageBuilder placesDisponibles(Integer placesDisponibles) {
            this.placesDisponibles = placesDisponibles;
            return this;
        }
        public VoyageBuilder noteMoyenne(Double noteMoyenne) {
            this.noteMoyenne = noteMoyenne;
            return this;
        }
        public VoyageBuilder nombreAvis(Integer nombreAvis) {
            this.nombreAvis = nombreAvis;
            return this;
        }
        public VoyageBuilder nombreConsultations(Integer nombreConsultations) {
            this.nombreConsultations = nombreConsultations;
            return this;
        }
        public VoyageBuilder bagageCabineInclus(Boolean bagageCabineInclus) {
            this.bagageCabineInclus = bagageCabineInclus;
            return this;
        }
        public VoyageBuilder bagageSouteInclus(Boolean bagageSouteInclus) {
            this.bagageSouteInclus = bagageSouteInclus;
            return this;
        }
        public VoyageBuilder annulationGratuite(Boolean annulationGratuite) {
            this.annulationGratuite = annulationGratuite;
            return this;
        }
        public VoyageBuilder repasInclus(Boolean repasInclus) {
            this.repasInclus = repasInclus;
            return this;
        }
        public VoyageBuilder wifiDisponible(Boolean wifiDisponible) {
            this.wifiDisponible = wifiDisponible;
            return this;
        }
        public VoyageBuilder imageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }
        public VoyageBuilder statut(StatutOffre statut) {
            this.statut = statut;
            return this;
        }
        public VoyageBuilder billets(List<OffreBillet> billets) {
            this.billets = billets;
            return this;
        }

        public Voyage build() {
            Voyage instance = new Voyage();
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
            instance.escales = this.escales;
            instance.prixBase = this.prixBase;
            instance.prixPromo = this.prixPromo;
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
            instance.billets = this.billets;
            return instance;
        }
    }

}

