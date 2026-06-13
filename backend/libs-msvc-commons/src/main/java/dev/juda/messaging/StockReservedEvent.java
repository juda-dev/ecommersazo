package dev.juda.messaging;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record StockReservedEvent(UUID eventId, String eventType, Instant occurredAt, String aggregateId, int version,
                                 Long orderId, List<StockItem> items
) implements DomainEvent {}

