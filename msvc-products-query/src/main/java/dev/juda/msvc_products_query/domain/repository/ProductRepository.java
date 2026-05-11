package dev.juda.msvc_products_query.domain.repository;

import dev.juda.msvc_products_query.persistence.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {}
