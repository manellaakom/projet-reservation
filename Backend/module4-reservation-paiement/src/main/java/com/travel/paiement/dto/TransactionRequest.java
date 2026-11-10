package com.travel.paiement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionRequest(
        @NotBlank String reference,
        @NotNull LocalDateTime dateTransaction,
        @NotNull @Positive BigDecimal montant,
        @NotBlank String statut
) {
}