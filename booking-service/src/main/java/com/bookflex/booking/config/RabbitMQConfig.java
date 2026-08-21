package com.bookflex.booking.config;

import com.bookflex.common.constant.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration — declares exchanges, queues, and bindings
 * for the booking event system.
 */
@Configuration
public class RabbitMQConfig {

    @Bean
    public TopicExchange bookingExchange() {
        return new TopicExchange(RabbitMQConstants.BOOKING_EXCHANGE);
    }

    @Bean
    public Queue bookingConfirmedQueue() {
        return new Queue(RabbitMQConstants.BOOKING_CONFIRMED_QUEUE, true);
    }

    @Bean
    public Queue bookingCancelledQueue() {
        return new Queue(RabbitMQConstants.BOOKING_CANCELLED_QUEUE, true);
    }

    @Bean
    public Queue bookingCreatedQueue() {
        return new Queue(RabbitMQConstants.BOOKING_CREATED_QUEUE, true);
    }

    @Bean
    public Binding confirmedBinding() {
        return BindingBuilder.bind(bookingConfirmedQueue())
                .to(bookingExchange())
                .with(RabbitMQConstants.BOOKING_CONFIRMED_ROUTING_KEY);
    }

    @Bean
    public Binding cancelledBinding() {
        return BindingBuilder.bind(bookingCancelledQueue())
                .to(bookingExchange())
                .with(RabbitMQConstants.BOOKING_CANCELLED_ROUTING_KEY);
    }

    @Bean
    public Binding createdBinding() {
        return BindingBuilder.bind(bookingCreatedQueue())
                .to(bookingExchange())
                .with(RabbitMQConstants.BOOKING_CREATED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
