package com.prolog.prologbackend.Security.ExceptionType;

import com.prolog.prologbackend.Exception.ExceptionType;
import lombok.Getter;

@Getter
public enum SecurityExceptionType implements ExceptionType {
    //인증 예외
    BAD_CREDENTIALS(401, "비밀번호가 일치하지 않습니다."),
    NOT_FOUND(404,"일치하는 회원이 없습니다."),
    DISABLED(422,"이메일을 인증해주세요."),
    LOCKED(423,"탈퇴한 회원입니다."),
    //인가 예외
    EXPIRED_JWT(401,"만료된 토큰입니다."),
    MALFORMED_JWT(401, "잘못된 토큰입니다."),
    BAD_REQUEST_JWT(401, "올바르지 않은 토큰입니다."),
    UNAUTHORIZED(401, "일치하지 않은 토큰입니다.");

    private int errorCode;
    private String errorMessage;

    SecurityExceptionType(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
