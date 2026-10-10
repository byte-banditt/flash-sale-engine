package com.flashsale.engine.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/** Schema mapping; workers use explicit JDBC row locks rather than detached entities. */
@Entity
@Table(name = "outbox_events", indexes = @Index(name = "outbox_pending_created", columnList = "status,created_at"))
public class OutboxEvent {
    @Id
    @Column(name = "event_id")
    private UUID eventId;
    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id", referencedColumnName = "order_id", nullable = false, unique = true)
    private Order order;
    @Column(nullable = false)
    private String eventType;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    public enum Status { PENDING, PUBLISHED, FAILED }
}
