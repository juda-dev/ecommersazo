package dev.juda.messaging;

import java.time.Instant;
import java.util.UUID;

public record UserRegisteredEvent(
        UUID eventId, String eventType, Instant occurredAt, String aggregateId, int version,
                                  Long userId, String username, String email
) implements DomainEvent {}
