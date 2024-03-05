package com.prolog.prologbackend.Exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.prolog.prologbackend.Project.ExceptionType.ProjectExceptionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity handleBusinessLogicExceptions(BusinessLogicException e) {
        return ResponseEntity.status(e.getExceptionType().getErrorCode())
                .body(ErrorResponse.of(e.getExceptionType()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(final MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        if(e.getCause() instanceof InvalidFormatException){
            InvalidFormatException t = (InvalidFormatException) e.getCause();
            ErrorResponse response = ErrorResponse.of(HttpStatus.BAD_REQUEST.value(),
                    t.getPath().get(0).getFieldName()+" : 잘못된 형식의 값입니다");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(response);
        }
        return ResponseEntity.status(ProjectExceptionType.DATE_FORMAT_ERROR.getErrorCode())
                .body(ErrorResponse.of(ProjectExceptionType.DATE_FORMAT_ERROR));
    }
}
