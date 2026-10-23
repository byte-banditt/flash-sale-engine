package com.flashsale.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.flashsale.engine.config.KafkaConfig;
import com.flashsale.engine.consumer.DLQConsumer;
import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.service.OrderAcceptanceService;
import com.flashsale.engine.service.OrderProcessingService;
import com.flashsale.engine.service.OutboxRelay;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(properties = "flashsale.outbox.scheduling-enabled=false")
class OrderFlowIntegrationTests {
    private static final int PRODUCT_ID = 101;

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);
    @Container
    static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("apache/kafka:3.8.0"));

    @DynamicPropertySource
    static void services(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
        registry.add("spring.kafka.consumer.group-id", () -> "flash-sale-test-" + System.nanoTime());
    }

    @Autowired OrderAcceptanceService acceptance;
    @Autowired OutboxRelay relay;
    @Autowired org.springframework.jdbc.core.JdbcTemplate db;
    @Autowired StringRedisTemplate redis;
    @Autowired KafkaTemplate<String, OrderRequest> publisher;
    @MockitoSpyBean OrderProcessingService processing;
    @MockitoSpyBean DLQConsumer dlqConsumer;

    @BeforeEach
    void clean() {
        reset(processing, dlqConsumer);
        db.execute("TRUNCATE orders CASCADE");
        redis.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    @Test
    void relayPublishesKeyedOrderAndConsumerConfirmsIt() throws Exception {
        acceptance.initialize(PRODUCT_ID, 1);
        assertEquals(202, acceptance.accept(new OrderRequest(null, (long) PRODUCT_ID, 1, "k1")).getStatusCode().value());
        relay.publishBatch();
        await("select count(*) from orders where status='CONFIRMED'", 1);
        assertEquals(0, db.queryForObject("select count(*) from outbox_events where status='PENDING'", Integer.class));
    }

    @Test
    void duplicateDeliveryRemainsIdempotent() throws Exception {
        acceptance.initialize(PRODUCT_ID, 1);
        acceptance.accept(new OrderRequest(null, (long) PRODUCT_ID, 1, "k2"));
        relay.publishBatch();
        await("select count(*) from orders where status='CONFIRMED'", 1);
        String orderId = db.queryForObject("select order_id from orders where idempotency_key='k2'", String.class);
        publisher.send(KafkaConfig.ORDERS_TOPIC, orderId,
                new OrderRequest(orderId, (long) PRODUCT_ID, 1, "k2")).get();
        await("select count(*) from orders where status='CONFIRMED'", 1);
        assertEquals(1, db.queryForObject("select count(*) from orders", Integer.class));
    }

    @Test
    void thirdFailureRecoversToDlqAndDlqDoesNotRethrow() throws Exception {
        acceptance.initialize(PRODUCT_ID, 1);
        acceptance.accept(new OrderRequest(null, (long) PRODUCT_ID, 1, "k3"));
        AtomicInteger attempts = new AtomicInteger();
        doAnswer(invocation -> {
            if (attempts.incrementAndGet() <= 3) throw new IllegalStateException("forced consumer failure");
            return invocation.callRealMethod();
        }).when(processing).processOrder(any(OrderRequest.class));
        relay.publishBatch();
        await("select count(*) from orders where status='CONFIRMED'", 1);
        verify(dlqConsumer, org.mockito.Mockito.timeout(Duration.ofSeconds(20).toMillis()))
                .handleFailedOrder(any(OrderRequest.class));
        assertTrue(attempts.get() >= 4);
    }

    private void await(String query, int expected) throws InterruptedException {
        long deadline = System.nanoTime() + Duration.ofSeconds(30).toNanos();
        while (System.nanoTime() < deadline) {
            if (db.queryForObject(query, Integer.class) == expected) return;
            Thread.sleep(100);
        }
        assertEquals(expected, db.queryForObject(query, Integer.class));
    }
}
