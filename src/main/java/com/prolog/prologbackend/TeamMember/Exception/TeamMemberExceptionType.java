package com.prolog.prologbackend.TeamMember.Exception;

import com.prolog.prologbackend.Exception.ExceptionType;

public enum TeamMemberExceptionType implements ExceptionType {
    NOT_FOUND(404, "조건에 맞는 팀멤버가 없습니다.");

    private int errorCode;
    private String errorMessage;

    TeamMemberExceptionType(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() {
        return 0;
    }

    @Override
    public String getErrorMessage() {
        return null;
    }
}
