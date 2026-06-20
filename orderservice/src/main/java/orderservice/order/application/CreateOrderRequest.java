package orderservice.order.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank String customerId,
        String customerName,
        @NotEmpty List<OrderItemRequest> items
) {
    public record OrderItemRequest(
            @NotBlank String productId,
            String productName,
            @Positive int quantity,
            @NotNull @Positive BigDecimal unitPrice
    ) {}
}