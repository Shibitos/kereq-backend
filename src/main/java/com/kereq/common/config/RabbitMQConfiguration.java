package com.kereq.common.config;

import com.kereq.common.constant.Queue;
import com.kereq.main.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RabbitMQConfiguration implements RabbitListenerConfigurer, ApplicationListener<ApplicationStartedEvent> {

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private AmqpAdmin admin;

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public RabbitAdmin rabbitAdmin() {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry(){
        return new RabbitListenerEndpointRegistry();
    }

    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(consumerJackson2MessageConverter());
        return factory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
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
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent applicationStartedEvent) {
        registerQueues();
    }

    private void registerQueues() {
        log.info("Registering queues");
        for (Queue queueName : Queue.values()) {
            log.info("Registering queue name: {}", queueName.getName());
            org.springframework.amqp.core.Queue queue = new org.springframework.amqp.core.Queue(queueName.getName(), true, false, false);
            Binding binding = new Binding(queueName.getName(), Binding.DestinationType.QUEUE, queueName.getName(), queueName.getName(), null);
            Exchange exchange = ExchangeBuilder.topicExchange(queueName.getName()).build();
            admin.declareExchange(exchange);
            admin.declareQueue(queue);
            admin.declareBinding(binding);
        }
        log.info("All queues registered");
    }
}
