package configurationservice.configuration.application;

import com.fasterxml.jackson.annotation.JsonInclude;
import configurationservice.configuration.infrastructure.ConfigurationDocument;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ConfigurationEvent(
    String type,
    String key,
    Object value,
    String namespace,
    Instant timestamp
) {
    public static ConfigurationEvent created(ConfigurationDocument doc) {
        return new ConfigurationEvent(
            "CREATED",
            doc.getKey(),
            doc.getValue(),
            doc.getNamespace(),
            Instant.now()
        );
    }

    public static ConfigurationEvent updated(ConfigurationDocument doc) {
        return new ConfigurationEvent(
            "UPDATED",
            doc.getKey(),
            doc.getValue(),
            doc.getNamespace(),
            Instant.now()
        );
    }

    public static ConfigurationEvent deleted(String key) {
        return new ConfigurationEvent(
            "DELETED",
            key,
            null,
            null,
            Instant.now()
        );
    }
}
