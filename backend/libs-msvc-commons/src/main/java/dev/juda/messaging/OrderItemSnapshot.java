package dev.juda.messaging;

import java.math.BigDecimal;

public record OrderItemSnapshot(Long productId,
                                String sku,
                                String productName,
                                BigDecimal unitPrice,
                                Integer quantity){}
