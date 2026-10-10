package com.flashsale.engine.config;

import com.flashsale.engine.service.OutboxRelay;
import com.flashsale.engine.service.OrderReconciliation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name="flashsale.outbox.scheduling-enabled", havingValue="true", matchIfMissing=true)
public class OutboxScheduling {
    private final OutboxRelay relay;
    private final OrderReconciliation reconciliation;
    public OutboxScheduling(OutboxRelay relay, OrderReconciliation reconciliation) {
        this.relay = relay; this.reconciliation = reconciliation;
    }
    @Scheduled(fixedDelayString="${flashsale.outbox.poll-delay-ms:20}")
    public void publish() { relay.publishBatch(); }
    @Scheduled(fixedDelayString="${flashsale.outbox.sweep-delay-ms:1000}")
    public void reconcile() { reconciliation.sweep(); }
}
