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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderFlowIntegrationTests {
    private static final int PRODUCT_ID = 101;
    private static final HttpClient HTTP = HttpClient.newHttpClient();

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");
    @Container
    static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379);
    @Container
    static final RabbitMQContainer RABBIT = new RabbitMQContainer("rabbitmq:3.13-management-alpine");

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
}
