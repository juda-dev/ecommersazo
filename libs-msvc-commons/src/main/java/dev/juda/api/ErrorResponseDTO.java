package dev.juda.api;

import java.time.Instant;
import java.util.List;

public record ErrorResponseDTO(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        String traceId,
        List<FieldViolation> details
) {
    public record FieldViolation(String field, String message){}

    public static ErrorResponseDTO of(int status, String error, String message, String path, String traceId) {
        return new ErrorResponseDTO(Instant.now(), status, error, message, path, traceId, null);
    }

    public static ErrorResponseDTO withDetails(int status, String error, String message, String path, String traceId, List<FieldViolation> details) {
        return new ErrorResponseDTO(Instant.now(), status, error, message, path, traceId, details);
    }
}
