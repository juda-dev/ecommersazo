package orderservice.order.application;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);
    private final OrderService orderService;

    @KafkaListener(
            topics = "${app.kafka.topics.payment-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @CircuitBreaker(name = "paymentService", fallbackMethod = "handlePaymentEventFallback")
    public void onPaymentEvent(
            @Payload PaymentEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Received payment event: orderId={}, status={}, partition={}, offset={}",
                event.orderId(), event.status(), partition, offset);

        orderService.updateOrderStatus(event.orderId(), event.status());

        log.info("Order {} status updated based on payment event", event.orderId());
    }

    public void handlePaymentEventFallback(PaymentEvent event, String key,
                                           int partition, long offset, Throwable t) {
        log.error("Circuit breaker OPEN — payment event rejected for order {}: {}",
                event.orderId(), t.getMessage());
    }
}
