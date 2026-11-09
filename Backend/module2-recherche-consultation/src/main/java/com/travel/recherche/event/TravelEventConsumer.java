package com.travel.recherche.event;

import com.travel.recherche.config.RabbitMQConfig;
import com.travel.recherche.entity.Voyage;
import com.travel.recherche.repository.VoyageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Consommateur RabbitMQ pour le Module 2 (Recherche & Consultation).
 * Reçoit les événements asynchrones et met à jour l'état du catalogue.
 */
@Component
public class TravelEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TravelEventConsumer.class);

    private final VoyageRepository voyageRepository;

    public TravelEventConsumer(VoyageRepository voyageRepository) {
        this.voyageRepository = voyageRepository;
    }

    /**
     * Scénario 1 (Asynchrone) : Consommation de l'événement Réservation Créée
     * - Décrémente les places disponibles
     * - Incrémente le compteur de consultations/popularité
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_RESERVATION)
    public void consommerReservationEvent(ReservationEvent event) {
        log.info(" [RabbitMQ] Réception ReservationEvent : {}", event);

        if (event.getVoyageId() == null) {
            log.warn("Voyage ID absent dans l'événement réservation");
            return;
        }

        Optional<Voyage> optionalVoyage = voyageRepository.findById(event.getVoyageId());
        if (optionalVoyage.isPresent()) {
            Voyage voyage = optionalVoyage.get();
            int placesActuelles = voyage.getPlacesDisponibles() != null ? voyage.getPlacesDisponibles() : 0;
            int placesReservees = event.getNombrePlacesReservees() > 0 ? event.getNombrePlacesReservees() : 1;
            int nouvellesPlaces = Math.max(0, placesActuelles - placesReservees);

            voyage.setPlacesDisponibles(nouvellesPlaces);
            voyage.setNombreConsultations((voyage.getNombreConsultations() != null ? voyage.getNombreConsultations() : 0) + 10);
            voyageRepository.save(voyage);

            log.info(" [RabbitMQ] Voyage #{} mis à jour : {} places restantes, consultations = {}",
                    voyage.getId(), nouvellesPlaces, voyage.getNombreConsultations());
        } else {
            log.warn("Voyage #{} introuvable pour la réservation reçue", event.getVoyageId());
        }
    }

    /**
     * Scénario 2 (Asynchrone) : Consommation de l'événement Paiement Confirmé
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PAIEMENT)
    public void consommerPaiementEvent(PaiementEvent event) {
        log.info(" [RabbitMQ] Réception PaiementEvent : Paiement {} validé de {} € pour voyage #{}",
                event.getPaiementId(), event.getMontant(), event.getVoyageId());

        if (event.getVoyageId() != null) {
            voyageRepository.findById(event.getVoyageId()).ifPresent(voyage -> {
                voyage.setNombreConsultations((voyage.getNombreConsultations() != null ? voyage.getNombreConsultations() : 0) + 5);
                voyageRepository.save(voyage);
                log.info(" [RabbitMQ] Popularité boostée suite au paiement validé pour le voyage #{}", voyage.getId());
            });
        }
    }

    /**
     * Scénario 3 (Asynchrone) : Consommation d'une Promotion Flash
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_PROMOTIONS)
    public void consommerPromotionEvent(PromotionEvent event) {
        log.info(" [RabbitMQ] Réception PromotionEvent : Flash Promo sur voyage #{} (-{}%)",
                event.getVoyageId(), event.getPourcentageRemise());

        if (event.getVoyageId() != null) {
            voyageRepository.findById(event.getVoyageId()).ifPresent(voyage -> {
                voyage.setEnPromotion(true);
                if (event.getNouveauPrix() != null) {
                    voyage.setPrixPromo(event.getNouveauPrix());
                }
                voyageRepository.save(voyage);
                log.info(" [RabbitMQ] Voyage #{} passé en promotion avec prix promo de {} €",
                        voyage.getId(), voyage.getPrixPromo());
            });
        }
    }
}
