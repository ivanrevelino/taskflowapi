package com.ivan.taskflowapi.infra.handler;

import com.ivan.taskflowapi.exception.BadRequestException;
import com.ivan.taskflowapi.exception.ResourceNotFoundException;
import com.ivan.taskflowapi.exception.ForbiddenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

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
}
