package com.travel.recherche.event;

import com.travel.recherche.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Producteur RabbitMQ : Publie des messages dans le broker pour communication asynchrone
 */
@Component
public class TravelEventProducer {

    private static final Logger log = LoggerFactory.getLogger(TravelEventProducer.class);

    private final RabbitTemplate rabbitTemplate;

    public TravelEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publie un événement de réservation dans l'échange Direct
     */
    public void envoyerReservationEvent(ReservationEvent event) {
        if (event.getDateCreation() == null) {
            event.setDateCreation(LocalDateTime.now());
        }
        log.info(" [RabbitMQ] Publication ReservationEvent vers '{}' avec clé '{}' : {}",
                RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.ROUTING_KEY_RESERVATION, event);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DIRECT_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_RESERVATION,
                event
        );
    }

    /**
     * Publie un événement de paiement dans l'échange Direct
     */
    public void envoyerPaiementEvent(PaiementEvent event) {
        if (event.getDatePaiement() == null) {
            event.setDatePaiement(LocalDateTime.now());
        }
        log.info(" [RabbitMQ] Publication PaiementEvent vers '{}' avec clé '{}' : {}",
                RabbitMQConfig.DIRECT_EXCHANGE, RabbitMQConfig.ROUTING_KEY_PAIEMENT, event);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.DIRECT_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_PAIEMENT,
                event
        );
    }

    /**
     * Publie un événement de promotion flash dans l'échange Topic
     */
    public void envoyerPromotionEvent(PromotionEvent event) {
        log.info(" [RabbitMQ] Publication PromotionEvent vers '{}' avec clé '{}' : {}",
                RabbitMQConfig.TOPIC_EXCHANGE, RabbitMQConfig.ROUTING_KEY_PROMO_FLASH, event);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TOPIC_EXCHANGE,
                RabbitMQConfig.ROUTING_KEY_PROMO_FLASH,
                event
        );
    }
}
