package dev.juda.msvc_products_command.domain.service.impl;

import dev.juda.libs_msvc_commons.domain.exception.NotFoundException;
import dev.juda.msvc_products_command.domain.dto.ProductDto;
import dev.juda.msvc_products_command.domain.repository.ProductRepository;
import dev.juda.msvc_products_command.domain.service.ProductService;
import dev.juda.msvc_products_command.persistence.entity.Product;
import dev.juda.msvc_products_command.persistence.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductServiceImpl implements ProductService {

    public final ProductRepository repo;

    public ProductServiceImpl(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    @Transactional
    public ProductDto create(ProductDto dto) {
        return ProductMapper.toDto(repo.save(ProductMapper.toEntity(dto)));
    }

    @Override
    @Transactional
    public ProductDto update(Long id, ProductDto dto) {
        Product p = repo
            .findById(id)
            .orElseThrow(() ->
                new NotFoundException("Product not found: " + id)
            );

        p.setName(dto.name());
        p.setPrice(dto.price());

        return ProductMapper.toDto(repo.save(p));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new NotFoundException(
            "Product not found: " + id
        );

        repo.deleteById(id);
    }
}
