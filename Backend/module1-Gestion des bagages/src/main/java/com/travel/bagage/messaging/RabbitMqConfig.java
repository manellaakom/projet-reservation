package com.travel.bagage.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE_NAME = "bagage.exchange";
    public static final String QUEUE_BAGAGE_CREATED = "bagage.created.queue";
    public static final String QUEUE_BAGAGE_STATUS = "bagage.status.queue";
    public static final String QUEUE_RESERVATION_CANCELLED = "reservation.cancelled.queue";

    public static final String ROUTING_KEY_CREATED = "bagage.created";
    public static final String ROUTING_KEY_STATUS = "bagage.status.updated";
    public static final String ROUTING_KEY_RES_CANCELLED = "reservation.cancelled";

    @Bean
    public TopicExchange bagageExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue bagageCreatedQueue() {
        return QueueBuilder.durable(QUEUE_BAGAGE_CREATED).build();
    }

    @Bean
    public Queue bagageStatusQueue() {
        return QueueBuilder.durable(QUEUE_BAGAGE_STATUS).build();
    }

    @Bean
    public Queue reservationCancelledQueue() {
        return QueueBuilder.durable(QUEUE_RESERVATION_CANCELLED).build();
    }

    @Bean
    public Binding bindingCreated(Queue bagageCreatedQueue, TopicExchange bagageExchange) {
        return BindingBuilder.bind(bagageCreatedQueue).to(bagageExchange).with(ROUTING_KEY_CREATED);
    }

    @Bean
    public Binding bindingStatus(Queue bagageStatusQueue, TopicExchange bagageExchange) {
        return BindingBuilder.bind(bagageStatusQueue).to(bagageExchange).with("bagage.status.*");
    }

    @Bean
    public Binding bindingResCancelled(Queue reservationCancelledQueue, TopicExchange bagageExchange) {
        return BindingBuilder.bind(reservationCancelledQueue).to(bagageExchange).with(ROUTING_KEY_RES_CANCELLED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
