package productservice.infrastructure.web;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import productservice.application.dto.ProductRequest;
import productservice.application.dto.ProductResponse;
import productservice.application.service.ProductApplicationService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductApplicationService svc;

    public ProductController(ProductApplicationService svc) {
        this.svc = svc;
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> search(@RequestParam(required = false) String cat, @RequestParam(required = false) BigDecimal min, @RequestParam(required = false) BigDecimal max, @RequestParam(required = false) String q, Pageable p){
        return ResponseEntity.ok(svc.search(cat, min, max, q, p));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id){
        return ResponseEntity.ok(svc.getById(id));
    }

    @GetMapping("/batch")
    public ResponseEntity<List<ProductResponse>> getByIds(@RequestParam List<Long> ids){
        return ResponseEntity.ok(svc.getByIds(ids));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest req){
        return ResponseEntity.status(HttpStatus.CREATED).body(svc.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest req){
        return ResponseEntity.ok(svc.update(id, req));
    }
}
