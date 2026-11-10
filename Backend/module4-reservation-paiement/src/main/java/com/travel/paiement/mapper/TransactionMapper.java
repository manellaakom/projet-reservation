package com.travel.paiement.mapper;

import com.travel.paiement.dto.TransactionResponse;
import com.travel.paiement.entity.Transaction;

public final class TransactionMapper {

    private TransactionMapper() {
    }

    public static TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getReference(),
                transaction.getDateTransaction(),
                transaction.getMontant(),
                transaction.getStatut(),
                transaction.getPaiementId()
        );
    }
}