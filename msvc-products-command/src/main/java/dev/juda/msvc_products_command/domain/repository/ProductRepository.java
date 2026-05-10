package dev.juda.msvc_products_command.domain.repository;

import dev.juda.msvc_products_command.persistence.entity.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {}
