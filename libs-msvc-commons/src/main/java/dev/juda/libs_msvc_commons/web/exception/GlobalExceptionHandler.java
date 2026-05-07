package dev.juda.libs_msvc_commons.web.exception;

import dev.juda.libs_msvc_commons.domain.dto.response.ErrorResponse;
import dev.juda.libs_msvc_commons.domain.exception.BadRequestException;
import dev.juda.libs_msvc_commons.domain.exception.ConflictException;
import dev.juda.libs_msvc_commons.domain.exception.NotFoundException;
import dev.juda.libs_msvc_commons.domain.exception.TimeoutException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(
        GlobalExceptionHandler.class
    );

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(
        NotFoundException ex,
        HttpServletRequest req
    ) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handle(
        ConflictException ex,
        HttpServletRequest req
    ) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handle(
        BadRequestException ex,
        HttpServletRequest req
    ) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(TimeoutException.class)
    public ResponseEntity<ErrorResponse> handle(
        TimeoutException ex,
        HttpServletRequest req
    ) {
        return build(
            HttpStatus.GATEWAY_TIMEOUT,
            ex.getMessage(),
            req,
            List.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(
        MethodArgumentNotValidException ex,
        HttpServletRequest req
    ) {
        var details = ex
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .map(fe ->
                new ErrorResponse.FieldViolation(
                    fe.getField(),
                    Optional.ofNullable(fe.getDefaultMessage()).orElse(
                        "Invalid value"
                    )
                )
            )
            .toList();
        return build(HttpStatus.BAD_REQUEST, "Validation failed", req, details);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(
        Exception ex,
        HttpServletRequest req
    ) {
        log.error("Unexpected error", ex);
        return build(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Internal server error",
            req,
            List.of()
        );
    }

    private ResponseEntity<ErrorResponse> build(
        HttpStatus status,
        String message,
        HttpServletRequest req,
        List<ErrorResponse.FieldViolation> details
    ) {
        return ResponseEntity.status(status).body(
            new ErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                (String) req.getAttribute("traceId"),
                details
            )
        );
    }
}
