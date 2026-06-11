package productservice.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import productservice.domain.model.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE (:cat IS NULL OR p.category.name = :cat) AND (:min IS NULL OR p.price >= :min) AND (:max IS NULL OR p.price <= :max) AND (:q IS NULL OR LOWER(p.name) LIKE LOWER (CONCAT('%',:q,'%'))) AND p.active = true")
    Page<Product> search(
            @Param("cat") String cat,
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max,
            @Param("q") String q,
            Pageable p);

    List<Product> findByIdIn(List<Long> ids);

    boolean existsBySku(String sku);
}
