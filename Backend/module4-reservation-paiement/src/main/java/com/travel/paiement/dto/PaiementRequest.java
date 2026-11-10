package com.travel.paiement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaiementRequest(
        @NotNull @Positive BigDecimal montant,
        @NotNull LocalDateTime datePaiement,
        @NotBlank String statut,
        @NotBlank String reference
) {
}