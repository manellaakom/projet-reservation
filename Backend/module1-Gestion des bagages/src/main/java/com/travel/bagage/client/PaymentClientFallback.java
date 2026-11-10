package com.travel.bagage.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentClientFallback implements PaymentServiceClient {

    private static final Logger log = LoggerFactory.getLogger(PaymentClientFallback.class);

    @Override
    public PaymentDto recordBaggagePayment(PaymentDto paymentDto) {
        log.warn("Circuit Breaker activé: service Paiement indisponible pour bagage {}. Transaction en file d'attente différée.", paymentDto.getBagageId());
        return PaymentDto.builder()
                .paymentId("PAY-FALLBACK-" + UUID.randomUUID().toString().substring(0, 8))
                .bagageId(paymentDto.getBagageId())
                .reservationId(paymentDto.getReservationId())
                .montant(paymentDto.getMontant())
                .devise("TND")
                .statut("EN_ATTENTE_SYNC")
                .message("Mode dégradé : montant enregistré localement dans le module bagages (30/50/70 DT), paiement différé.")
                .build();
    }
}
