package com.travel.bagage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BagageRequest {
    
    private String reservationId;

    @NotBlank(message = "Le type de bagage est obligatoire")
    private String type;

    @NotNull(message = "Le poids est obligatoire")
    @Positive(message = "Le poids doit être positif")
    private Double poids;

    @NotBlank(message = "Le statut est obligatoire")
    private String statut;

    // Calculé automatiquement selon le poids du bagage si omis
    private Double prix;
}
