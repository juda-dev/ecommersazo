package paymentservice.payment.infrastructure;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox")
public class PaymentOutbox {

    @Id
    @Column(length = 36, nullable = false)
    private String id;

    @Column(name = "aggregate_id", nullable = false)
    private String aggregateId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public PaymentOutbox() {}

    public PaymentOutbox(String aggregateId, String eventType, String payload) {
        this.id = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
