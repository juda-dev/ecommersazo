package productservice.product.application;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

public record ProductResponse(
        String id,
        String name,
        String description,
        String sku,
        String category,
        BigDecimal price,
        String currency,
        int stock,
        Map<String, Object> attributes,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}
