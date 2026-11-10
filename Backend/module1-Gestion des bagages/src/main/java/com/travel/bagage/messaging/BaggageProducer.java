package com.travel.bagage.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BaggageProducer {

    private static final Logger log = LoggerFactory.getLogger(BaggageProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public BaggageProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Scénario RabbitMQ 1 : Publication d'un événement lors de la création d'un bagage
     */
    public void sendBaggageCreatedEvent(String bagageId, String reservationId, Double poids, Double prix, String statut) {
        BaggageEvent event = BaggageEvent.builder()
                .eventType("BAGAGE_CREATED")
                .bagageId(bagageId)
                .reservationId(reservationId)
                .poids(poids)
                .prix(prix)
                .statut(statut)
                .details("Nouveau bagage enregistré avec succès, étiquette générée")
                .timestamp(LocalDateTime.now())
                .build();

        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, RabbitMqConfig.ROUTING_KEY_CREATED, event);
            log.info("Événement RabbitMQ publié [bagage.created] : {}", event);
        } catch (Exception e) {
            log.warn("Impossible d'envoyer l'événement RabbitMQ (broker indisponible): {}", e.getMessage());
        }
    }

    /**
     * Scénario RabbitMQ 2 : Publication d'un événement lors du changement d'état d'un bagage
     */
    public void sendBaggageStatusUpdatedEvent(String bagageId, String reservationId, String newStatus, String details) {
        BaggageEvent event = BaggageEvent.builder()
                .eventType("BAGAGE_UPDATED")
                .bagageId(bagageId)
                .reservationId(reservationId)
                .statut(newStatus)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        try {
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, RabbitMqConfig.ROUTING_KEY_STATUS, event);
            log.info("Événement RabbitMQ publié [bagage.status.updated] : {}", event);
        } catch (Exception e) {
            log.warn("Impossible d'envoyer l'événement RabbitMQ (broker indisponible): {}", e.getMessage());
        }
    }
}
