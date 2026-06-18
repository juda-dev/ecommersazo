package configurationservice.configuration.infrastructure;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigurationRepository
    extends MongoRepository<ConfigurationDocument, String>
{
    Optional<ConfigurationDocument> findByKey(String key);

    Page<ConfigurationDocument> findByNamespace(
        String namespace,
        Pageable pageable
    );

    boolean existsByKey(String key);

    void deleteByKey(String key);
}
