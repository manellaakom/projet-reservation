package com.travel.paiement.mapper;

import com.travel.paiement.dto.PaiementResponse;
import com.travel.paiement.entity.Paiement;

public final class PaiementMapper {

    private PaiementMapper() {
    }

    public static PaiementResponse toResponse(Paiement paiement) {
        return new PaiementResponse(
                paiement.getId(),
                paiement.getMontant(),
                paiement.getDatePaiement(),
                paiement.getStatut(),
                paiement.getReference(),
                paiement.getTransactionId()
        );
    }
}