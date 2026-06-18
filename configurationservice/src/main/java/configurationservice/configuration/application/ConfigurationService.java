package configurationservice.configuration.application;

import configurationservice.configuration.infrastructure.ConfigurationDocument;
import configurationservice.configuration.infrastructure.ConfigurationRepository;
import dev.juda.pagination.PageResult;
import dev.juda.result.Result;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private static final Logger log = LoggerFactory.getLogger(
        ConfigurationService.class
    );
    private final ConfigurationRepository repository;
    private final BroadcastService broadcastService;

    public Result<ConfigurationResponse> create(
        CreateConfigurationRequest request
    ) {
        if (repository.existsByKey(request.key())) {
            return Result.error(
                "CONFIG_KEY_EXISTS",
                "Configuration with key '" +
                    request.key() +
                    "' already exists. Use PUT to update.",
                null
            );
        }

        ConfigurationDocument doc = ConfigurationDocument.builder()
            .key(request.key())
            .value(request.value())
            .description(request.description())
            .namespace(request.namespace())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        doc = repository.save(doc);

        log.info("Configuration created: {}", request.key());

        broadcastService.broadcast(ConfigurationEvent.created(doc));

        return Result.success(toResponse(doc));
    }

    public Result<ConfigurationResponse> findByKey(String key) {
        return repository
            .findByKey(key)
            .map(doc -> Result.success(toResponse(doc)))
            .orElseGet(() ->
                Result.error(
                    "CONFIG_NOT_FOUND",
                    "Configuration with key '" + key + "' not found",
                    null
                )
            );
    }

    public Result<PageResult<ConfigurationResponse>> findAll(
        String namespace,
        int page,
        int size
    ) {
        PageRequest pageRequest = PageRequest.of(
            page,
            size,
            Sort.by("key").ascending()
        );

        Page<ConfigurationDocument> docs;
        if (namespace != null && !namespace.isBlank()) {
            docs = repository.findByNamespace(namespace, pageRequest);
        } else {
            docs = repository.findAll(pageRequest);
        }

        List<ConfigurationResponse> responses = docs
            .getContent()
            .stream()
            .map(this::toResponse)
            .toList();

        PageResult<ConfigurationResponse> pageResult = PageResult.of(
            responses,
            page,
            size,
            docs.getTotalElements()
        );

        return Result.success(pageResult);
    }

    public Result<ConfigurationResponse> update(
        String key,
        UpdateConfigurationRequest request
    ) {
        ConfigurationDocument doc = repository.findByKey(key).orElse(null);
        if (doc == null) {
            return Result.error(
                "CONFIG_NOT_FOUND",
                "Configuration with key '" + key + "' not found",
                null
            );
        }

        if (request.value() != null) {
            doc.setValue(request.value());
        }
        if (request.description() != null) {
            doc.setDescription(request.description());
        }
        doc.setUpdatedAt(Instant.now());

        doc = repository.save(doc);
        log.info("Configuration updated: {}", key);

        broadcastService.broadcast(ConfigurationEvent.updated(doc));

        return Result.success(toResponse(doc));
    }

    public Result<Void> delete(String key) {
        ConfigurationDocument doc = repository.findByKey(key).orElse(null);
        if (doc == null) {
            return Result.error(
                "CONFIG_NOT_FOUND",
                "Configuration with key '" + key + "' not found",
                null
            );
        }

        repository.delete(doc);
        log.info("Configuration deleted: {}", key);

        broadcastService.broadcast(ConfigurationEvent.deleted(key));

        return Result.success();
    }

    private ConfigurationResponse toResponse(ConfigurationDocument doc) {
        return new ConfigurationResponse(
            doc.getId(),
            doc.getKey(),
            doc.getValue(),
            doc.getDescription(),
            doc.getNamespace(),
            doc.getCreatedAt(),
            doc.getUpdatedAt()
        );
    }
}
