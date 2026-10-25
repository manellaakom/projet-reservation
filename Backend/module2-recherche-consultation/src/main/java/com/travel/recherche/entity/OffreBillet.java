package com.travel.recherche.entity;

import com.travel.recherche.enums.ClasseBillet;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "offres_billets")
public class OffreBillet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codeOffreBillet;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ClasseBillet classe;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    @Column(nullable = false)
    private Integer placesRestantes;

    @Column(nullable = false)
    private Integer bagageCabineKg = 8;

    @Column(nullable = false)
    private Integer bagageSouteKg = 0;

    @Column(nullable = false)
    private Boolean modifiable = false;

    @Column(nullable = false)
    private Boolean remboursable = false;

    @Column(nullable = false)
    private Boolean choixSiegeGratuit = false;

    @Column(nullable = false)
    private Boolean accesSalonVip = false;

    @Column(length = 500)
    private String conditionsTarifaires;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voyage_id", nullable = false)
    private Voyage voyage;


    // --- Constructors ---
    public OffreBillet() {}

    public OffreBillet(Long id, String codeOffreBillet, ClasseBillet classe, BigDecimal prix, Integer placesRestantes, Integer bagageCabineKg, Integer bagageSouteKg, Boolean modifiable, Boolean remboursable, Boolean choixSiegeGratuit, Boolean accesSalonVip, String conditionsTarifaires, Voyage voyage) {
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
        this.voyage = voyage;
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
    public Voyage getVoyage() {
        return this.voyage;
    }
    public void setVoyage(Voyage voyage) {
        this.voyage = voyage;
    }

    // --- Builder Pattern ---
    public static OffreBilletBuilder builder() {
        return new OffreBilletBuilder();
    }

    public static class OffreBilletBuilder {
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
        private Voyage voyage;

        public OffreBilletBuilder() {}

        public OffreBilletBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public OffreBilletBuilder codeOffreBillet(String codeOffreBillet) {
            this.codeOffreBillet = codeOffreBillet;
            return this;
        }
        public OffreBilletBuilder classe(ClasseBillet classe) {
            this.classe = classe;
            return this;
        }
        public OffreBilletBuilder prix(BigDecimal prix) {
            this.prix = prix;
            return this;
        }
        public OffreBilletBuilder placesRestantes(Integer placesRestantes) {
            this.placesRestantes = placesRestantes;
            return this;
        }
        public OffreBilletBuilder bagageCabineKg(Integer bagageCabineKg) {
            this.bagageCabineKg = bagageCabineKg;
            return this;
        }
        public OffreBilletBuilder bagageSouteKg(Integer bagageSouteKg) {
            this.bagageSouteKg = bagageSouteKg;
            return this;
        }
        public OffreBilletBuilder modifiable(Boolean modifiable) {
            this.modifiable = modifiable;
            return this;
        }
        public OffreBilletBuilder remboursable(Boolean remboursable) {
            this.remboursable = remboursable;
            return this;
        }
        public OffreBilletBuilder choixSiegeGratuit(Boolean choixSiegeGratuit) {
            this.choixSiegeGratuit = choixSiegeGratuit;
            return this;
        }
        public OffreBilletBuilder accesSalonVip(Boolean accesSalonVip) {
            this.accesSalonVip = accesSalonVip;
            return this;
        }
        public OffreBilletBuilder conditionsTarifaires(String conditionsTarifaires) {
            this.conditionsTarifaires = conditionsTarifaires;
            return this;
        }
        public OffreBilletBuilder voyage(Voyage voyage) {
            this.voyage = voyage;
            return this;
        }

        public OffreBillet build() {
            OffreBillet instance = new OffreBillet();
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
            instance.voyage = this.voyage;
            return instance;
        }
    }

}

