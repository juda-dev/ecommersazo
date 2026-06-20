package orderservice.order.presentation;

import dev.juda.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import orderservice.order.application.CreateOrderRequest;
import orderservice.order.application.OrderResponse;
import orderservice.order.application.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateOrderRequest request,
                                    HttpServletRequest httpRequest) {
        Result<OrderResponse> result = service.createOrder(request);
        return switch (result) {
            case Result.Success<OrderResponse> s -> ResponseEntity.status(201).body(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(httpRequest.getRequestURI()));
        };
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id, HttpServletRequest req) {
        return switch (service.findById(id)) {
            case Result.Success<OrderResponse> s -> ResponseEntity.ok(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(req.getRequestURI()));
        };
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.findAll(page, size));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> findByCustomer(
            @PathVariable String customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.findByCustomer(customerId, page, size));
    }
}
