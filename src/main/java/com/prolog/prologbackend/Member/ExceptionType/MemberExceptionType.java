package com.prolog.prologbackend.Member.ExceptionType;

import com.prolog.prologbackend.Exception.ExceptionType;

public enum MemberExceptionType implements ExceptionType {
    MEMBER_NOT_FOUND(404,"존재하지 않는 회원입니다"),
    MEMBER_CONFLICT(409,"이미 존재하는 회원입니다");

    private int errorCode;
    private String errorMessage;

    MemberExceptionType(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode() { return errorCode; }

    @Override
    public String getErrorMessage() { return errorMessage; }
}
