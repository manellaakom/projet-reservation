package com.travel.paiement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String id,
        String reference,
        LocalDateTime dateTransaction,
        BigDecimal montant,
        String statut,
        String paiementId
) {
}