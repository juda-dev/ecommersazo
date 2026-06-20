package paymentservice.payment.shared.exception;

import dev.juda.error.ErrorResponse;
import dev.juda.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        var errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new Result.ValidationError(
                        fe.getField(), fe.getDefaultMessage())).toList();
        return ResponseEntity.badRequest().body(
                ErrorResponse.of("VALIDATION_ERROR", "Validation failed",
                        request.getRequestURI(), errors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex, HttpServletRequest request) {
        log.error("Unhandled: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError()
                .body(ErrorResponse.internalError(ex.getMessage(), request.getRequestURI()));
    }
}