package dev.juda.messaging;

import java.time.Instant;
import java.util.UUID;

public record StockReleasedEvent(
        UUID eventId, String eventType, Instant occurredAt, String aggregateId, int version,
                                 Long orderId, String reason
) implements DomainEvent {}
