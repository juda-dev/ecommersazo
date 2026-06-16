package dev.juda.error;

import dev.juda.result.Result;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends BaseException {

    private final transient List<Result.ValidationError> validationErrors;

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message, 400);
        this.validationErrors = null;
    }

    public ValidationException(String message, List<Result.ValidationError> errors) {
        super("VALIDATION_ERROR", message, 400);
        this.validationErrors = errors;
    }

    @Override
    public ErrorResponse toErrorResponse(String path) {
        return ErrorResponse.of(
                getErrorCode(),
                getMessage(),
                path,
                validationErrors
        );
    }
}
