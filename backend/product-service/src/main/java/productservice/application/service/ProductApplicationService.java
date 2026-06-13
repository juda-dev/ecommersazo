package productservice.application.service;

import dev.juda.error.ConflictException;
import dev.juda.error.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import productservice.application.dto.ProductRequest;
import productservice.application.dto.ProductResponse;
import productservice.domain.model.Category;
import productservice.domain.model.Product;
import productservice.domain.repository.CategoryRepository;
import productservice.domain.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductApplicationService {

    private final ProductRepository pr;
    private final CategoryRepository cr;

    public ProductApplicationService(ProductRepository pr, CategoryRepository cr) {
        this.pr = pr;
        this.cr = cr;
    }

    public Page<ProductResponse> search(String cat, BigDecimal min, BigDecimal max, String q, Pageable p){
        return pr.search(cat, min, max, q, p).map(ProductResponse::from);
    }

    public ProductResponse getById(Long id){
        return pr.findById(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new NotFoundException("Product not found with id " + id));
    }

    public List<ProductResponse> getByIds(List<Long> ids){
        return pr.findByIdIn(ids).stream().map(ProductResponse::from).toList();
    }

    @Transactional
    public ProductResponse create(ProductRequest req){
        if (pr.existsBySku(req.sku())) throw new ConflictException("SKU exists: " + req.sku());
        Category cat = cr.findById(req.categoryId()).orElseThrow(() -> new NotFoundException("Category not found with id: " + req.categoryId()));
        Product p = new Product();
        p.setSku(req.sku());
        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setImageUrl(req.imageUrl());
        p.setCategory(cat);

        return ProductResponse.from(pr.save(p));
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest req){
        Product p = pr.findById(id).orElseThrow(() -> new NotFoundException("Product not found with id " + id));
        Category cat = cr.findById(req.categoryId()).orElseThrow(() -> new NotFoundException("Category not found with id: " + req.categoryId()));

        p.setName(req.name());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setImageUrl(req.imageUrl());
        p.setCategory(cat);

        return ProductResponse.from(pr.save(p));
    }
}
