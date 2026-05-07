package dev.juda.libs_msvc_commons.domain.dto.response;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path,
    String traceId,
    List<FieldViolation> details
) {
    public record FieldViolation(String field, String message) {}
}
