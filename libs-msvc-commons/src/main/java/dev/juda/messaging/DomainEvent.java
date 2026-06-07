package dev.juda.messaging;

import java.time.Instant;

public abstract class DomainEvent {
    private final String eventId;
    private final String eventType;
    private final Instant occurredAt;
    private final String aggregateId;
    private final int version;

    protected DomainEvent(String eventId, String eventType, Instant occurredAt, String aggregateId, int version) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.aggregateId = aggregateId;
        this.version = version;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public int getVersion() {
        return version;
    }
}
