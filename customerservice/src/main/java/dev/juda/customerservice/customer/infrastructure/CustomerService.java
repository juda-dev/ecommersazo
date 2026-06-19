package dev.juda.customerservice.customer.infrastructure;

import dev.juda.customerservice.customer.application.CreateCustomerRequest;
import dev.juda.customerservice.customer.application.CustomerResponse;
import dev.juda.customerservice.customer.application.UpdateCustomerRequest;
import dev.juda.pagination.PageResult;
import dev.juda.result.Result;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository repository;


    public Result<CustomerResponse> create(CreateCustomerRequest request) {
        if (repository.existsByEmail(request.email())) {
            return Result.error("EMAIL_EXISTS", "Email '" + request.email() + "' is already registered", null);
        }

        CustomerDocument doc =  CustomerDocument.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .build();

        doc = repository.save(doc);
        log.info("Customer created: {} ({})", doc.getId(), doc.getEmail());
        return Result.success(toResponse(doc));
    }

    @Cacheable(value = "customers", key = "#id", unless = "#result == null")
    public Result<CustomerResponse> findById(String id) {
        log.debug("Cache MISS for customer: {} — querying MongoDB", id);
        return repository.findById(id)
                .map(doc -> Result.success(toResponse(doc)))
                .orElseGet(() -> Result.error("CUSTOMER_NOT_FOUND",
                        "Customer with id " + id + " not found", null));
    }

    @Cacheable(value = "customers-by-email", key = "#email", unless = "#result == null")
    public Result<CustomerResponse> findByEmail(String email) {
        log.debug("Cache MISS for email: {} — querying MongoDB", email);
        return repository.findByEmail(email)
                .map(doc -> Result.success(toResponse(doc)))
                .orElseGet(() -> Result.error("CUSTOMER_NOT_FOUND",
                        "Customer with email " + email + " not found", null));
    }

    public Result<PageResult<CustomerResponse>> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<CustomerDocument> docs = repository.findByActiveTrue(pageRequest);

        var responses = docs.getContent().stream()
                .map(this::toResponse).toList();

        return Result.success(PageResult.of(responses, page, size, docs.getTotalElements()));
    }

    @CachePut(value = "customers", key = "#id")
    public Result<CustomerResponse> update(String id, UpdateCustomerRequest request) {
        CustomerDocument doc = repository.findById(id).orElse(null);
        if (doc == null) {
            return Result.error("CUSTOMER_NOT_FOUND", "Customer with id " + id + " not found", null);
        }

        if (request.firstName() != null) doc.setFirstName(request.firstName());
        if (request.lastName() != null) doc.setLastName(request.lastName());
        if (request.phone() != null) doc.setPhone(request.phone());
        doc.setUpdatedAt(Instant.now());

        doc = repository.save(doc);
        log.info("Customer updated: {}", id);

        return Result.success(toResponse(doc));
    }

    @CacheEvict(value = "customers", key = "#id")
    public Result<Void> delete(String id) {
        CustomerDocument doc = repository.findById(id).orElse(null);
        if (doc == null) {
            return Result.error("CUSTOMER_NOT_FOUND", "Customer with id " + id + " not found", null);
        }

        doc.setActive(false);
        doc.setUpdatedAt(Instant.now());
        repository.save(doc);
        log.info("Customer soft-deleted: {}", id);
        return Result.success();
    }

    private CustomerResponse toResponse(CustomerDocument doc) {
        return new CustomerResponse(
                doc.getId(), doc.getFirstName(), doc.getLastName(),
                doc.getEmail(), doc.getPhone(), doc.isActive(),
                doc.getCreatedAt(), doc.getUpdatedAt()
        );
    }
}
