package dev.juda.messaging;

import java.time.Instant;
import java.util.UUID;

public sealed interface DomainEvent permits OrderCreatedEvent, PaymentFailedEvent, PaymentProcessedEvent, StockReleasedEvent, StockReservedEvent, UserRegisteredEvent {
    UUID eventId();
    String eventType();
    Instant occurredAt();
    String aggregateId();
    int version();
}
