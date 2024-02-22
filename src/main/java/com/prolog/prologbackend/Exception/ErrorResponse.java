package com.prolog.prologbackend.Exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private int errorCode;
    private String errorMessage;

    private ErrorResponse(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static ErrorResponse of(ExceptionType e){
        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }
    public static ErrorResponse of(int errorCode, String errorMessage){
        return new ErrorResponse(errorCode, errorMessage);
    }
}
