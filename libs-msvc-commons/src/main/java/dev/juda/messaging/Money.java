package dev.juda.messaging;

import java.math.BigDecimal;

public record Money(BigDecimal amount, String currency) {
    public Money{
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Money cannot be negative");
    }
}
