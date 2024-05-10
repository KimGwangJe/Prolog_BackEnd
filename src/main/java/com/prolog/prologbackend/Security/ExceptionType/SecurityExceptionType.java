package com.prolog.prologbackend.Security.ExceptionType;

import com.prolog.prologbackend.Exception.ExceptionType;
import lombok.Getter;

@Getter
public enum SecurityExceptionType implements ExceptionType {
    //인증 예외
    BAD_REQUEST(400,"잘못된 요청입니다. 확인해주세요."),
    BAD_CREDENTIALS(401, "비밀번호가 일치하지 않습니다."),
    METHOD_NOT_ALLOWED(405, "지원하지 않는 메서드 입니다."),
    NOT_FOUND(404,"일치하는 회원이 없습니다."),
    DISABLED(422,"이메일을 인증해주세요."),
    LOCKED(423,"탈퇴한 회원입니다."),
    //인가 예외
    JWT_BAD_REQUEST(400, "올바르지 않은 토큰입니다."),
    JWT_EXPIRED(401,"만료된 토큰입니다."),
    JWT_UNAUTHORIZED(401, "일치하지 않은 토큰입니다."),
    JWT_FORBIDDEN(403, "사용할 수 없는 토큰입니다.");

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
