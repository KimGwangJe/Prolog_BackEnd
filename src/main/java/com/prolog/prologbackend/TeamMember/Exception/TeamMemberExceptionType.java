package com.prolog.prologbackend.TeamMember.Exception;

import com.prolog.prologbackend.Exception.ExceptionType;

public enum TeamMemberExceptionType implements ExceptionType {
    NOT_FOUND(404, "조건에 맞는 팀멤버가 없습니다"),
    BAD_REQUEST(400, "유효한 값의 역할이 아닙니다"),
    CONFLICT(409, "이미 존재하는 팀멤버입니다"),
    FORBIDDEN(403, "권한 없는 요청입니다");

    private int errorCode;
    private String errorMessage;

    TeamMemberExceptionType(int errorCode, String errorMessage){
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
