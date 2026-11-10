package com.travel.paiement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaiementResponse(
        String id,
        BigDecimal montant,
        LocalDateTime datePaiement,
        String statut,
        String reference,
        String transactionId
) {
}