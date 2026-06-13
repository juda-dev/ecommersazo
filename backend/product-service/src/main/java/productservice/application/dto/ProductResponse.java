package productservice.application.dto;

import productservice.domain.model.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        String imageUrl,
        String categoryName
) {
    public static ProductResponse from(Product p){
        return new ProductResponse(
                p.getId(), p.getSku(), p.getName(), p.getDescription(), p.getPrice(), p.getImageUrl(),
                p.getCategory() != null ? p.getCategory().getName():null
        );
    }

}
