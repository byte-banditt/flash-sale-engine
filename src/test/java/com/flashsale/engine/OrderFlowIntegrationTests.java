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
import com.flashsale.engine.service.InventoryService;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    InventoryService inventory;
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
        orders.deleteAll();
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
        redis.opsForValue().set("product:" + PRODUCT_ID + ":stock", Integer.toString(value));
    }

    private int stock() {
        return Integer.parseInt(redis.opsForValue().get("product:" + PRODUCT_ID + ":stock"));
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
        await(() -> orders.count() == m);
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
        await(() -> orders.count() == 1);
        assertEquals(1, orders.count());
        assertEquals(9, stock());
    }

    @Test
    @Order(4)
    void exhaustedRetriesReachDlqAndRollbackOnlyOnce() throws Exception {
        stock(1);
        String key = "fail-" + UUID.randomUUID();
        AtomicInteger completedRollbacks = new AtomicInteger();
        doAnswer(invocation -> {
            Object result = invocation.callRealMethod();
            completedRollbacks.incrementAndGet();
            return result;
        }).when(dlqConsumer).handleFailedOrder(any(OrderRequest.class));
        doAnswer(invocation -> {
            throw new IllegalStateException("forced consumer failure");
        }).when(processing).processOrder(any(OrderRequest.class));
        assertEquals(202, post(key, 1).statusCode());
        await(() -> completedRollbacks.get() == 1);
        assertEquals(1, stock());
        verify(processing, timeout(10_000).times(3)).processOrder(any(OrderRequest.class));
        verify(dlqConsumer, timeout(10_000).times(1)).handleFailedOrder(any(OrderRequest.class));
        assertEquals(0, orders.count());

        String orderId = redis.opsForValue().get("idempotency:" + key);
        // DLQ rollback releases the key; obtain the original order ID from captured invocation.
        var captor = org.mockito.ArgumentCaptor.forClass(OrderRequest.class);
        verify(dlqConsumer).handleFailedOrder(captor.capture());
        OrderRequest failed = captor.getValue();
        assertNotNull(failed.getOrderId());
        assertNull(orderId);
        publisher.convertAndSend(RabbitMQConfig.DLX_EXCHANGE, RabbitMQConfig.DLQ_ROUTING_KEY, failed);
        publisher.convertAndSend(RabbitMQConfig.DLX_EXCHANGE, RabbitMQConfig.DLQ_ROUTING_KEY, failed);
        await(() -> completedRollbacks.get() == 3);
        verify(dlqConsumer, times(3)).handleFailedOrder(any(OrderRequest.class));
        assertEquals(1, stock());
    }

    @Test
    @Order(5)
    void publishFailureRestoresStock() throws Exception {
        stock(1);
        doThrow(new AmqpException("forced publish failure"))
                .when(publisher).convertAndSend(eq(RabbitMQConfig.ORDER_EXCHANGE),
                        eq(RabbitMQConfig.ORDER_ROUTING_KEY), any(OrderRequest.class));
        HttpResponse<String> response = post("publish-" + UUID.randomUUID(), 1);
        assertEquals(503, response.statusCode());
        assertEquals(1, stock());
        assertEquals(0, orders.count());
    }

    @Test
    @Order(6)
    void brokerNackRestoresStockAndReleasesKey() throws Exception {
        stock(1);
        String key = "nack-" + UUID.randomUUID();
        doAnswer(invocation -> {
            CorrelationData correlation = invocation.getArgument(3);
            correlation.getFuture().complete(new CorrelationData.Confirm(false, "forced broker nack"));
            return null;
        }).when(publisher).convertAndSend(eq(RabbitMQConfig.ORDER_EXCHANGE),
                eq(RabbitMQConfig.ORDER_ROUTING_KEY), any(OrderRequest.class), any(CorrelationData.class));
        assertEquals(503, post(key, 1).statusCode());
        assertEquals(1, stock());
        assertNull(redis.opsForValue().get("idempotency:" + key));
        assertEquals(0, orders.count());
    }

    @Test
    @Order(7)
    void actualUnroutableReturnRestoresStock() throws Exception {
        stock(1);
        String key = "return-" + UUID.randomUUID();
        Binding binding = new Binding(RabbitMQConfig.ORDER_QUEUE, Binding.DestinationType.QUEUE,
                RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, null);
        rabbitAdmin.removeBinding(binding);
        try {
            assertEquals(503, post(key, 1).statusCode());
            assertEquals(1, stock());
            assertNull(redis.opsForValue().get("idempotency:" + key));
            assertEquals(0, orders.count());
        } finally {
            rabbitAdmin.declareBinding(binding);
        }
    }

    @Test
    @Order(8)
    void confirmTimeoutRestoresStock() throws Exception {
        stock(1);
        String key = "timeout-" + UUID.randomUUID();
        doNothing().when(publisher).convertAndSend(eq(RabbitMQConfig.ORDER_EXCHANGE),
                eq(RabbitMQConfig.ORDER_ROUTING_KEY), any(OrderRequest.class), any(CorrelationData.class));
        assertEquals(503, post(key, 1).statusCode());
        assertEquals(1, stock());
        assertNull(redis.opsForValue().get("idempotency:" + key));
        assertEquals(0, orders.count());
    }
    private RabbitTemplate independentPublisher() {
        RabbitTemplate direct = new RabbitTemplate(publisher.getConnectionFactory());
        direct.setMessageConverter(publisher.getMessageConverter());
        return direct;
    }

    private void assertDecision(OrderRequest request, String decision) {
        assertEquals(decision, redis.opsForValue().get("order:" + request.getOrderId() + ":decision"));
    }

    @Test
    @Order(9)
    void lostConfirmAfterDeliveryKeepsConsumerOwnedReservation() throws Exception {
        stock(1);
        String key = "lost-confirm-consumer-" + UUID.randomUUID();
        AtomicReference<OrderRequest> sent = new AtomicReference<>();
        RabbitTemplate direct = independentPublisher();
        doAnswer(invocation -> {
            OrderRequest request = invocation.getArgument(2);
            sent.set(request);
            // A real broker delivery, but the original correlation never completes.
            direct.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, request);
            await(() -> orders.findByIdempotencyKey(key).isPresent());
            return null;
        }).when(publisher).convertAndSend(eq(RabbitMQConfig.ORDER_EXCHANGE),
                eq(RabbitMQConfig.ORDER_ROUTING_KEY), any(OrderRequest.class), any(CorrelationData.class));
        assertEquals(503, post(key, 1).statusCode());
        assertEquals(1, orders.count());
        assertEquals(0, stock());
        assertEquals(sent.get().getOrderId(), redis.opsForValue().get("idempotency:" + key));
        assertDecision(sent.get(), "CONSUMER");
        assertNull(redis.opsForValue().get("order:" + sent.get().getOrderId() + ":rolled-back"));
    }

    @Test
    @Order(10)
    void lostConfirmRefundWinsAndLaterDeliveryCannotPersist() throws Exception {
        stock(1);
        String key = "lost-confirm-refund-" + UUID.randomUUID();
        CountDownLatch entered = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        AtomicInteger completed = new AtomicInteger();
        AtomicReference<OrderRequest> sent = new AtomicReference<>();
        RabbitTemplate direct = independentPublisher();
        doAnswer(invocation -> {
            entered.countDown();
            assertTrue(release.await(20, TimeUnit.SECONDS), "Consumer was not released");
            Object result = invocation.callRealMethod();
            completed.incrementAndGet();
            return result;
        }).when(processing).processOrder(any(OrderRequest.class));
        doAnswer(invocation -> {
            OrderRequest request = invocation.getArgument(2);
            sent.set(request);
            direct.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, request);
            assertTrue(entered.await(10, TimeUnit.SECONDS), "Message never reached the real consumer");
            return null;
        }).when(publisher).convertAndSend(eq(RabbitMQConfig.ORDER_EXCHANGE),
                eq(RabbitMQConfig.ORDER_ROUTING_KEY), any(OrderRequest.class), any(CorrelationData.class));
        try {
            assertEquals(503, post(key, 1).statusCode());
            assertEquals(1, stock());
            assertNull(redis.opsForValue().get("idempotency:" + key));
            assertDecision(sent.get(), "REFUND");
        } finally {
            release.countDown();
        }
        await(() -> completed.get() == 1);
        assertEquals(0, orders.count());
        assertEquals(1, stock());
        assertDecision(sent.get(), "REFUND");
    }

    @Test
    @Order(11)
    void nackWithoutDeliveryRestoresStockAndOldDeliveryCannotUndoRetry() throws Exception {
        stock(1);
        String key = "nack-retry-" + UUID.randomUUID();
        AtomicReference<OrderRequest> failed = new AtomicReference<>();
        doAnswer(invocation -> {
            failed.set(invocation.getArgument(2));
            CorrelationData correlation = invocation.getArgument(3);
            correlation.getFuture().complete(new CorrelationData.Confirm(false, "forced broker nack"));
            return null;
        }).when(publisher).convertAndSend(eq(RabbitMQConfig.ORDER_EXCHANGE),
                eq(RabbitMQConfig.ORDER_ROUTING_KEY), any(OrderRequest.class), any(CorrelationData.class));
        assertEquals(503, post(key, 1).statusCode());
        assertEquals(1, stock());
        assertEquals(0, orders.count());
        assertNull(redis.opsForValue().get("idempotency:" + key));
        assertDecision(failed.get(), "REFUND");
        reset(publisher);
        assertEquals(202, post(key, 1).statusCode());
        await(() -> orders.count() == 1);
        String replacement = orders.findByIdempotencyKey(key).orElseThrow().getOrderId();
        assertNotEquals(failed.get().getOrderId(), replacement);
        publisher.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, failed.get());
        verify(processing, timeout(10_000)).processOrder(argThat(r -> r.getOrderId().equals(failed.get().getOrderId())));
        assertEquals(1, orders.count());
        assertEquals(0, stock());
        assertEquals(replacement, redis.opsForValue().get("idempotency:" + key));
        assertDecision(failed.get(), "REFUND");
    }

    @Test
    @Order(12)
    void claimSurvivesCrashAndRedeliveryPersistsExactlyOnce() throws Exception {
        stock(1);
        String key = "claim-redelivery-" + UUID.randomUUID();
        OrderRequest request = new OrderRequest(UUID.randomUUID().toString(), (long) PRODUCT_ID, 1, key);
        assertEquals(1, inventory.reserveStock(Integer.toString(PRODUCT_ID), 1, key, request.getOrderId()));
        // Simulate a consumer dying after Redis claim and before DB persistence.
        assertTrue(inventory.claimForPersistence(request.getOrderId(), key));
        assertEquals(0, orders.count());
        publisher.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, request);
        await(() -> orders.count() == 1);
        publisher.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE, RabbitMQConfig.ORDER_ROUTING_KEY, request);
        verify(processing, timeout(10_000).times(2)).processOrder(any(OrderRequest.class));
        assertEquals(1, orders.count());
        assertEquals(request.getOrderId(), orders.findByIdempotencyKey(key).orElseThrow().getOrderId());
        assertEquals(0, stock());
        assertEquals(request.getOrderId(), redis.opsForValue().get("idempotency:" + key));
        assertDecision(request, "CONSUMER");
        assertFalse(inventory.rollbackStockOnce(request.getOrderId(), Integer.toString(PRODUCT_ID), 1, key));
        assertEquals(0, stock());
    }

    @Test
    @Order(13)
    void deadLetterOfClaimedOrderResumesPersistenceInsteadOfOrphaningReservation() throws Exception {
        stock(1);
        String key = "claim-dlq-" + UUID.randomUUID();
        OrderRequest request = new OrderRequest(UUID.randomUUID().toString(), (long) PRODUCT_ID, 1, key);
        assertEquals(1, inventory.reserveStock(Integer.toString(PRODUCT_ID), 1, key, request.getOrderId()));
        assertTrue(inventory.claimForPersistence(request.getOrderId(), key));
        publisher.convertAndSend(RabbitMQConfig.DLX_EXCHANGE, RabbitMQConfig.DLQ_ROUTING_KEY, request);
        await(() -> orders.count() == 1);
        assertEquals(1, orders.count());
        assertEquals(0, stock());
        assertEquals(request.getOrderId(), redis.opsForValue().get("idempotency:" + key));
        assertDecision(request, "CONSUMER");
        assertNull(redis.opsForValue().get("order:" + request.getOrderId() + ":rolled-back"));
    }

}
