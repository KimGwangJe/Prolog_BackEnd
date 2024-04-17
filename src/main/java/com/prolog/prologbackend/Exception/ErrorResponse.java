package com.prolog.prologbackend.Exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(title = "ErrorResponse : 에러 정보 DTO")
@Getter
public class ErrorResponse {
    @Schema(description = "에러 코드", example = "400")
    private int errorCode;
    @Schema(description = "에러 메시지", example = "String")
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
