package com.travel.recherche.dto;

import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.ClasseBillet;
import com.travel.recherche.enums.TypeTransport;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CritereRechercheRequest {

    private String motCle;
    private String villeDepart;
    private String villeDestination;
    private String paysDestination;
    private String compagnie;
    private TypeTransport typeTransport;
    private CategorieOffre categorie;
    private ClasseBillet classeBillet;
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
    @Builder.Default
    private String sortBy = "prix";

    // Direction : asc, desc
    @Builder.Default
    private String sortDir = "asc";
}
