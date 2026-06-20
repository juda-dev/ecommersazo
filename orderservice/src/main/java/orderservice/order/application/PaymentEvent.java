package orderservice.order.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentEvent(
        String paymentId,
        String orderId,
        String status,    // COMPLETED, FAILED
        BigDecimal amount,
        LocalDateTime timestamp
) {}
