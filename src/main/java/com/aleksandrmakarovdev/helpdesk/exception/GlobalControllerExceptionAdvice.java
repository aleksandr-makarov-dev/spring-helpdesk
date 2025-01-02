package com.aleksandrmakarovdev.helpdesk.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Hidden
public class GlobalControllerExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(GlobalControllerExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ProblemDetail> handleException(Exception e) {

        String message = e.getMessage();

        if (e instanceof MethodArgumentNotValidException ex) {
            message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }

        log.error(message, e);

        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST, message);

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }
}
