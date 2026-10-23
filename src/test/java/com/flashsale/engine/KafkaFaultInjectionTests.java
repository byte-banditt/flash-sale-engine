package com.flashsale.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
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
@SpringBootTest(properties = {
        "flashsale.outbox.scheduling-enabled=false",
        "flashsale.kafka.delivery-timeout-ms=3000"
})
class KafkaFaultInjectionTests {
    private static final int PRODUCT_ID = 101;
    private static final int INITIAL_STOCK = 10;

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
        registry.add("spring.kafka.consumer.group-id", () -> "fault-test-" + UUID.randomUUID());
    }

    @Autowired OrderAcceptanceService acceptance;
    @Autowired OutboxRelay relay;
    @Autowired KafkaTemplate<String, OrderRequest> publisher;
    @Autowired org.springframework.jdbc.core.JdbcTemplate db;
    @Autowired StringRedisTemplate redis;
    @Autowired KafkaListenerEndpointRegistry listeners;
    @MockitoSpyBean OrderProcessingService processing;
    @MockitoSpyBean DLQConsumer dlqConsumer;

    @BeforeEach
    void freshFixture() throws Exception {
        reset(processing, dlqConsumer);
        db.execute("TRUNCATE orders CASCADE");
        redis.getConnectionFactory().getConnection().serverCommands().flushDb();
        purgeKafka();
    }

    @RepeatedTest(10)
    void brokerRestartWithUnsentOutboxRowsDrainsExactlyOnce(RepetitionInfo repetition) throws Exception {
        acceptBatch("broker-restart", 10);
        KAFKA.stop();
        relay.publishBatch();
        assertEquals(10, pendingEvents());
        KAFKA.start();
        relay.publishBatch();
        awaitConfirmed(10);
        assertLedger(10, 10);
        report("broker-restart-unsent-outbox", repetition);
    }

    @RepeatedTest(10)
    void consumerStopBeforeOffsetCommitRedeliversWithoutDoubleEffect(RepetitionInfo repetition) throws Exception {
        String key = key("consumer-kill");
        accept(key, 1);
        AtomicBoolean stopped = new AtomicBoolean();
        doAnswer(invocation -> {
            if (stopped.compareAndSet(false, true)) {
                listeners.getListenerContainer("orders-consumer").stop();
            }
            return invocation.callRealMethod();
        }).when(processing).processOrder(any(OrderRequest.class));
        relay.publishBatch();
        await(() -> stopped.get());
        listeners.getListenerContainer("orders-consumer").start();
        awaitConfirmed(1);
        assertLedger(1);
        assertTrue(stopped.get());
        report("consumer-kill-before-offset-commit", repetition);
    }

    @RepeatedTest(10)
    void poisonMessageReachesDlqOnceAfterThreeAttempts(RepetitionInfo repetition) throws Exception {
        String key = key("poison");
        OrderRequest poison = new OrderRequest("missing-" + key, (long) PRODUCT_ID, 1, key);
        publisher.send(KafkaConfig.ORDERS_TOPIC, poison.getOrderId(), poison).get();
        verify(dlqConsumer, org.mockito.Mockito.timeout(Duration.ofSeconds(20).toMillis()).times(1))
                .handleFailedOrder(any(OrderRequest.class));
        verify(processing, org.mockito.Mockito.timeout(Duration.ofSeconds(1).toMillis()).times(4))
                .processOrder(any(OrderRequest.class));
        assertEquals(0, db.queryForObject("select count(*) from orders", Integer.class));
        assertEquals(INITIAL_STOCK, stock());
        report("poison-to-dlq", repetition);
    }

    @RepeatedTest(10)
    void duplicateOrderIdHasOneTerminalEffect(RepetitionInfo repetition) throws Exception {
        String key = key("duplicate");
        accept(key, 1);
        String orderId = db.queryForObject("select order_id from orders where idempotency_key=?", String.class, key);
        OrderRequest request = new OrderRequest(orderId, (long) PRODUCT_ID, 1, key);
        publisher.send(KafkaConfig.ORDERS_TOPIC, orderId, request).get();
        publisher.send(KafkaConfig.ORDERS_TOPIC, orderId, request).get();
        awaitConfirmed(1);
        assertLedger(1);
        assertEquals(1, db.queryForObject("select count(*) from orders", Integer.class));
        report("duplicate-order-id", repetition);
    }

    private void accept(String key, int quantity) {
        acceptance.initialize(PRODUCT_ID, INITIAL_STOCK);
        assertEquals(202, acceptance.accept(new OrderRequest(null, (long) PRODUCT_ID, quantity, key))
                .getStatusCode().value());
    }

    private void acceptBatch(String scenario, int count) {
        acceptance.initialize(PRODUCT_ID, INITIAL_STOCK);
        for (int i = 0; i < count; i++) {
            assertEquals(202, acceptance.accept(new OrderRequest(null, (long) PRODUCT_ID, 1,
                    key(scenario + "-" + i))).getStatusCode().value());
        }
    }

    private String key(String scenario) {
        return scenario + "-" + UUID.randomUUID();
    }

    private int stock() {
        return db.queryForObject("select available_stock from products where id=?", Integer.class, PRODUCT_ID);
    }

    private int pendingEvents() {
        return db.queryForObject("select count(*) from outbox_events where status='PENDING'", Integer.class);
    }

    private void purgeKafka() throws Exception {
        Map<String, Object> properties = new HashMap<>();
        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA.getBootstrapServers());
        try (AdminClient admin = AdminClient.create(properties)) {
            Map<TopicPartition, RecordsToDelete> records = new HashMap<>();
            for (int partition = 0; partition < 3; partition++) {
                records.put(new TopicPartition(KafkaConfig.ORDERS_TOPIC, partition),
                        RecordsToDelete.beforeOffset(Long.MAX_VALUE));
            }
            records.put(new TopicPartition(KafkaConfig.DLQ_TOPIC, 0),
                    RecordsToDelete.beforeOffset(Long.MAX_VALUE));
            admin.deleteRecords(records).all().get();
        }
    }

    private void awaitConfirmed(int expected) throws InterruptedException {
        await(() -> db.queryForObject("select count(*) from orders where status='CONFIRMED'", Integer.class) == expected);
    }

    private void assertLedger(int quantity) {
        assertLedger(quantity, 1);
    }

    private void assertLedger(int quantity, int orderCount) {
        assertEquals(INITIAL_STOCK, stock() + quantity);
        assertEquals(0, pendingEvents());
        assertEquals(orderCount, db.queryForObject("select count(*) from orders where status in ('CONFIRMED','CANCELLED')", Integer.class));
    }

    private void report(String scenario, RepetitionInfo repetition) {
        System.out.printf("KAFKA_FAULT scenario=%s run=%d/%d result=PASS%n",
                scenario, repetition.getCurrentRepetition(), repetition.getTotalRepetitions());
    }

    private void await(CheckedCondition condition) throws InterruptedException {
        long deadline = System.nanoTime() + Duration.ofSeconds(30).toNanos();
        while (System.nanoTime() < deadline) {
            if (condition.ready()) return;
            Thread.sleep(100);
        }
        assertTrue(condition.ready(), "Timed out waiting for Kafka fault condition");
    }

    @FunctionalInterface
    private interface CheckedCondition {
        boolean ready() throws InterruptedException;
    }
}
