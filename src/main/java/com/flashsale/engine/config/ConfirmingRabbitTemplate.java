package com.flashsale.engine.config;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.flashsale.engine.dto.OrderRequest;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/** The order API only accepts a reservation after a routed broker acknowledgement. */
public class ConfirmingRabbitTemplate extends RabbitTemplate {
    private static final Logger log = LoggerFactory.getLogger(ConfirmingRabbitTemplate.class);
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
        try {
            super.convertAndSend(exchange, routingKey, object, correlation);
        } catch (AmqpException e) {
            failure(object, "SEND_EXCEPTION", e.toString());
            throw e;
        }
        try {
            CorrelationData.Confirm confirm = correlation.getFuture()
                    .get(confirmTimeoutMillis, TimeUnit.MILLISECONDS);
            // Spring sets the correlated return before completing the confirm future.
            if (correlation.getReturned() != null) {
                failure(object, "RETURN", correlation.getReturned().getReplyText());
                throw new AmqpException("Broker returned publish: " + correlation.getReturned().getReplyText());
            }
            if (!confirm.isAck()) {
                failure(object, "NACK", confirm.getReason());
                throw new AmqpException("Broker rejected publish: " + confirm.getReason());
            }
        } catch (InterruptedException e) {
            failure(object, "INTERRUPTED", e.toString());
            Thread.currentThread().interrupt();
            throw new AmqpException("Publisher confirm interrupted", e);
        } catch (TimeoutException e) {
            failure(object, "TIMEOUT", e.toString());
            throw new AmqpException("Publisher confirm timed out", e);
        } catch (ExecutionException e) {
            failure(object, "CONFIRM_ERROR", e.toString());
            throw new AmqpException("Publisher confirm failed", e);
        }
    }
    private void failure(Object object, String type, String reason) {
        if (object instanceof OrderRequest request) {
            log.warn("ORDER_TRACE event=CONFIRM_FAILURE type={} key={} orderId={} epochMs={} reason={}",
                    type, request.getIdempotencyKey(), request.getOrderId(),
                    System.currentTimeMillis(), reason);
        }
    }

}
