package paymentservice.payment.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final PaymentService paymentService;

    public OrderEventConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.order-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void onOrderCreated(
            @Payload OrderEvent event,
            @Header(KafkaHeaders.RECEIVED_KEY) String key,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Received OrderCreated event: orderId={}, customer={}, amount={}, partition={}, offset={}",
                event.orderId(), event.customerName(), event.totalAmount(), partition, offset);

        try {
            paymentService.createPaymentFromOrder(event);
            log.info("Payment created for order {}", event.orderId());
        } catch (Exception e) {
            log.error("Failed to create payment for order {}: {}", event.orderId(), e.getMessage());
            throw e;
        }
    }
}
