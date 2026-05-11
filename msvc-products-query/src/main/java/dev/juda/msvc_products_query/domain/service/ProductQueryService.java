package dev.juda.msvc_products_query.domain.service;

import dev.juda.msvc_products_query.domain.dto.ProductResponseDto;
import dev.juda.msvc_products_query.domain.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductQueryService {

    private final ProductRepository repo;

    public ProductQueryService(ProductRepository repo) {
        this.repo = repo;
    }

    public ProductResponseDto findById(Long id) {
        return repo
            .findById(id)
            .map(p ->
                new ProductResponseDto(p.getId(), p.getName(), p.getPrice())
            )
            .orElse(null);
    }

    public List<ProductResponseDto> findAll() {
        return repo
            .findAll()
            .stream()
            .map(p ->
                new ProductResponseDto(p.getId(), p.getName(), p.getPrice())
            )
            .toList();
    }
}
