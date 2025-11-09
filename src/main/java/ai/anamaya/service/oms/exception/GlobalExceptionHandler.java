package ai.anamaya.service.oms.exception;

import ai.anamaya.service.oms.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format(
                "Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(),
                ex.getName(),
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(NotFoundException ex) {
        ApiResponse<?> response = ApiResponse.<Object>builder()
                .success(false)
                .message(ex.getMessage() != null ? ex.getMessage() : "Data not found")
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        String rootCause = ex.getRootCause() != null ? ex.getRootCause().getMessage() : ex.getMessage();
        String message;

        if (rootCause != null && rootCause.toLowerCase().contains("duplicate")) {
            message = "Duplicate data — one of the fields must be unique";
        } else if (rootCause != null && rootCause.toLowerCase().contains("foreign key")) {
            message = "Invalid reference — related data not found";
        } else {
            message = "Data integrity error";
        }

        ApiResponse<?> response = ApiResponse.<Object>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleConstraintViolation(ConstraintViolationException ex) {
        String rootCause = ex.getMessage();
        String message;

        if (rootCause != null && rootCause.toLowerCase().contains("duplicate")) {
            message = "Duplicate data — one of the fields must be unique";
        } else if (rootCause != null && rootCause.toLowerCase().contains("foreign key")) {
            message = "Invalid reference — related data not found";
        } else {
            message = "Constraint violation";
        }

        ApiResponse<?> response = ApiResponse.<Object>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    Map<String, String> err = new HashMap<>();
                    String fieldName = ((FieldError) error).getField();
                    String message = error.getDefaultMessage();
                    err.put("field", fieldName);
                    err.put("message", message);
                    return err;
                })
                .collect(Collectors.toList());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);

        ApiResponse<Map<String, Object>> apiResponse = ApiResponse.<Map<String, Object>>builder()
                .success(false)
                .message("Validation failed")
                .data(errorResponse)
                .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception ex) {
        ApiResponse<?> response = ApiResponse.<Object>builder()
                .success(false)
                .message(ex.getMessage() != null ? ex.getMessage() : "Internal server error")
                .data(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
