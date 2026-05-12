package dev.juda.msvc_products_api.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.juda.libs_msvc_commons.domain.exception.BadRequestException;
import dev.juda.libs_msvc_commons.domain.messaging.Reply;
import dev.juda.msvc_products_api.domain.dto.request.CreateProductRequestDto;
import dev.juda.msvc_products_api.domain.dto.request.UpdateProductRequestDto;
import dev.juda.msvc_products_api.domain.dto.response.ProductResponseDto;
import dev.juda.msvc_products_api.domain.service.ProductCommandService;
import jakarta.validation.Valid;
import java.time.Duration;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductCommandService service;
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    public ProductController(ProductCommandService service) {
        this.service = service;
    }

    @PostMapping
    public ProductResponseDto create(
        @Valid @RequestBody CreateProductRequestDto dto
    ) {
        Reply<?> reply = service.sendCreateAndAwait(dto, TIMEOUT);
        if (!"SUCCESS".equals(reply.status())) throw new BadRequestException(
            reply.message()
        );

        ObjectMapper mapper = new ObjectMapper();
        ProductResponseDto res = mapper.convertValue(
            reply.body(),
            ProductResponseDto.class
        );
        return res;
    }

    @PutMapping("/{id}")
    public ProductResponseDto update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateProductRequestDto dto
    ) {
        Reply<?> reply = service.sendUpdateAndAwait(id, dto, TIMEOUT);
        if (!"SUCCESS".equals(reply.status())) throw new BadRequestException(
            reply.message()
        );
        ObjectMapper mapper = new ObjectMapper();
        ProductResponseDto res = mapper.convertValue(
            reply.body(),
            ProductResponseDto.class
        );
        return res;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        Reply<?> reply = service.sendDeleteAndAwait(id, TIMEOUT);
        if (!"SUCCESS".equals(reply.status())) throw new BadRequestException(
            reply.message()
        );
    }
}
