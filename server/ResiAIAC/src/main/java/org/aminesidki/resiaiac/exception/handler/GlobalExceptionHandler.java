package org.aminesidki.resiaiac.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.aminesidki.resiaiac.dto.response.ErrorResponse;
import org.aminesidki.resiaiac.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex){
        ErrorResponse error = new ErrorResponse(HttpStatus.NOT_FOUND.value(),ex.getMessage(), LocalDateTime.now());
        log.warn(error.message());
        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),ex.getBindingResult().getFieldErrors().toString(), LocalDateTime.now());
        log.warn(error.message());
        return ResponseEntity.status(error.status()).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex){
        log.error("An unhandled exception occurred", ex);
        ErrorResponse error = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Something went wrong.", LocalDateTime.now());
        return ResponseEntity.status(error.status()).body(error);
    }
}
