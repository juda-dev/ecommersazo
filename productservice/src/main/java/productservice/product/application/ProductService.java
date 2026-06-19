package productservice.product.application;

import dev.juda.pagination.PageResult;
import dev.juda.result.Result;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import productservice.product.infrastructure.ProductDocument;
import productservice.product.infrastructure.ProductRepository;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository repository;

    public Result<ProductResponse> create(CreateProductRequest request) {
        if (repository.existsBySku(request.sku())) {
            return Result.error("SKU_EXISTS", "SKU '" + request.sku() + "' already exists",  null);
        }

        ProductDocument doc =  ProductDocument.builder()
                .name(request.name())
                .description(request.description())
                .sku(request.sku())
                .category(request.category())
                .price(request.price())
                .stock(request.stock())
                .build();

        if (request.attributes() != null) {
            doc.setAttributes(request.attributes());
        }
        doc = repository.save(doc);
        log.info("Product created: {} (SKU: {})", doc.getId(), doc.getSku());
        return Result.success(toResponse(doc));
    }

    @Cacheable(value = "products", key = "#id", unless = "#result == null")
    public Result<ProductResponse> findById(String id) {
        log.debug("Cache MISS product: {}", id);
        return repository.findById(id)
                .map(doc -> Result.success(toResponse(doc)))
                .orElseGet(() -> Result.error("PRODUCT_NOT_FOUND", "Product not found", null));
    }

    public Result<PageResult<ProductResponse>> findAll(int page, int size, String category) {
        PageRequest pr = PageRequest.of(page, size);
        Page<ProductDocument> docs = (category != null && !category.isBlank())
                ? repository.findByCategory(category, pr)
                : repository.findByActiveTrue(pr);

        var responses = docs.getContent().stream().map(this::toResponse).toList();
        return Result.success(PageResult.of(responses, page, size, docs.getTotalElements()));
    }

    public Result<PageResult<ProductResponse>> search(String term, int page, int size) {
        Page<ProductDocument> docs = repository.searchByText(term, PageRequest.of(page, size));
        var responses = docs.getContent().stream().map(this::toResponse).toList();
        return Result.success(PageResult.of(responses, page, size, docs.getTotalElements()));
    }

    @CachePut(value = "products", key = "#id")
    public Result<ProductResponse> update(String id, UpdateProductRequest request) {
        ProductDocument doc = repository.findById(id).orElse(null);
        if (doc == null) return Result.error("PRODUCT_NOT_FOUND", "Product not found", null);

        if (request.name() != null) doc.setName(request.name());
        if (request.description() != null) doc.setDescription(request.description());
        if (request.price() != null) doc.setPrice(request.price());
        if (request.stock() != null) doc.setStock(request.stock());
        if (request.attributes() != null) doc.setAttributes(request.attributes());
        doc.setUpdatedAt(Instant.now());

        doc = repository.save(doc);
        log.info("Product updated: {}", id);
        return Result.success(toResponse(doc));
    }

    @CacheEvict(value = "products", key = "#id")
    public Result<Void> delete(String id) {
        ProductDocument doc = repository.findById(id).orElse(null);
        if (doc == null) return Result.error("PRODUCT_NOT_FOUND", "Product not found", null);
        doc.setActive(false);
        doc.setUpdatedAt(Instant.now());
        repository.save(doc);
        return Result.success();
    }

    private ProductResponse toResponse(ProductDocument doc) {
        return new ProductResponse(doc.getId(), doc.getName(), doc.getDescription(),
                doc.getSku(), doc.getCategory(), doc.getPrice(), doc.getCurrency(),
                doc.getStock(), doc.getAttributes(), doc.isActive(),
                doc.getCreatedAt(), doc.getUpdatedAt());
    }
}