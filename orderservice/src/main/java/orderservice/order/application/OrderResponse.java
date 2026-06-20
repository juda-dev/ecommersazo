package orderservice.order.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String id, String customerId, String customerName,
        String status, BigDecimal totalAmount, String currency,
        List<OrderItemResponse> items, LocalDateTime createdAt, LocalDateTime updatedAt
) {
    public record OrderItemResponse(String productId, String productName,
                                    int quantity, BigDecimal unitPrice) {}
}
