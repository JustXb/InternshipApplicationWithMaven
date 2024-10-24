package com.example.intershipapplicationwithmaven.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String MONITORING_QUEUE_NAME = "bookingQueue";
    public static final String BOOKING_EXCHANGE_NAME = "bookingExchange";
    public static final String MONITORING_ROUTING_KEY = "monitoringRoutingKey";


    // Создание очереди для сервиса мониторинга
    @Bean
    public Queue monitoringQueue() {
        return new Queue(MONITORING_QUEUE_NAME, true);
    }

    // Создание обмена (Exchange)
    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE_NAME);
    }


    // Привязка очереди для сервиса мониторинга к обмену
    @Bean
    public Binding monitoringBinding(Queue monitoringQueue, TopicExchange bookingExchange) {
        return BindingBuilder.bind(monitoringQueue).to(bookingExchange).with(MONITORING_ROUTING_KEY);
    }


}
