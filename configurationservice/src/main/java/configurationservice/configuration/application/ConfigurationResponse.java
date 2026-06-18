package configurationservice.configuration.application;

import java.time.Instant;

public record ConfigurationResponse(
    String id,
    String key,
    Object value,
    String description,
    String namespace,
    Instant createdAt,
    Instant updatedAt
) {}
