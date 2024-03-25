package com.prolog.prologbackend.Member.ExceptionType;

import com.prolog.prologbackend.Exception.ExceptionType;

public enum MemberExceptionType implements ExceptionType {
    BAD_REQUEST(400,"잘못된 회원 정보입니다"),
    NOT_FOUND(404,"존재하지 않는 회원입니다"),
    CONFLICT(409,"이미 존재하는 회원입니다"),
    //회원 정보 찾기
    CODE_BAD_REQUEST(400, "잘못된 인증번호 입니다"),
    CODE_UNAUTHORIZED(401,"이메일을 인증해주세요"),
    CODE_NOT_FOUND(404, "인증번호를 발급해주세요"),
    VERIFICATION_CONFLICT(409, "이미 인증 완료된 회원입니다");

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
