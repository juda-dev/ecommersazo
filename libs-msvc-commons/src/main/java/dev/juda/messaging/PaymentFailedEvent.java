package dev.juda.messaging;

import java.time.Instant;
import java.util.UUID;

public record PaymentFailedEvent(
        UUID eventId, String eventType, Instant occurredAt, String aggregateId, int version,
        Long paymentId, Long orderId, String reason
) implements DomainEvent {}
