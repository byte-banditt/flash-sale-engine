package com.flashsale.engine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BooleanSupplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.flashsale.engine.config.RabbitMQConfig;
import com.flashsale.engine.consumer.DLQConsumer;
import com.flashsale.engine.dto.OrderRequest;
import com.flashsale.engine.repository.OrderRepository;
import com.flashsale.engine.service.OrderProcessingService;

@Testcontainers
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "flashsale.outbox.scheduling-enabled=false")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderFlowIntegrationTests {
    private static final int PRODUCT_ID = 101;
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig()
                    .withMemory(384L * 1024 * 1024).withMemorySwap(384L * 1024 * 1024));
    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig()
                    .withMemory(384L * 1024 * 1024).withMemorySwap(384L * 1024 * 1024));
    @Container
    static final RabbitMQContainer RABBIT = new RabbitMQContainer("rabbitmq:3.13-management-alpine")
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig()
                    .withMemory(384L * 1024 * 1024).withMemorySwap(384L * 1024 * 1024));

    @DynamicPropertySource
    static void services(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.rabbitmq.host", RABBIT::getHost);
        registry.add("spring.rabbitmq.port", RABBIT::getAmqpPort);
        registry.add("spring.rabbitmq.username", RABBIT::getAdminUsername);
        registry.add("spring.rabbitmq.password", RABBIT::getAdminPassword);
    }

    @Value("${local.server.port}")
    int port;
    @Autowired
    StringRedisTemplate redis;
    @Autowired
    OrderRepository orders;
    @Autowired
    com.flashsale.engine.service.OrderAcceptanceService acceptance;
    @Autowired
    com.flashsale.engine.service.OutboxRelay relay;
    @Autowired
    com.flashsale.engine.service.OrderReconciliation reconciliation;
    @Autowired
    org.springframework.jdbc.core.JdbcTemplate db;
    @Autowired
    RabbitAdmin rabbitAdmin;
    @MockitoSpyBean(name = "rabbitTemplateForRecovery")
    RabbitTemplate publisher;
    @MockitoSpyBean
    OrderProcessingService processing;
    @MockitoSpyBean
    DLQConsumer dlqConsumer;

    @BeforeEach
    void clean() {
        reset(publisher, processing, dlqConsumer);
        rabbitAdmin.purgeQueue(RabbitMQConfig.ORDER_QUEUE, false);
        rabbitAdmin.purgeQueue(RabbitMQConfig.DLQ_QUEUE, false);
        db.execute("TRUNCATE orders CASCADE");
        redis.getConnectionFactory().getConnection().serverCommands().flushDb();
    }

    private HttpResponse<String> post(String key, int quantity) throws Exception {
        String body = "{\"productId\":" + PRODUCT_ID + ",\"quantity\":" + quantity
                + ",\"idempotencyKey\":\"" + key + "\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/orders"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build();
        return HTTP.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private void stock(int value) {
        acceptance.initialize(PRODUCT_ID, value);
    }

    private int stock() {
        return db.queryForObject("select available_stock from products where id=?", Integer.class, PRODUCT_ID);
    }

    @Test
    @Order(0)
    void actuatorExposesOnlyHealthAndMetrics() throws Exception {
        HttpResponse<String> health = HTTP.send(HttpRequest.newBuilder(
                URI.create("http://localhost:" + port + "/actuator/health")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertEquals(200, health.statusCode());
        assertTrue(health.body().contains("\"status\":\"UP\""));
        assertEquals(200, HTTP.send(HttpRequest.newBuilder(
                URI.create("http://localhost:" + port + "/actuator/metrics")).GET().build(),
                HttpResponse.BodyHandlers.ofString()).statusCode());
        assertEquals(404, HTTP.send(HttpRequest.newBuilder(
                URI.create("http://localhost:" + port + "/actuator/env")).GET().build(),
                HttpResponse.BodyHandlers.ofString()).statusCode());
    }

    private void await(BooleanSupplier condition) throws InterruptedException {
        long deadline = System.nanoTime() + Duration.ofMinutes(2).toNanos();
        while (System.nanoTime() < deadline) {
            if (condition.getAsBoolean()) return;
            Thread.sleep(100);
        }
        fail("Timed out waiting for asynchronous order processing");
    }

    @Test
    @Order(1)
    void invalidInputReturns400() throws Exception {
        stock(1);
        assertEquals(400, post("", 1).statusCode());
        assertTrue(post("", 1).body().contains("idempotencyKey"));
        assertEquals(400, post("valid", 0).statusCode());
        assertEquals(400, post("valid", -1).statusCode());
        String missingProduct = "{\"quantity\":1,\"idempotencyKey\":\"valid\"}";
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/orders"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(missingProduct)).build();
        assertEquals(400, HTTP.send(request, HttpResponse.BodyHandlers.ofString()).statusCode());
        assertEquals(1, stock());
    }

    @Test
    @Order(2)
    void concurrentRequestsCannotOversell() throws Exception {
        int m = 100;
        int n = 2000;
        stock(m);
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            String key = "oversell-" + i;
            tasks.add(() -> post(key, 1).statusCode());
        }
        try (var pool = Executors.newFixedThreadPool(64)) {
            List<Future<Integer>> results = pool.invokeAll(tasks, 2, TimeUnit.MINUTES);
            long accepted = 0;
            for (Future<Integer> result : results) {
                int status = result.get();
                assertTrue(status == 202 || status == 409, "Unexpected HTTP status: " + status);
                if (status == 202) accepted++;
            }
            assertEquals(m, accepted);
        }
        assertEquals(0, stock());
        relay.publishBatch();
        relay.publishBatch();
        relay.publishBatch();
        relay.publishBatch();
        await(() -> confirmed() == m);
        assertEquals(m, orders.findAll().stream().filter(o -> "CONFIRMED".equals(o.getStatus())).count());
    }

    @Test
    @Order(3)
    void concurrentSameKeyReservesOnce() throws Exception {
        stock(10);
        int k = 40;
        String key = "same-" + UUID.randomUUID();
        List<Callable<Integer>> tasks = new ArrayList<>();
        for (int i = 0; i < k; i++) tasks.add(() -> post(key, 1).statusCode());
        try (var pool = Executors.newFixedThreadPool(k)) {
            List<Future<Integer>> results = pool.invokeAll(tasks, 1, TimeUnit.MINUTES);
            long accepted = 0;
            for (Future<Integer> result : results) {
                int status = result.get();
                assertTrue(status == 202 || status == 200, "Unexpected HTTP status: " + status);
                if (status == 202) accepted++;
            }
            assertEquals(1, accepted);
        }
        relay.publishBatch();
        await(() -> confirmed() == 1);
        assertEquals(1, orders.count());
        assertEquals(9, stock());
    }

    private long confirmed() {
        return db.queryForObject("select count(*) from orders where status='CONFIRMED'", Long.class);
    }
    private String status(String key) { return orders.findByIdempotencyKey(key).orElseThrow().getStatus(); }
    private long pendingEvents() {
        return db.queryForObject("select count(*) from outbox_events where status='PENDING'", Long.class);
    }
    private OrderRequest intent(String key) {
        var o = orders.findByIdempotencyKey(key).orElseThrow();
        return new OrderRequest(o.getOrderId(), o.getProductId(), o.getQuantity(), key);
    }
    private void expire() { db.update("update orders set created_at=current_timestamp - interval '1 day' where status='PENDING'"); }
    private RabbitTemplate independentPublisher() {
        RabbitTemplate direct = new RabbitTemplate(publisher.getConnectionFactory());
        direct.setMessageConverter(publisher.getMessageConverter());
        return direct;
    }
    private void assertCancelledAndRefunded(String key) {
        expire();
        assertEquals(1, reconciliation.sweep());
        assertEquals("CANCELLED", status(key));
        assertEquals(1, stock());
        assertEquals(0, confirmed());
        assertEquals(0, pendingEvents());
        assertEquals(0, reconciliation.sweep());
        assertEquals(1, stock());
    }

    @Test @Order(4)
    void exhaustedRetriesReachDlqAndRollbackOnlyOnce() throws Exception {
        stock(1);
        String key = "dlq-" + UUID.randomUUID();
        AtomicInteger attempts = new AtomicInteger();
        doAnswer(invocation -> {
            if (attempts.incrementAndGet() <= 3) throw new IllegalStateException("forced consumer failure");
            return invocation.callRealMethod();
        }).when(processing).processOrder(any(OrderRequest.class));
        assertEquals(202, post(key, 1).statusCode());
        relay.publishBatch();
        await(() -> confirmed() == 1);
        verify(dlqConsumer, timeout(10000)).handleFailedOrder(any(OrderRequest.class));
        OrderRequest request = intent(key);
        independentPublisher().convertAndSend(RabbitMQConfig.DLX_EXCHANGE, RabbitMQConfig.DLQ_ROUTING_KEY, request);
        independentPublisher().convertAndSend(RabbitMQConfig.DLX_EXCHANGE, RabbitMQConfig.DLQ_ROUTING_KEY, request);
        verify(dlqConsumer, timeout(10000).times(3)).handleFailedOrder(any(OrderRequest.class));
        assertEquals(1, confirmed());
        assertEquals(0, stock());
        assertEquals(0, reconciliation.sweep());
    }

    @Test @Order(5)
    void publishFailureRestoresStock() throws Exception {
        stock(1); String key = "publish-" + UUID.randomUUID();
        doThrow(new AmqpException("forced publish failure")).when(publisher).convertAndSend(
                eq(RabbitMQConfig.ORDER_EXCHANGE), eq(RabbitMQConfig.ORDER_ROUTING_KEY), any(OrderRequest.class));
        assertEquals(202, post(key, 1).statusCode());
        relay.publishBatch();
        assertEquals("PENDING", status(key)); assertEquals(0, stock()); assertEquals(1, pendingEvents());
        assertCancelledAndRefunded(key);
    }

    @Test @Order(6)
    void brokerNackRestoresStockAndReleasesKey() throws Exception {
        stock(1); String key = "nack-" + UUID.randomUUID();
        doAnswer(invocation -> {
            CorrelationData correlation = invocation.getArgument(3);
            correlation.getFuture().complete(new CorrelationData.Confirm(false, "forced nack")); return null;
        }).when(publisher).convertAndSend(eq(RabbitMQConfig.ORDER_EXCHANGE),
                eq(RabbitMQConfig.ORDER_ROUTING_KEY), any(OrderRequest.class), any(CorrelationData.class));
        assertEquals(202, post(key, 1).statusCode()); relay.publishBatch();
        assertEquals(0, stock()); assertEquals(1, pendingEvents());
        assertCancelledAndRefunded(key);
        assertEquals(200, post(key, 1).statusCode()); assertEquals(1, stock());
    }

    @Test @Order(7)
    void actualUnroutableReturnRestoresStock() throws Exception {
        stock(1); String key = "return-" + UUID.randomUUID();
        Binding binding = new Binding(RabbitMQConfig.ORDER_QUEUE, Binding.DestinationType.QUEUE,
                RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, null);
        rabbitAdmin.removeBinding(binding);
        try {
            assertEquals(202, post(key, 1).statusCode()); relay.publishBatch();
            assertEquals(0, stock()); assertEquals(1, pendingEvents());
            assertCancelledAndRefunded(key);
        } finally { rabbitAdmin.declareBinding(binding); }
    }

    @Test @Order(8)
    void confirmTimeoutRestoresStock() throws Exception {
        stock(1); String key = "timeout-" + UUID.randomUUID();
        doNothing().when(publisher).convertAndSend(eq(RabbitMQConfig.ORDER_EXCHANGE),
                eq(RabbitMQConfig.ORDER_ROUTING_KEY), any(OrderRequest.class), any(CorrelationData.class));
        assertEquals(202, post(key, 1).statusCode()); relay.publishBatch();
        assertEquals(0, stock()); assertEquals(1, pendingEvents());
        assertCancelledAndRefunded(key);
    }

    @Test @Order(9)
    void lostConfirmAfterDeliveryKeepsConsumerOwnedReservation() throws Exception {
        stock(1); String key = "lost-confirm-consumer-" + UUID.randomUUID();
        RabbitTemplate direct = independentPublisher();
        doAnswer(invocation -> {
            direct.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, (OrderRequest) invocation.getArgument(2));
            await(() -> confirmed() == 1); return null;
        }).when(publisher).convertAndSend(eq(RabbitMQConfig.ORDER_EXCHANGE),
                eq(RabbitMQConfig.ORDER_ROUTING_KEY), any(OrderRequest.class), any(CorrelationData.class));
        assertEquals(202, post(key, 1).statusCode()); relay.publishBatch();
        assertEquals("CONFIRMED", status(key)); assertEquals(0, stock());
        expire(); assertEquals(0, reconciliation.sweep());
        reset(publisher); relay.publishBatch();
        assertEquals(0, pendingEvents()); assertEquals(1, confirmed()); assertEquals(0, stock());
    }

    @Test @Order(10)
    void lostConfirmRefundWinsAndLaterDeliveryCannotPersist() throws Exception {
        stock(1); String key = "lost-confirm-cancel-" + UUID.randomUUID();
        CountDownLatch entered = new CountDownLatch(1), release = new CountDownLatch(1);
        AtomicInteger completed = new AtomicInteger();
        doAnswer(invocation -> {
            entered.countDown(); assertTrue(release.await(20, TimeUnit.SECONDS));
            Object result = invocation.callRealMethod(); completed.incrementAndGet(); return result;
        }).when(processing).processOrder(any(OrderRequest.class));
        RabbitTemplate direct = independentPublisher();
        doAnswer(invocation -> {
            direct.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, (OrderRequest) invocation.getArgument(2));
            assertTrue(entered.await(10, TimeUnit.SECONDS)); return null;
        }).when(publisher).convertAndSend(eq(RabbitMQConfig.ORDER_EXCHANGE),
                eq(RabbitMQConfig.ORDER_ROUTING_KEY), any(OrderRequest.class), any(CorrelationData.class));
        try {
            assertEquals(202, post(key, 1).statusCode()); relay.publishBatch();
            assertCancelledAndRefunded(key);
        } finally { release.countDown(); }
        await(() -> completed.get() == 1);
        assertEquals("CANCELLED", status(key)); assertEquals(0, confirmed()); assertEquals(1, stock());
    }

    @Test @Order(11)
    void nackWithoutDeliveryRestoresStockAndOldDeliveryCannotUndoRetry() throws Exception {
        stock(1); String key = "cancel-retry-" + UUID.randomUUID();
        assertEquals(202, post(key, 1).statusCode()); OrderRequest old = intent(key);
        assertCancelledAndRefunded(key);
        assertEquals(200, post(key, 1).statusCode());
        independentPublisher().convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, old);
        verify(processing, timeout(10000)).processOrder(any(OrderRequest.class));
        assertEquals(1, orders.count()); assertEquals(0, confirmed()); assertEquals(1, stock());
        assertEquals("CANCELLED", status(key));
    }

    @Test @Order(12)
    void claimSurvivesCrashAndRedeliveryPersistsExactlyOnce() throws Exception {
        stock(1); String key = "redelivery-" + UUID.randomUUID();
        assertEquals(202, post(key, 1).statusCode()); OrderRequest request = intent(key);
        assertEquals("PENDING", status(key)); assertEquals(0, stock());
        RabbitTemplate direct = independentPublisher();
        direct.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, request);
        await(() -> confirmed() == 1);
        direct.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, request);
        verify(processing, timeout(10000).times(2)).processOrder(any(OrderRequest.class));
        assertEquals(1, orders.count()); assertEquals(1, confirmed()); assertEquals(0, stock());
        assertEquals(0, reconciliation.sweep());
    }

    @Test @Order(13)
    void deadLetterOfClaimedOrderResumesPersistenceInsteadOfOrphaningReservation() throws Exception {
        stock(1); String key = "durable-dlq-" + UUID.randomUUID();
        assertEquals(202, post(key, 1).statusCode());
        independentPublisher().convertAndSend(RabbitMQConfig.DLX_EXCHANGE, RabbitMQConfig.DLQ_ROUTING_KEY, intent(key));
        await(() -> confirmed() == 1);
        assertEquals(1, orders.count()); assertEquals(0, stock()); assertEquals(0, reconciliation.sweep());
    }

    @Test @Order(14)
    void outboxInsertFailureRollsBackOrderAndInventoryTogether() throws Exception {
        stock(1);
        db.execute("create function fail_outbox_test() returns trigger language plpgsql as $$ begin raise exception 'forced outbox insert failure'; end $$");
        db.execute("create trigger fail_outbox_test before insert on outbox_events for each row execute function fail_outbox_test()");
        try {
            assertEquals(500, post("insert-failure-" + UUID.randomUUID(), 1).statusCode());
            assertEquals(1, stock()); assertEquals(0, orders.count()); assertEquals(0, pendingEvents());
        } finally {
            db.execute("drop trigger fail_outbox_test on outbox_events"); db.execute("drop function fail_outbox_test()");
        }
    }

    @Test @Order(16)
    void cancellationFailureRollsBackRefundAndTerminalStatus() throws Exception {
        stock(1); String key = "cancel-failure-" + UUID.randomUUID();
        assertEquals(202, post(key, 1).statusCode()); expire();
        db.execute("create function fail_cancel_test() returns trigger language plpgsql as $$ begin raise exception 'forced cancellation write failure'; end $$");
        db.execute("create trigger fail_cancel_test before update on outbox_events for each row execute function fail_cancel_test()");
        try {
            assertThrows(org.springframework.dao.DataAccessException.class, () -> reconciliation.sweep());
            assertEquals("PENDING", status(key)); assertEquals(0, stock()); assertEquals(1, pendingEvents());
        } finally {
            db.execute("drop trigger fail_cancel_test on outbox_events"); db.execute("drop function fail_cancel_test()");
        }
        relay.publishBatch(); await(() -> confirmed() == 1);
        assertEquals(0, stock()); assertEquals(0, reconciliation.sweep());
    }

    @Test @Order(15)
    void consumerAndSweepRaceHasExactlyOneTerminalOutcome() throws Exception {
        stock(1); String key = "terminal-race-" + UUID.randomUUID();
        assertEquals(202, post(key, 1).statusCode()); OrderRequest request = intent(key); expire();
        try (var pool = Executors.newFixedThreadPool(2)) {
            var a = pool.submit(() -> processing.processOrder(request));
            var b = pool.submit(() -> reconciliation.sweep());
            a.get(); b.get();
        }
        assertTrue(List.of("CONFIRMED", "CANCELLED").contains(status(key)));
        assertEquals(1, confirmed() + stock()); assertEquals(0, reconciliation.sweep());
        assertEquals(1, orders.count());
    }
}
