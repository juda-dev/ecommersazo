package paymentservice.payment.application;

import java.math.BigDecimal;
import java.util.List;

public record OrderEvent(
        String orderId,
        String customerId,
        String customerName,
        BigDecimal totalAmount,
        String currency,
        List<OrderItemPayload> items,
        String createdAt
) {
    public record OrderItemPayload(String productId, String productName,
                                   int quantity, BigDecimal unitPrice) {}
}
