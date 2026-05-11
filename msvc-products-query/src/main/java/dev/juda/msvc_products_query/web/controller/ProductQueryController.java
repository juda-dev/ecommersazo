package dev.juda.msvc_products_query.web.controller;

import dev.juda.libs_msvc_commons.domain.exception.NotFoundException;
import dev.juda.msvc_products_query.domain.dto.ProductResponseDto;
import dev.juda.msvc_products_query.domain.service.ProductQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductQueryController {

    private final ProductQueryService service;

    public ProductQueryController(ProductQueryService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ProductResponseDto findById(@PathVariable Long id) {
        ProductResponseDto dto = service.findById(id);
        if (dto == null) throw new NotFoundException(
            "Product not found: " + id
        );

        return dto;
    }

    @GetMapping
    public List<ProductResponseDto> findAll() {
        return service.findAll();
    }
}
