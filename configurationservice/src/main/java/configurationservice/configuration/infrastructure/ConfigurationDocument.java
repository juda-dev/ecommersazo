package configurationservice.configuration.infrastructure;

import java.time.Instant;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Builder
@Document(collection = "configurations")
public class ConfigurationDocument {

    @Id
    private String id;

    @Field("key")
    @Indexed(unique = true)
    private String key;

    @Field("value")
    private Object value;

    @Field("description")
    private String description;

    @Field("namespace")
    private String namespace;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;
}
