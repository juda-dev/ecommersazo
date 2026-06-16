package dev.juda.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.juda.result.Result;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
        String timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        List<FieldError> errors
) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FieldError(String field, String message) {}

    public static ErrorResponse of(
            String code,
            String message,
            String path,
            List<Result.ValidationError> validationErrors
    ) {
        int status = httpStatusFromCode(code);
        String errorPhrase = httpPhrase(status);

        List<FieldError> fieldErrors = null;
        if (validationErrors != null && !validationErrors.isEmpty()) {
            fieldErrors = validationErrors.stream()
                    .map(ve -> new FieldError(ve.field(), ve.message()))
                    .toList();
        }

        return new ErrorResponse(
                Instant.now().toString(),
                status,
                errorPhrase,
                code,
                message,
                path,
                fieldErrors
        );
    }

    public static ErrorResponse internalError(String message, String path) {
        return new ErrorResponse(
                Instant.now().toString(),
                500,
                "Internal Server Error",
                "INTERNAL_ERROR",
                message,
                path,
                null
        );
    }

    private static int httpStatusFromCode(String code) {
        if (code == null) return 500;
        if (code.contains("NOT_FOUND")) return 404;
        if (code.contains("VALIDATION")) return 400;
        if (code.contains("UNAUTHORIZED") || code.contains("TOKEN_EXPIRED")) return 401;
        if (code.contains("FORBIDDEN")) return 403;
        if (code.contains("CONFLICT") || code.contains("ALREADY_EXISTS")) return 409;
        return 500;
    }

    private static String httpPhrase(int status) {
        return switch (status) {
            case 400 -> "Bad Request";
            case 401 -> "Unauthorized";
            case 403 -> "Forbidden";
            case 404 -> "Not Found";
            case 409 -> "Conflict";
            case 422 -> "Unprocessable Entity";
            case 429 -> "Too Many Requests";
            case 500 -> "Internal Server Error";
            case 503 -> "Service Unavailable";
            default  -> "Error";
        };
    }
}
