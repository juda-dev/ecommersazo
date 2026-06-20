package paymentservice.payment.application;

import dev.juda.pagination.PageResult;
import dev.juda.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import paymentservice.payment.domain.Payment;
import paymentservice.payment.domain.PaymentStatus;
import paymentservice.payment.infrastructure.PaymentOutbox;
import paymentservice.payment.infrastructure.PaymentOutboxRepository;
import paymentservice.payment.infrastructure.PaymentRepository;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final PaymentOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentOutboxRepository outboxRepository,
                          ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Payment createPaymentFromOrder(OrderEvent event) {
        if (paymentRepository.existsByOrderId(event.orderId())) {
            log.warn("Payment already exists for order {}", event.orderId());
            return paymentRepository.findByOrderId(event.orderId()).orElse(null);
        }

        Payment payment = new Payment(event.orderId(), event.customerId(),
                event.totalAmount(), event.currency());
        payment = paymentRepository.save(payment);
        log.info("Payment {} created for order {}", payment.getId(), event.orderId());
        return payment;
    }

    @Transactional
    public Result<PaymentResponse> updateStatus(String orderId, String newStatusStr) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElse(null);
        if (payment == null) {
            return Result.error("PAYMENT_NOT_FOUND", "Payment for order " + orderId + " not found", null);
        }

        PaymentStatus newStatus;
        try {
            newStatus = PaymentStatus.valueOf(newStatusStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Result.error("INVALID_STATUS",
                    "Invalid payment status: " + newStatusStr + ". Valid: COMPLETED, FAILED, REFUNDED", null);
        }

        payment.setStatus(newStatus);
        payment.setUpdatedAt(LocalDateTime.now());
        payment = paymentRepository.save(payment);
        log.info("Payment {} status updated to {}", payment.getId(), newStatus);

        PaymentEvent event = new PaymentEvent(payment.getId(), payment.getOrderId(),
                newStatus.name(), payment.getAmount(), payment.getCurrency(), LocalDateTime.now());

        try {
            String payload = objectMapper.writeValueAsString(event);
            PaymentOutbox outbox = new PaymentOutbox(payment.getOrderId(),
                    "Payment" + newStatus.name().charAt(0)
                            + newStatus.name().substring(1).toLowerCase(),
                    payload);
            outboxRepository.save(outbox);
            log.info("Outbox record created for payment event: {}", newStatus);
        } catch (Exception e) {
            log.error("Failed to create outbox record", e);
            throw new RuntimeException("Failed to publish payment event", e);
        }

        return Result.success(toResponse(payment));
    }

    public Result<PaymentResponse> findByOrderId(String orderId) {
        return paymentRepository.findByOrderId(orderId)
                .map(p -> Result.success(toResponse(p)))
                .orElseGet(() -> Result.error("PAYMENT_NOT_FOUND",
                        "Payment for order " + orderId + " not found", null));
    }

    public Result<PageResult<PaymentResponse>> findAll(int page, int size) {
        Page<Payment> payments = paymentRepository.findAll(PageRequest.of(page, size));
        var responses = payments.getContent().stream().map(this::toResponse).toList();
        return Result.success(PageResult.of(responses, page, size, payments.getTotalElements()));
    }

    private PaymentResponse toResponse(Payment p) {
        return new PaymentResponse(p.getId(), p.getOrderId(), p.getCustomerId(),
                p.getAmount(), p.getCurrency(), p.getStatus().name(),
                p.getCreatedAt(), p.getUpdatedAt());
    }
}
