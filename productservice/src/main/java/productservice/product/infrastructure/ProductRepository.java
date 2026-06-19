package productservice.product.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<ProductDocument, String> {

    Optional<ProductDocument> findBySku(String sku);
    boolean existsBySku(String sku);
    Page<ProductDocument> findByCategory(String category, Pageable pageable);
    Page<ProductDocument> findByActiveTrue(Pageable pageable);

    @Query("{ '$text': { '$search': ?0 } }")
    Page<ProductDocument> searchByText(String term, Pageable pageable);

    @Query("{ 'price': { '$gte': ?0, '$lte': ?1 }, 'active': true }")
    Page<ProductDocument> findByPriceRange(BigDecimal min, BigDecimal max, Pageable pageable);

    Page<ProductDocument> findByCategoryAndStockGreaterThanAndActiveTrue(
            String category, int minStock, Pageable pageable);
}
