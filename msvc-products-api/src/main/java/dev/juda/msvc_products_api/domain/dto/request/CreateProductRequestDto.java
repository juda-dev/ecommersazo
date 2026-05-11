package dev.juda.msvc_products_api.domain.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateProductRequestDto(
    @NotBlank String name,
    @NotNull @Min(10) Double price
) {}
