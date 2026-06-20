package paymentservice.payment.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentEvent(
        String paymentId,
        String orderId,
        String status,
        BigDecimal amount,
        String currency,
        LocalDateTime timestamp
) {}
