package com.travel.recherche.dto;

import com.travel.recherche.enums.ClasseBillet;
import com.travel.recherche.enums.TypeTransport;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OffreBilletDto {

    private Long id;

    @NotBlank(message = "Le code de l'offre de billet est obligatoire")
    private String codeOffreBillet;

    @NotNull(message = "La classe du billet est obligatoire")
    private ClasseBillet classe;

    @NotNull(message = "Le prix du billet est obligatoire")
    @Min(value = 1, message = "Le prix doit être supérieur à 0")
    private BigDecimal prix;

    @NotNull(message = "Le nombre de places restantes est obligatoire")
    @Min(value = 0, message = "Le nombre de places ne peut pas être négatif")
    private Integer placesRestantes;
    @Min(value = 0, message = "Le poids du bagage cabine ne peut pas être négatif")
    @Max(value = 30, message = "Le poids du bagage ne peut pas dépasser 30 kg (règle métier Module 1)")
    private Integer bagageCabineKg = 8;
    @Min(value = 0, message = "Le poids du bagage soute ne peut pas être négatif")
    @Max(value = 30, message = "Le poids du bagage en soute ne peut pas dépasser 30 kg (règle métier Module 1)")
    private Integer bagageSouteKg = 0;
    private Boolean modifiable = false;
    private Boolean remboursable = false;
    private Boolean choixSiegeGratuit = false;
    private Boolean accesSalonVip = false;

    private String conditionsTarifaires;

    // Informations du voyage associé (utiles lors de la recherche directe de billets)
    private Long voyageId;
    private String codeVoyage;
    private String titreVoyage;
    private String villeDepart;
    private String villeDestination;
    private String compagnie;
    private TypeTransport typeTransport;
    private LocalDateTime dateDepart;
    private LocalDateTime dateArrivee;


    // --- Constructors ---
    public OffreBilletDto() {}

    public OffreBilletDto(Long id, String codeOffreBillet, ClasseBillet classe, BigDecimal prix, Integer placesRestantes, Integer bagageCabineKg, Integer bagageSouteKg, Boolean modifiable, Boolean remboursable, Boolean choixSiegeGratuit, Boolean accesSalonVip, String conditionsTarifaires, Long voyageId, String codeVoyage, String titreVoyage, String villeDepart, String villeDestination, String compagnie, TypeTransport typeTransport, LocalDateTime dateDepart, LocalDateTime dateArrivee) {
        this.id = id;
        this.codeOffreBillet = codeOffreBillet;
        this.classe = classe;
        this.prix = prix;
        this.placesRestantes = placesRestantes;
        this.bagageCabineKg = bagageCabineKg;
        this.bagageSouteKg = bagageSouteKg;
        this.modifiable = modifiable;
        this.remboursable = remboursable;
        this.choixSiegeGratuit = choixSiegeGratuit;
        this.accesSalonVip = accesSalonVip;
        this.conditionsTarifaires = conditionsTarifaires;
        this.voyageId = voyageId;
        this.codeVoyage = codeVoyage;
        this.titreVoyage = titreVoyage;
        this.villeDepart = villeDepart;
        this.villeDestination = villeDestination;
        this.compagnie = compagnie;
        this.typeTransport = typeTransport;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCodeOffreBillet() {
        return this.codeOffreBillet;
    }
    public void setCodeOffreBillet(String codeOffreBillet) {
        this.codeOffreBillet = codeOffreBillet;
    }
    public ClasseBillet getClasse() {
        return this.classe;
    }
    public void setClasse(ClasseBillet classe) {
        this.classe = classe;
    }
    public BigDecimal getPrix() {
        return this.prix;
    }
    public void setPrix(BigDecimal prix) {
        this.prix = prix;
    }
    public Integer getPlacesRestantes() {
        return this.placesRestantes;
    }
    public void setPlacesRestantes(Integer placesRestantes) {
        this.placesRestantes = placesRestantes;
    }
    public Integer getBagageCabineKg() {
        return this.bagageCabineKg;
    }
    public void setBagageCabineKg(Integer bagageCabineKg) {
        this.bagageCabineKg = bagageCabineKg;
    }
    public Integer getBagageSouteKg() {
        return this.bagageSouteKg;
    }
    public void setBagageSouteKg(Integer bagageSouteKg) {
        this.bagageSouteKg = bagageSouteKg;
    }
    public Boolean getModifiable() {
        return this.modifiable;
    }
    public Boolean isModifiable() {
        return this.modifiable;
    }
    public void setModifiable(Boolean modifiable) {
        this.modifiable = modifiable;
    }
    public Boolean getRemboursable() {
        return this.remboursable;
    }
    public Boolean isRemboursable() {
        return this.remboursable;
    }
    public void setRemboursable(Boolean remboursable) {
        this.remboursable = remboursable;
    }
    public Boolean getChoixSiegeGratuit() {
        return this.choixSiegeGratuit;
    }
    public Boolean isChoixSiegeGratuit() {
        return this.choixSiegeGratuit;
    }
    public void setChoixSiegeGratuit(Boolean choixSiegeGratuit) {
        this.choixSiegeGratuit = choixSiegeGratuit;
    }
    public Boolean getAccesSalonVip() {
        return this.accesSalonVip;
    }
    public Boolean isAccesSalonVip() {
        return this.accesSalonVip;
    }
    public void setAccesSalonVip(Boolean accesSalonVip) {
        this.accesSalonVip = accesSalonVip;
    }
    public String getConditionsTarifaires() {
        return this.conditionsTarifaires;
    }
    public void setConditionsTarifaires(String conditionsTarifaires) {
        this.conditionsTarifaires = conditionsTarifaires;
    }
    public Long getVoyageId() {
        return this.voyageId;
    }
    public void setVoyageId(Long voyageId) {
        this.voyageId = voyageId;
    }
    public String getCodeVoyage() {
        return this.codeVoyage;
    }
    public void setCodeVoyage(String codeVoyage) {
        this.codeVoyage = codeVoyage;
    }
    public String getTitreVoyage() {
        return this.titreVoyage;
    }
    public void setTitreVoyage(String titreVoyage) {
        this.titreVoyage = titreVoyage;
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

    // --- Builder Pattern ---
    public static OffreBilletDtoBuilder builder() {
        return new OffreBilletDtoBuilder();
    }

    public static class OffreBilletDtoBuilder {
        private Long id;
        private String codeOffreBillet;
        private ClasseBillet classe;
        private BigDecimal prix;
        private Integer placesRestantes;
        private Integer bagageCabineKg = 8;
        private Integer bagageSouteKg = 0;
        private Boolean modifiable = false;
        private Boolean remboursable = false;
        private Boolean choixSiegeGratuit = false;
        private Boolean accesSalonVip = false;
        private String conditionsTarifaires;
        private Long voyageId;
        private String codeVoyage;
        private String titreVoyage;
        private String villeDepart;
        private String villeDestination;
        private String compagnie;
        private TypeTransport typeTransport;
        private LocalDateTime dateDepart;
        private LocalDateTime dateArrivee;

        public OffreBilletDtoBuilder() {}

        public OffreBilletDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public OffreBilletDtoBuilder codeOffreBillet(String codeOffreBillet) {
            this.codeOffreBillet = codeOffreBillet;
            return this;
        }
        public OffreBilletDtoBuilder classe(ClasseBillet classe) {
            this.classe = classe;
            return this;
        }
        public OffreBilletDtoBuilder prix(BigDecimal prix) {
            this.prix = prix;
            return this;
        }
        public OffreBilletDtoBuilder placesRestantes(Integer placesRestantes) {
            this.placesRestantes = placesRestantes;
            return this;
        }
        public OffreBilletDtoBuilder bagageCabineKg(Integer bagageCabineKg) {
            this.bagageCabineKg = bagageCabineKg;
            return this;
        }
        public OffreBilletDtoBuilder bagageSouteKg(Integer bagageSouteKg) {
            this.bagageSouteKg = bagageSouteKg;
            return this;
        }
        public OffreBilletDtoBuilder modifiable(Boolean modifiable) {
            this.modifiable = modifiable;
            return this;
        }
        public OffreBilletDtoBuilder remboursable(Boolean remboursable) {
            this.remboursable = remboursable;
            return this;
        }
        public OffreBilletDtoBuilder choixSiegeGratuit(Boolean choixSiegeGratuit) {
            this.choixSiegeGratuit = choixSiegeGratuit;
            return this;
        }
        public OffreBilletDtoBuilder accesSalonVip(Boolean accesSalonVip) {
            this.accesSalonVip = accesSalonVip;
            return this;
        }
        public OffreBilletDtoBuilder conditionsTarifaires(String conditionsTarifaires) {
            this.conditionsTarifaires = conditionsTarifaires;
            return this;
        }
        public OffreBilletDtoBuilder voyageId(Long voyageId) {
            this.voyageId = voyageId;
            return this;
        }
        public OffreBilletDtoBuilder codeVoyage(String codeVoyage) {
            this.codeVoyage = codeVoyage;
            return this;
        }
        public OffreBilletDtoBuilder titreVoyage(String titreVoyage) {
            this.titreVoyage = titreVoyage;
            return this;
        }
        public OffreBilletDtoBuilder villeDepart(String villeDepart) {
            this.villeDepart = villeDepart;
            return this;
        }
        public OffreBilletDtoBuilder villeDestination(String villeDestination) {
            this.villeDestination = villeDestination;
            return this;
        }
        public OffreBilletDtoBuilder compagnie(String compagnie) {
            this.compagnie = compagnie;
            return this;
        }
        public OffreBilletDtoBuilder typeTransport(TypeTransport typeTransport) {
            this.typeTransport = typeTransport;
            return this;
        }
        public OffreBilletDtoBuilder dateDepart(LocalDateTime dateDepart) {
            this.dateDepart = dateDepart;
            return this;
        }
        public OffreBilletDtoBuilder dateArrivee(LocalDateTime dateArrivee) {
            this.dateArrivee = dateArrivee;
            return this;
        }

        public OffreBilletDto build() {
            OffreBilletDto instance = new OffreBilletDto();
            instance.id = this.id;
            instance.codeOffreBillet = this.codeOffreBillet;
            instance.classe = this.classe;
            instance.prix = this.prix;
            instance.placesRestantes = this.placesRestantes;
            instance.bagageCabineKg = this.bagageCabineKg;
            instance.bagageSouteKg = this.bagageSouteKg;
            instance.modifiable = this.modifiable;
            instance.remboursable = this.remboursable;
            instance.choixSiegeGratuit = this.choixSiegeGratuit;
            instance.accesSalonVip = this.accesSalonVip;
            instance.conditionsTarifaires = this.conditionsTarifaires;
            instance.voyageId = this.voyageId;
            instance.codeVoyage = this.codeVoyage;
            instance.titreVoyage = this.titreVoyage;
            instance.villeDepart = this.villeDepart;
            instance.villeDestination = this.villeDestination;
            instance.compagnie = this.compagnie;
            instance.typeTransport = this.typeTransport;
            instance.dateDepart = this.dateDepart;
            instance.dateArrivee = this.dateArrivee;
            return instance;
        }
    }

}

