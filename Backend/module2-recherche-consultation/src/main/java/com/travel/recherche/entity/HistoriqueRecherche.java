package com.travel.recherche.entity;

import com.travel.recherche.enums.ClasseBillet;
import com.travel.recherche.enums.TypeTransport;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "historiques_recherches")
public class HistoriqueRecherche {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String motCle;
    private String villeDepart;
    private String villeDestination;

    @Enumerated(EnumType.STRING)
    private TypeTransport typeTransport;

    @Enumerated(EnumType.STRING)
    private ClasseBillet classeBillet;

    @Column(precision = 10, scale = 2)
    private BigDecimal budgetMax;

    private Integer nombreResultats;

    @Column(nullable = false)
    private LocalDateTime dateRecherche = LocalDateTime.now();


    // --- Constructors ---
    public HistoriqueRecherche() {}

    public HistoriqueRecherche(Long id, String motCle, String villeDepart, String villeDestination, TypeTransport typeTransport, ClasseBillet classeBillet, BigDecimal budgetMax, Integer nombreResultats, LocalDateTime dateRecherche) {
        this.id = id;
        this.motCle = motCle;
        this.villeDepart = villeDepart;
        this.villeDestination = villeDestination;
        this.typeTransport = typeTransport;
        this.classeBillet = classeBillet;
        this.budgetMax = budgetMax;
        this.nombreResultats = nombreResultats;
        this.dateRecherche = dateRecherche;
    }

    // --- Getters & Setters ---
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
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
    public TypeTransport getTypeTransport() {
        return this.typeTransport;
    }
    public void setTypeTransport(TypeTransport typeTransport) {
        this.typeTransport = typeTransport;
    }
    public ClasseBillet getClasseBillet() {
        return this.classeBillet;
    }
    public void setClasseBillet(ClasseBillet classeBillet) {
        this.classeBillet = classeBillet;
    }
    public BigDecimal getBudgetMax() {
        return this.budgetMax;
    }
    public void setBudgetMax(BigDecimal budgetMax) {
        this.budgetMax = budgetMax;
    }
    public Integer getNombreResultats() {
        return this.nombreResultats;
    }
    public void setNombreResultats(Integer nombreResultats) {
        this.nombreResultats = nombreResultats;
    }
    public LocalDateTime getDateRecherche() {
        return this.dateRecherche;
    }
    public void setDateRecherche(LocalDateTime dateRecherche) {
        this.dateRecherche = dateRecherche;
    }

    // --- Builder Pattern ---
    public static HistoriqueRechercheBuilder builder() {
        return new HistoriqueRechercheBuilder();
    }

    public static class HistoriqueRechercheBuilder {
        private Long id;
        private String motCle;
        private String villeDepart;
        private String villeDestination;
        private TypeTransport typeTransport;
        private ClasseBillet classeBillet;
        private BigDecimal budgetMax;
        private Integer nombreResultats;
        private LocalDateTime dateRecherche = LocalDateTime.now();

        public HistoriqueRechercheBuilder() {}

        public HistoriqueRechercheBuilder id(Long id) {
            this.id = id;
            return this;
        }
        public HistoriqueRechercheBuilder motCle(String motCle) {
            this.motCle = motCle;
            return this;
        }
        public HistoriqueRechercheBuilder villeDepart(String villeDepart) {
            this.villeDepart = villeDepart;
            return this;
        }
        public HistoriqueRechercheBuilder villeDestination(String villeDestination) {
            this.villeDestination = villeDestination;
            return this;
        }
        public HistoriqueRechercheBuilder typeTransport(TypeTransport typeTransport) {
            this.typeTransport = typeTransport;
            return this;
        }
        public HistoriqueRechercheBuilder classeBillet(ClasseBillet classeBillet) {
            this.classeBillet = classeBillet;
            return this;
        }
        public HistoriqueRechercheBuilder budgetMax(BigDecimal budgetMax) {
            this.budgetMax = budgetMax;
            return this;
        }
        public HistoriqueRechercheBuilder nombreResultats(Integer nombreResultats) {
            this.nombreResultats = nombreResultats;
            return this;
        }
        public HistoriqueRechercheBuilder dateRecherche(LocalDateTime dateRecherche) {
            this.dateRecherche = dateRecherche;
            return this;
        }

        public HistoriqueRecherche build() {
            HistoriqueRecherche instance = new HistoriqueRecherche();
            instance.id = this.id;
            instance.motCle = this.motCle;
            instance.villeDepart = this.villeDepart;
            instance.villeDestination = this.villeDestination;
            instance.typeTransport = this.typeTransport;
            instance.classeBillet = this.classeBillet;
            instance.budgetMax = this.budgetMax;
            instance.nombreResultats = this.nombreResultats;
            instance.dateRecherche = this.dateRecherche;
            return instance;
        }
    }

}

