package configurationservice.configuration.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateConfigurationRequest(
    @NotBlank String key,
    @NotNull Object value,
    String description,
    @NotBlank String namespace
) {}
