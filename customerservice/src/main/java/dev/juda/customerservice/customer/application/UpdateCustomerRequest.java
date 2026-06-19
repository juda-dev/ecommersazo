package dev.juda.customerservice.customer.application;

public record UpdateCustomerRequest(
        String firstName,
        String lastName,
        String phone
) {}