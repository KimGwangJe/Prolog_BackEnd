package com.prolog.prologbackend.Exception;

import com.prolog.prologbackend.Project.ExceptionType.ProjectExceptionType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity handleBusinessLogicExceptions(BusinessLogicException e) {
        return ResponseEntity.status(e.getExceptionType().getErrorCode())
                .body(ErrorResponse.of(e.getExceptionType()));
    }

    //Email 형식이 올바르지 않음
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleValidationEmailExceptions(MethodArgumentNotValidException e) {
        return ResponseEntity.status(ProjectExceptionType.EMAIL_FORMAT_ERROR.getErrorCode())
                .body(ErrorResponse.of(ProjectExceptionType.EMAIL_FORMAT_ERROR));
    }

    //Date Format이 제대로 오지 않았을
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ResponseEntity.status(ProjectExceptionType.DATE_FORMAT_ERROR.getErrorCode())
                .body(ErrorResponse.of(ProjectExceptionType.DATE_FORMAT_ERROR));
    }
}
