package com.travel.paiement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Contrat d'echange avec le Module 1 (Feign PaymentServiceClient) :
 * enregistrement du montant d'un bagage dans le module de paiement.
 */
public record BagagePaiementDto(
        String paymentId,
        String bagageId,
        String reservationId,
        @NotNull @Positive Double montant,
        String devise,
        String statut,
        String message
) {
}
