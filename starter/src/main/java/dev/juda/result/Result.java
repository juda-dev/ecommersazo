package dev.juda.result;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.function.Function;

import dev.juda.error.ErrorResponse;

public sealed interface Result<T> permits Result.Success, Result.Error {

    static <T> Result<T> success(T value) {
        return new Success<>(value);
    }

    static <T> Result<T> success() {
        return new Success<>(null);
    }

    static <T> Result<T> error(String code, String message, List<ValidationError> errors) {
        return new Error<>(code, message, errors);
    }

    boolean isSuccess();
    boolean isError();
    T getValue();
    Error<T> getError();
    @SuppressWarnings("unchecked")
    default <U> Result<U> map(Function<? super T, ? extends U> mapper) {
        if (this instanceof Success<T>(T value)) {
            return Result.success(mapper.apply(value));
        }
        return (Result<U>) this;
    }

    @SuppressWarnings("unchecked")
    default <U> Result<U> flatMap(Function<? super T, Result<U>> mapper) {
        if (this instanceof Success<T>(T value)) {
            return mapper.apply(value);
        }
        return (Result<U>) this;
    }

    record Success<T>(T value) implements Result<T> {

        @Override
        public boolean isSuccess() { return true; }

        @Override
        public boolean isError() { return false; }

        @Override
        public T getValue() { return value; }

        @Override
        public Error<T> getError() {
            return new Error<T>( "null", "null", List.of());
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record Error<T>(
                      String code,
                      String message,
                      @JsonProperty("errors") List<ValidationError> errors
    ) implements Result<T> {

        @Override
        public boolean isSuccess() { return false; }

        @Override
        public boolean isError() { return true; }

        @Override
        public T getValue() {
            throw new IllegalStateException("Cannot get value from an Error result");
        }

        @Override
        public Error<T> getError() { return this; } // ✅ Actualizado

        public ErrorResponse toErrorResponse(String path) {
            return ErrorResponse.of(code, message, path, errors);
        }

        public ErrorResponse toErrorResponse() {
            return toErrorResponse(null);
        }

        public int httpStatus() {
            if (code == null) return 500;
            if (code.contains("NOT_FOUND")) return 404;
            if (code.contains("VALIDATION")) return 400;
            if (code.contains("UNAUTHORIZED") || code.contains("TOKEN_EXPIRED")) return 401;
            if (code.contains("FORBIDDEN")) return 403;
            if (code.contains("CONFLICT") || code.contains("ALREADY_EXISTS")) return 409;
            return 500;
        }
    }

    record ValidationError(String field, String message) {}
}