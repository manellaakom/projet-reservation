package com.travel.recherche.dto;

import com.travel.recherche.enums.CategorieOffre;
import com.travel.recherche.enums.StatutOffre;
import com.travel.recherche.enums.TypeTransport;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Builder.Default
    private Integer escales = 0;

    @NotNull(message = "Le prix de base est obligatoire")
    @Min(value = 1, message = "Le prix de base doit être positif")
    private BigDecimal prixBase;

    private BigDecimal prixPromo;

    @Builder.Default
    private Boolean enPromotion = false;

    @NotNull(message = "Le nombre de places disponibles est obligatoire")
    @Min(value = 0, message = "Le nombre de places ne peut pas être négatif")
    private Integer placesDisponibles;

    @Builder.Default
    private Double noteMoyenne = 4.5;

    @Builder.Default
    private Integer nombreAvis = 10;

    @Builder.Default
    private Boolean bagageCabineInclus = true;

    @Builder.Default
    private Boolean bagageSouteInclus = false;

    @Builder.Default
    private Boolean annulationGratuite = false;

    @Builder.Default
    private Boolean repasInclus = false;

    @Builder.Default
    private Boolean wifiDisponible = false;

    private String imageUrl;

    @Builder.Default
    private StatutOffre statut = StatutOffre.DISPONIBLE;

    @Valid
    @Builder.Default
    private List<OffreBilletDto> billets = new ArrayList<>();
}
