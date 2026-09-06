package com.flashsale.engine.config;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
// import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptor;
// import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptorFactoryBean;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
// import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// import org.springframework.core.retry.RetryPolicy;
// import org.springframework.core.retry.RetryTemplate;
// import org.springframework.retry.support.RetryTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptorFactoryBean;
import org.springframework.amqp.rabbit.config.StatelessRetryOperationsInterceptor;
import org.springframework.core.retry.RetryPolicy;
import java.time.Duration;
// import ch.qos.logback.classic.pattern.MessageConverter;

// import org.springframework.retry.backoff.ExponentialBackOffPolicy;
// import org.springframework.retry.interceptor.RetryOperationsInterceptor;
// import org.springframework.retry.policy.SimpleRetryPolicy;
// import org.springframework.retry.interceptor.RetryInterceptorBuilder;

@Configuration 
public class RabbitMQConfig {
    public static final String ORDER_EXCHANGE = "order.exchange";
    public static final String ORDER_QUEUE = "order.queue";
    public static final String ORDER_ROUTING_KEY = "order.created";

    public static final String DLX_EXCHANGE = "order.dlx";
    public static final String DLQ_QUEUE = "order.dlq";
    public static final String DLQ_ROUTING_KEY = "order.failed";

    @Bean 
    public DirectExchange orderExchange(){
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean 
    public DirectExchange deadLetterExchange(){
        return new DirectExchange(DLX_EXCHANGE);
    }

    @Bean 
    public Queue deadLetterQueue(){
        return  QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean 
    public Binding deadLetterBinding(){
        return BindingBuilder.bind(deadLetterQueue())
        .to(deadLetterExchange())
        .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public Queue orderQueue(){
        return QueueBuilder.durable(ORDER_QUEUE)
                .withArgument("x-dead-letter-exchange", DLX_EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)
                .build();
    }

    @Bean 
    public Binding orderBinding(){
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(ORDER_ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter jackson2JsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    // @Bean
    // public RetryOperationsInterceptor retryInterceptor(){
    //     RetryTemplate retryTemplate = new RetryTemplate();

    //     retryTemplate.setRetryPolicy( new SimpleRetryPolicy());

    //     retryTemplate.setBackOffPolicy(new ExponentialBackOffPolicy());

    //      return org.springframework.retry.interceptor.RetryInterceptorBuilder
    //             .stateless()
    //             .retryOperations(retryTemplate)
    //             .recoverer(new RepublishMessageRecoverer(
    //                     rabbitTemplateForRecovery(),
    //                     DLX_EXCHANGE,
    //                     DLQ_ROUTING_KEY))
    //             .build();
    // }

    @Bean
    public StatelessRetryOperationsInterceptor retryInterceptor(
            ConnectionFactory connectionFactory,
            MessageConverter converter) {

        RetryPolicy retryPolicy = RetryPolicy.builder()
                .maxRetries(2)                       // 1 initial + 2 retries = 3 total attempts
                .delay(Duration.ofSeconds(2))
                .multiplier(2.0)
                .build();

        StatelessRetryOperationsInterceptorFactoryBean factoryBean =
                new StatelessRetryOperationsInterceptorFactoryBean();
        factoryBean.setRetryPolicy(retryPolicy);
        factoryBean.setMessageRecoverer(new RepublishMessageRecoverer(
                rabbitTemplateForRecovery(connectionFactory, converter),
                DLX_EXCHANGE, DLQ_ROUTING_KEY));

        return factoryBean.getObject();
    }
    
    @Bean
    public RabbitTemplate rabbitTemplateForRecovery( ConnectionFactory connectionFactory,MessageConverter messageConverter){
        // inject ConnectionFactory (constructor or @Autowired) and pass it here —
        // left out so you wire it the way the rest of the class expects.
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter( messageConverter);
        // throw new UnsupportedOperationException("wire ConnectionFactory here");
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        factory.setAdviceChain(retryInterceptor(connectionFactory, converter));
        return factory;
    }
}
