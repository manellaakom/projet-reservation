package com.travel.bagage.messaging;

import com.travel.bagage.entity.Bagage;
import com.travel.bagage.repository.BagageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BaggageConsumer {

    private static final Logger log = LoggerFactory.getLogger(BaggageConsumer.class);

    private final BagageRepository bagageRepository;

    public BaggageConsumer(BagageRepository bagageRepository) {
        this.bagageRepository = bagageRepository;
    }

    /**
     * Consommateur Scénario 1 : Traitement du tri et acheminement du bagage
     */
    @RabbitListener(queues = RabbitMqConfig.QUEUE_BAGAGE_CREATED)
    public void handleBaggageCreated(BaggageEvent event) {
        log.info("Message RabbitMQ reçu sur [{}] -> Traitement logistique : Bagage {} pesant {} kg dirigé vers le circuit de soute.",
                RabbitMqConfig.QUEUE_BAGAGE_CREATED, event.getBagageId(), event.getPoids());
    }

    /**
     * Consommateur Scénario 2 : Annulation de vol / réservation reçue de manière asynchrone
     */
    @RabbitListener(queues = RabbitMqConfig.QUEUE_RESERVATION_CANCELLED)
    public void handleReservationCancelled(BaggageEvent event) {
        log.info("Message RabbitMQ reçu sur [{}] -> Réservation {} annulée. Mise à jour automatique des bagages associés...",
                RabbitMqConfig.QUEUE_RESERVATION_CANCELLED, event.getReservationId());

        if (event.getReservationId() != null) {
            List<Bagage> bagages = bagageRepository.findByReservationId(event.getReservationId());
            for (Bagage b : bagages) {
                b.setStatut("CANCELLED");
                bagageRepository.save(b);
                log.info("Bagage {} marqué comme CANCELLED suite à notification asynchrone.", b.getId());
            }
        }
    }
}
