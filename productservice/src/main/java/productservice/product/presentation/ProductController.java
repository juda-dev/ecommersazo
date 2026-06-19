package productservice.product.presentation;

import dev.juda.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import productservice.product.application.CreateProductRequest;
import productservice.product.application.ProductResponse;
import productservice.product.application.ProductService;
import productservice.product.application.UpdateProductRequest;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateProductRequest request,
                                    HttpServletRequest httpRequest) {
        Result<ProductResponse> result = service.create(request);
        return switch (result) {
            case Result.Success<ProductResponse> s -> ResponseEntity.status(201).body(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(httpRequest.getRequestURI()));
        };
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id, HttpServletRequest req) {
        return switch (service.findById(id)) {
            case Result.Success<ProductResponse> s -> ResponseEntity.ok(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(req.getRequestURI()));
        };
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(service.findAll(page, size, category));
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(service.search(q, page, size));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id,
                                    @Valid @RequestBody UpdateProductRequest request,
                                    HttpServletRequest httpRequest) {
        return switch (service.update(id, request)) {
            case Result.Success<ProductResponse> s -> ResponseEntity.ok(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(httpRequest.getRequestURI()));
        };
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, HttpServletRequest req) {
        return switch (service.delete(id)) {
            case Result.Success<Void> s -> ResponseEntity.noContent().build();
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(req.getRequestURI()));
        };
    }
}