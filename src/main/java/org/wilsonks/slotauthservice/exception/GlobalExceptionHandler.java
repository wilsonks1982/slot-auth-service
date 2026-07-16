package org.wilsonks.slotauthservice.exception;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.wilsonks.slotauthservice.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {




    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                new ErrorResponse(405, java.time.Instant.now(), "Method Not Allowed: " + ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(InvalidPinException.class)
    public ResponseEntity<?> handleInvalidPinException(InvalidPinException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ErrorResponse(401, java.time.Instant.now(), "Invalid PIN: " + ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictException(ConflictException ex, HttpServletRequest request) {
        return ResponseEntity.status(409).body(
                new ErrorResponse(409, java.time.Instant.now(), "Data Conflict: " + ex.getMessage(), request.getRequestURI())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        return ResponseEntity.status(409).body(
                new ErrorResponse(409, java.time.Instant.now(), "Data integrity violation: " + ex.getMessage(), request.getRequestURI())
        );
    }

    //Method Argument Not Valid - This exception is thrown when a method argument annotated with @Valid fails validation. It typically occurs in controller methods when the request body does not meet the validation constraints defined in the DTO.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        return ResponseEntity.status(400).body(
                new ErrorResponse(400, java.time.Instant.now(), "Validation Error: " + ex.getMessage(), request.getRequestURI())
        );
    }

    //Method Argument Type Mismatch - This exception is thrown when a method argument cannot be converted to the expected type. It typically occurs in controller methods when a request parameter or path variable cannot be converted to the required type (e.g., trying to convert a string to an integer).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatchException(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return ResponseEntity.status(400).body(
                new ErrorResponse(400, java.time.Instant.now(), "Type Mismatch Error: " + ex.getMessage(), request.getRequestURI())
        );
    }

    //Http Message Not Readable - This exception is thrown when the HTTP message (usually the request body) cannot be read or parsed. It typically occurs in controller methods when the request body is malformed or cannot be converted to the expected object type (e.g., invalid JSON).
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        return ResponseEntity.status(400).body(
                new ErrorResponse(400, java.time.Instant.now(), "Malformed JSON request: " + ex.getMessage(), request.getRequestURI())
        );
    }

    //Handler Method Validation Exception - This exception is thrown when a method parameter annotated with validation annotations (e.g., @NotNull, @Size) fails validation. It typically occurs in controller methods when the request parameters do not meet the validation constraints defined in the method signature.
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpServletRequest request) {
        return ResponseEntity.status(400).body(
                new ErrorResponse(400, java.time.Instant.now(), "Validation Error: " + ex.getMessage(), request.getRequestURI())
        );
    }

    //Generic Exception Handler - This is a catch-all exception handler that handles any unhandled exceptions that may occur in the application. It provides a generic error response with a 500 Internal Server Error status code.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(500).body(
                new ErrorResponse(500, java.time.Instant.now(), "Internal Server Error: " + ex.getMessage(), request.getRequestURI())
        );
    }


}
