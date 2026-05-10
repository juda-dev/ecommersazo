package dev.juda.msvc_products_command.persistence.mapper;

import dev.juda.msvc_products_command.domain.dto.ProductDto;
import dev.juda.msvc_products_command.persistence.entity.Product;

public final class ProductMapper {

    private ProductMapper() {}

    public static ProductDto toDto(Product p) {
        return new ProductDto(p.getId(), p.getName(), p.getPrice());
    }

    public static Product toEntity(ProductDto dto) {
        return new Product(dto.name(), dto.price());
    }
}
