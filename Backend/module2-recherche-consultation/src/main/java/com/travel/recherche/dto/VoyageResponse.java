package com.travel.recherche.dto;

import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.StatutOffre;
import com.travel.recherche.enums.TypeTransport;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private List<String> badgesRecommandation = new ArrayList<>();
    @Builder.Default
    private List<String> avantagesCles = new ArrayList<>();

    @Builder.Default
    private List<OffreBilletDto> billets = new ArrayList<>();
}
