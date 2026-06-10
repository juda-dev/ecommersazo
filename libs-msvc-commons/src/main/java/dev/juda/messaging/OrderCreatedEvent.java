package dev.juda.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        String eventType,
        Instant occurredAt,
        String aggregateId,
        int version,
        Long orderId,
        Long customerId,
        List<OrderItemSnapshot> items,
        Money total
) implements DomainEvent{
}

