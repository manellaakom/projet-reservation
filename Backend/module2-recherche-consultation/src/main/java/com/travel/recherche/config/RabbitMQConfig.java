package com.travel.recherche.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ pour les communications asynchrones (Note 4 de la grille AWD)
 * - Supporte plus de 2 scénarios d'échanges d'événements
 */
@Configuration
public class RabbitMQConfig {

    // Noms des Exchanges
    public static final String DIRECT_EXCHANGE = "travel.direct.exchange";
    public static final String TOPIC_EXCHANGE = "travel.topic.exchange";

    // Noms des Queues
    public static final String QUEUE_RESERVATION = "recherche.reservation.queue";
    public static final String QUEUE_PAIEMENT = "recherche.paiement.queue";
    public static final String QUEUE_PROMOTIONS = "recherche.promotions.queue";

    // Noms des Routing Keys
    public static final String ROUTING_KEY_RESERVATION = "reservation.created.key";
    public static final String ROUTING_KEY_PAIEMENT = "paiement.success.key";
    public static final String ROUTING_KEY_PROMO_PATTERN = "voyage.promo.#";
    public static final String ROUTING_KEY_PROMO_FLASH = "voyage.promo.flash";

    // 1. Déclaration des Exchanges
    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(DIRECT_EXCHANGE);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

    // 2. Déclaration des Files (Queues)
    @Bean
    public Queue reservationQueue() {
        return QueueBuilder.durable(QUEUE_RESERVATION).build();
    }

    @Bean
    public Queue paiementQueue() {
        return QueueBuilder.durable(QUEUE_PAIEMENT).build();
    }

    @Bean
    public Queue promotionsQueue() {
        return QueueBuilder.durable(QUEUE_PROMOTIONS).build();
    }

    // 3. Bindings (Liaisons entre Exchanges, Routing Keys et Queues)
    @Bean
    public Binding reservationBinding(Queue reservationQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(reservationQueue).to(directExchange).with(ROUTING_KEY_RESERVATION);
    }

    @Bean
    public Binding paiementBinding(Queue paiementQueue, DirectExchange directExchange) {
        return BindingBuilder.bind(paiementQueue).to(directExchange).with(ROUTING_KEY_PAIEMENT);
    }

    @Bean
    public Binding promotionsBinding(Queue promotionsQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(promotionsQueue).to(topicExchange).with(ROUTING_KEY_PROMO_PATTERN);
    }

    // 4. Convertisseur JSON pour sérialisation automatique des objets
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
