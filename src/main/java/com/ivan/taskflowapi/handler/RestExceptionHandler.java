package com.ivan.taskflowapi.handler;

import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<RestErrorMessage> resourceNotFoundException(ResourceNotFoundException exception) {
        RestErrorMessage message = RestErrorMessage.builder()
                .message(exception.getMessage())
                .error("Not found")
                .status(HttpStatus.NOT_FOUND.value())
                .timeStamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @ExceptionHandler(ForbiddenException.class)
    private ResponseEntity<RestErrorMessage> forbiddenException(ForbiddenException exception) {
        RestErrorMessage message = RestErrorMessage.builder()
                .message(exception.getMessage())
                .error("Unauthorized")
                .status(HttpStatus.FORBIDDEN.value())
                .timeStamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(message);
    }

    @ExceptionHandler(BadRequestException.class)
    private ResponseEntity<RestErrorMessage> badRequestException(BadRequestException exception) {
        RestErrorMessage message = RestErrorMessage.builder()
                .message(exception.getMessage())
                .error("BadRequest")
                .status(HttpStatus.BAD_REQUEST.value())
                .timeStamp(Instant.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    private ResponseEntity<RestErrorMessage> methodArgumentNotValid(MethodArgumentNotValidException exception) {

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getFieldErrors().forEach((error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();

            errors.put(fieldName, errorMessage);
        }));

        RestErrorMessage message = RestErrorMessage.builder()
                .message(exception.getMessage())
                .error("Bad Request")
                .fields(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .timeStamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }
}
