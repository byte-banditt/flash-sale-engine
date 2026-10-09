package com.flashsale.engine.config;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/** The order API only accepts a reservation after a routed broker acknowledgement. */
public class ConfirmingRabbitTemplate extends RabbitTemplate {
    private final long confirmTimeoutMillis;

    public ConfirmingRabbitTemplate(ConnectionFactory connectionFactory, long confirmTimeoutMillis) {
        super(connectionFactory);
        if (confirmTimeoutMillis <= 0) {
            throw new IllegalArgumentException("Publisher confirm timeout must be positive");
        }
        this.confirmTimeoutMillis = confirmTimeoutMillis;
        setMandatory(true);
    }

    @Override
    public void convertAndSend(String exchange, String routingKey, Object object) throws AmqpException {
        CorrelationData correlation = new CorrelationData();
        super.convertAndSend(exchange, routingKey, object, correlation);
        try {
            CorrelationData.Confirm confirm = correlation.getFuture()
                    .get(confirmTimeoutMillis, TimeUnit.MILLISECONDS);
            // Spring sets the correlated return before completing the confirm future.
            if (correlation.getReturned() != null) {
                throw new AmqpException("Broker returned publish: " + correlation.getReturned().getReplyText());
            }
            if (!confirm.isAck()) {
                throw new AmqpException("Broker rejected publish: " + confirm.getReason());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AmqpException("Publisher confirm interrupted", e);
        } catch (TimeoutException e) {
            throw new AmqpException("Publisher confirm timed out", e);
        } catch (ExecutionException e) {
            throw new AmqpException("Publisher confirm failed", e);
        }
    }
}
