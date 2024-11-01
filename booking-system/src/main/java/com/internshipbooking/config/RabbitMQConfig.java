package com.internshipbooking.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String MONITORING_QUEUE_NAME = "bookingQueue";
    public static final String BOOKING_EXCHANGE_NAME = "bookingExchange";
    public static final String MONITORING_ROUTING_KEY = "monitoringRoutingKey";


    @Bean
    public Queue monitoringQueue() {
        return new Queue(MONITORING_QUEUE_NAME, true);
    }

    @Bean
    public CachingConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        connectionFactory.setPort(5672);
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(CachingConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(BOOKING_EXCHANGE_NAME);
    }


    @Bean
    public Binding monitoringBinding(Queue monitoringQueue, TopicExchange bookingExchange) {
        return BindingBuilder.bind(monitoringQueue).to(bookingExchange).with(MONITORING_ROUTING_KEY);
    }


}
