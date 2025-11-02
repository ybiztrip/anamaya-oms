package ai.anamaya.service.oms.exception;

import ai.anamaya.service.oms.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

    // ✅ Handle all other exceptions gracefully
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
