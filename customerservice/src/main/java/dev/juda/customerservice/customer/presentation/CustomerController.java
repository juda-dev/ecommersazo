package dev.juda.customerservice.customer.presentation;

import dev.juda.customerservice.customer.application.CreateCustomerRequest;
import dev.juda.customerservice.customer.application.CustomerResponse;
import dev.juda.customerservice.customer.application.UpdateCustomerRequest;
import dev.juda.customerservice.customer.infrastructure.CustomerService;
import dev.juda.pagination.PageResult;
import dev.juda.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService service;


    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateCustomerRequest request,
                                    HttpServletRequest httpRequest) {
        Result<CustomerResponse> result = service.create(request);
        return switch (result) {
            case Result.Success<CustomerResponse> s -> ResponseEntity.status(201).body(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(httpRequest.getRequestURI()));
        };
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable String id, HttpServletRequest request) {
        Result<CustomerResponse> result = service.findById(id);
        return switch (result) {
            case Result.Success<CustomerResponse> s -> ResponseEntity.ok(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(request.getRequestURI()));
        };
    }

    @GetMapping
    public ResponseEntity<?> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Result<PageResult<CustomerResponse>> result = service.findAll(page, size);
        return switch (result) {
            case Result.Success<PageResult<CustomerResponse>> s -> ResponseEntity.ok(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus()).body(e.toErrorResponse(null));
        };
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id,
                                    @Valid @RequestBody UpdateCustomerRequest request,
                                    HttpServletRequest httpRequest) {
        Result<CustomerResponse> result = service.update(id, request);
        return switch (result) {
            case Result.Success<CustomerResponse> s -> ResponseEntity.ok(s.value());
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(httpRequest.getRequestURI()));
        };
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id, HttpServletRequest request) {
        Result<Void> result = service.delete(id);
        return switch (result) {
            case Result.Success<Void> s -> ResponseEntity.noContent().build();
            case Result.Error e -> ResponseEntity.status(e.httpStatus())
                    .body(e.toErrorResponse(request.getRequestURI()));
        };
    }
}
