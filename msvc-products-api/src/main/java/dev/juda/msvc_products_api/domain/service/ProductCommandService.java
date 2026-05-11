package dev.juda.msvc_products_api.domain.service;

import dev.juda.libs_msvc_commons.domain.messaging.Reply;
import dev.juda.msvc_products_api.domain.dto.request.CreateProductRequestDto;
import dev.juda.msvc_products_api.domain.dto.request.UpdateProductRequestDto;
import java.time.Duration;

public interface ProductCommandService {
    Reply<?> sendCreateAndAwait(CreateProductRequestDto dto, Duration timeout);
    Reply<?> sendUpdateAndAwait(
        Long id,
        UpdateProductRequestDto dto,
        Duration timeout
    );
    Reply<?> sendDeleteAndAwait(Long id, Duration timeout);
}
