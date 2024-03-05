package com.prolog.prologbackend.Project.ExceptionType;

import com.prolog.prologbackend.Exception.ExceptionType;

public enum ProjectExceptionType implements ExceptionType {
    PROJECT_NOT_FOUND(404,"존재하지 않는 프로젝트입니다."),
    INVALID_INPUT_VALUE(400, "데이터에 null이 포함되어 있습니다."),
    DATE_FORMAT_ERROR(400, "잘못된 날짜 형식입니다."),
    PROJECT_SAVE_ERROR(400, "프로젝트 저장에 실패하였습니다.");

    private int errorCode;
    private String errorMessage;

    ProjectExceptionType(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    @Override
    public int getErrorCode(){
        return errorCode;
    }
    @Override
    public String getErrorMessage(){
        return errorMessage;
    }
}
