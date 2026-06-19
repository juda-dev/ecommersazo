package productservice.product.infrastructure;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@Document(collection = "products")
public class ProductDocument {

    @Id
    private String id;

    @TextIndexed
    @Field("name")
    private String name;

    @TextIndexed
    @Field("description")
    private String description;

    @Field("sku")
    @Indexed(unique = true)
    private String sku;

    @Field("category")
    @Indexed
    private String category;

    @Field("price")
    private BigDecimal price;

    @Field("currency")
    private String currency = "COP";

    @Field("stock")
    private int stock;

    @Field("attributes")
    private Map<String, Object> attributes;

    @Field("active")
    private boolean active = true;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;
}
