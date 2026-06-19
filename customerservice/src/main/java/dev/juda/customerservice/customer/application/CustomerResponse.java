package dev.juda.customerservice.customer.application;

import java.time.Instant;

public record CustomerResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String phone,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {}