package com.aleksandrmakarovdev.helpdesk.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Hidden
public class GlobalControllerExceptionAdvice {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ProblemDetail> handleException(Exception e) {

        String message = e.getMessage();

        if (e instanceof MethodArgumentNotValidException ex) {
            message = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        }

        ProblemDetail problemDetail = ProblemDetail
                .forStatusAndDetail(HttpStatus.BAD_REQUEST, message);

        return ResponseEntity.status(problemDetail.getStatus()).body(problemDetail);
    }
}
