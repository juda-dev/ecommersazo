package productservice.product.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Map;

public record CreateProductRequest(
        @NotBlank String name,
        String description,
        @NotBlank String sku,
        @NotBlank String category,
        @NotNull @Positive BigDecimal price,
        @PositiveOrZero int stock,
        Map<String, Object> attributes
) {}