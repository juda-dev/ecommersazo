package paymentservice.payment.application;

import java.time.LocalDateTime;

public record PaymentResponse(
        String id, String orderId, String customerId,
        java.math.BigDecimal amount, String currency, String status,
        LocalDateTime createdAt, LocalDateTime updatedAt
) {}
