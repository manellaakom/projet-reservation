package com.travel.recherche.dto;

import com.travel.recherche.enums.ClasseBillet;
import com.travel.recherche.enums.TypeTransport;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Builder.Default
    @Min(value = 0, message = "Le poids du bagage cabine ne peut pas être négatif")
    @Max(value = 30, message = "Le poids du bagage ne peut pas dépasser 30 kg (règle métier Module 1)")
    private Integer bagageCabineKg = 8;

    @Builder.Default
    @Min(value = 0, message = "Le poids du bagage soute ne peut pas être négatif")
    @Max(value = 30, message = "Le poids du bagage en soute ne peut pas dépasser 30 kg (règle métier Module 1)")
    private Integer bagageSouteKg = 0;

    @Builder.Default
    private Boolean modifiable = false;

    @Builder.Default
    private Boolean remboursable = false;

    @Builder.Default
    private Boolean choixSiegeGratuit = false;

    @Builder.Default
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
}
