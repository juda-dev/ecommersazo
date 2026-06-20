package orderservice.order.application;

import tools.jackson.databind.ObjectMapper;
import dev.juda.pagination.PageResult;
import dev.juda.result.Result;
import lombok.RequiredArgsConstructor;
import orderservice.order.domain.Order;
import orderservice.order.domain.OrderItem;
import orderservice.order.domain.OrderStatus;
import orderservice.order.infrastructure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final OrderOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Result<OrderResponse> createOrder(CreateOrderRequest request) {
        Order order = new Order(request.customerId(), request.customerName());

        for (var itemReq : request.items()) {
            OrderItem item = new OrderItem(
                    itemReq.productId(), itemReq.productName(),
                    itemReq.quantity(), itemReq.unitPrice()
            );
            order.addItem(item);
        }
        order.calculateTotal();

        order = orderRepository.save(order);
        log.info("Order {} saved with status PENDING", order.getId());

        OrderEvent event = new OrderEvent(
                order.getId(), order.getCustomerId(), order.getCustomerName(),
                order.getTotalAmount(), order.getCurrency(),
                order.getItems().stream()
                        .map(i -> new OrderEvent.OrderItemPayload(
                                i.getProductId(), i.getProductName(),
                                i.getQuantity(), i.getUnitPrice()))
                        .toList(),
                order.getCreatedAt()
        );

        try {
            String payload = objectMapper.writeValueAsString(event);

            OrderOutbox outbox = new OrderOutbox(order.getId(), "OrderCreated", payload);
            outboxRepository.save(outbox);

            outboxRepository.save(outbox);
            log.info("Outbox record created for order {}", order.getId());

        } catch (Exception e) {
            log.error("Failed to serialize order event", e);
            throw new RuntimeException("Failed to create order event", e);
        }

        return Result.success(toResponse(order));
    }

    public Result<OrderResponse> findById(String orderId) {
        return orderRepository.findById(orderId)
                .map(order -> Result.success(toResponse(order)))
                .orElseGet(() -> Result.error("ORDER_NOT_FOUND",
                        "Order " + orderId + " not found", null));
    }

    public Result<PageResult<OrderResponse>> findAll(int page, int size) {
        Page<Order> orders = orderRepository.findAll(PageRequest.of(page, size));
        var responses = orders.getContent().stream().map(this::toResponse).toList();
        return Result.success(PageResult.of(responses, page, size, orders.getTotalElements()));
    }

    public Result<PageResult<OrderResponse>> findByCustomer(String customerId, int page, int size) {
        Page<Order> orders = orderRepository.findByCustomerId(customerId, PageRequest.of(page, size));
        var responses = orders.getContent().stream().map(this::toResponse).toList();
        return Result.success(PageResult.of(responses, page, size, orders.getTotalElements()));
    }

    @Transactional
    public void updateOrderStatus(String orderId, String paymentStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        OrderStatus newStatus = switch (paymentStatus) {
            case "COMPLETED" -> OrderStatus.CONFIRMED;
            case "FAILED" -> OrderStatus.CANCELLED;
            default -> throw new IllegalArgumentException("Unknown payment status: " + paymentStatus);
        };

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, newStatus);
    }

    private OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(), order.getCustomerId(), order.getCustomerName(),
                order.getStatus().name(), order.getTotalAmount(), order.getCurrency(),
                order.getItems().stream()
                        .map(i -> new OrderResponse.OrderItemResponse(
                                i.getProductId(), i.getProductName(),
                                i.getQuantity(), i.getUnitPrice()))
                        .toList(),
                order.getCreatedAt(), order.getUpdatedAt()
        );
    }
}