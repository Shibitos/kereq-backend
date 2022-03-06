package com.kereq.common.config;

import com.kereq.common.constant.ExchangeName;
import com.kereq.common.constant.QueueName;
import com.kereq.main.exception.ApplicationException;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig implements RabbitListenerConfigurer {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry() {
        return new RabbitListenerEndpointRegistry();
    }

    @Override
    public void configureRabbitListeners(final RabbitListenerEndpointRegistrar registrar) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setPrefetchCount(1);
        factory.setConsecutiveActiveTrigger(1);
        factory.setConsecutiveIdleTrigger(1);
        factory.setConnectionFactory(connectionFactory);

        RetryInterceptorBuilder<RetryInterceptorBuilder.StatelessRetryInterceptorBuilder, RetryOperationsInterceptor> builder = RetryInterceptorBuilder.stateless();
        Map<Class<? extends Throwable>, Boolean> retryableExceptions = new HashMap<>();
        retryableExceptions.put(AmqpRejectAndDontRequeueException.class, false);
        retryableExceptions.put(ApplicationException.class, true);
        SimpleRetryPolicy policy = new SimpleRetryPolicy(1, retryableExceptions, true); //TODO: DLQ?
        builder.retryPolicy(policy).recoverer(new RejectAndDontRequeueRecoverer());
        factory.setAdviceChain(builder.build());

        registrar.setContainerFactory(factory);
        registrar.setEndpointRegistry(rabbitListenerEndpointRegistry());
    }

    @Bean
    Queue messagesQueue() {
        return new Queue(QueueName.MESSAGES, true);
    }

    @Bean
    FanoutExchange messagesExchange() {
        return new FanoutExchange(ExchangeName.MESSAGES);
    }

    @Bean
    Binding messagesBinding(Queue messagesQueue, FanoutExchange messagesExchange) {
        return BindingBuilder.bind(messagesQueue).to(messagesExchange);
    }

    @Bean
    Queue connectionsQueue() {
        return new Queue(QueueName.CONNECTIONS_BACKEND, false);
    }

    @Bean
    FanoutExchange connectionsExchange() {
        return new FanoutExchange(ExchangeName.CONNECTIONS_BACKEND);
    }

    @Bean
    Binding connectionsBinding(Queue connectionsQueue, FanoutExchange connectionsExchange) {
        return BindingBuilder.bind(connectionsQueue).to(connectionsExchange);
    }
}
