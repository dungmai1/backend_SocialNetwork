package SocialNetwork.SocialNetwork.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bắt lỗi validate cho @RequestBody không hợp lệ
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    // // Bắt lỗi validate cho @RequestParam và @PathVariable
    // @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    // public ResponseEntity<Map<String, String>> handleConstraintViolationException(
    //         jakarta.validation.ConstraintViolationException ex) {
    //     Map<String, String> errors = new HashMap<>();
    //     ex.getConstraintViolations().forEach(violation -> {
    //         String fieldName = violation.getPropertyPath().toString();
    //         String message = violation.getMessage();
    //         errors.put(fieldName, message);
    //     });
    //     return ResponseEntity.badRequest().body(errors);
    // }

    // // Bắt lỗi custom (ví dụ bạn tự throw)
    // @ExceptionHandler(CustomException.class)
    // public ResponseEntity<Map<String, String>> handleCustomException(CustomException ex) {
    //     Map<String, String> error = new HashMap<>();
    //     error.put("error", ex.getMessage());
    //     return ResponseEntity.badRequest().body(error);
    // }
}
