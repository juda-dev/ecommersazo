package paymentservice.payment.presentation;

import dev.juda.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentservice.payment.application.PaymentResponse;
import paymentservice.payment.application.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;
    public PaymentController(PaymentService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.findAll(page, size));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> findByOrderId(@PathVariable String orderId, HttpServletRequest req) {
        return switch (service.findByOrderId(orderId)) {
            case Result.Success<PaymentResponse> s -> ResponseEntity.ok(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(req.getRequestURI()));
        };
    }

    @PatchMapping("/order/{orderId}/status/{status}")
    public ResponseEntity<?> updateStatus(@PathVariable String orderId,
                                          @PathVariable String status,
                                          HttpServletRequest req) {
        return switch (service.updateStatus(orderId, status)) {
            case Result.Success<PaymentResponse> s -> ResponseEntity.ok(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(req.getRequestURI()));
        };
    }
}
