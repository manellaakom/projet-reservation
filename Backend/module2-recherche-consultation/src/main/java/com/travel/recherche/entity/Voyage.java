package com.travel.recherche.entity;

import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.StatutOffre;
import com.travel.recherche.enums.TypeTransport;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "voyages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private Integer escales = 0;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prixBase;

    @Column(precision = 10, scale = 2)
    private BigDecimal prixPromo;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enPromotion = false;

    @Column(nullable = false)
    private Integer placesDisponibles;

    @Column(nullable = false)
    @Builder.Default
    private Double noteMoyenne = 4.5;

    @Column(nullable = false)
    @Builder.Default
    private Integer nombreAvis = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer nombreConsultations = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean bagageCabineInclus = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean bagageSouteInclus = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean annulationGratuite = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean repasInclus = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean wifiDisponible = false;

    @Column(length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    @Builder.Default
    private StatutOffre statut = StatutOffre.DISPONIBLE;

    @OneToMany(mappedBy = "voyage", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
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
}
