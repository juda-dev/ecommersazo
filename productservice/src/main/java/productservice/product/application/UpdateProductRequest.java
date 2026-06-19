package productservice.product.application;

import java.math.BigDecimal;
import java.util.Map;

public record UpdateProductRequest(
        String name, String description, BigDecimal price,
        Integer stock, Map<String, Object> attributes
) {}
