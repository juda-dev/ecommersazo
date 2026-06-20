package orderservice.order.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderEvent(
        String orderId,
        String customerId,
        String customerName,
        BigDecimal totalAmount,
        String currency,
        List<OrderItemPayload> items,
        LocalDateTime createdAt
) {
    public record OrderItemPayload(
            String productId,
            String productName,
            int quantity,
            BigDecimal unitPrice
    ) {}
}