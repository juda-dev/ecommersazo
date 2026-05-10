package dev.juda.msvc_products_command.domain.service;

import dev.juda.msvc_products_command.domain.dto.ProductDto;

public interface ProductService {
    ProductDto create(ProductDto dto);
    ProductDto update(Long id, ProductDto dto);
    void delete(Long id);
}
