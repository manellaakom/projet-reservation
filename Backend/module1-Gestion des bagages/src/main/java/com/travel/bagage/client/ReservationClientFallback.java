package com.travel.bagage.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReservationClientFallback implements ReservationServiceClient {

    private static final Logger log = LoggerFactory.getLogger(ReservationClientFallback.class);

    @Override
    public ReservationDto getReservationById(String reservationId) {
        log.warn("Circuit Breaker activé: service Réservation indisponible pour ID {}. Utilisation du fallback sécurisé.", reservationId);
        return ReservationDto.builder()
                .id(reservationId)
                .codeReservation(reservationId)
                .voyageurNom("Voyageur Standard (Fallback DCS)")
                .volNumero("VOL-LOCAL")
                .statut("EN_ATTENTE_SYNC")
                .valide(true)
                .message("Mode dégradé : vérification locale acceptée suite à indisponibilité temporaire du service réservation.")
                .build();
    }
}
