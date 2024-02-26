package com.prolog.prologbackend.Notes.ExceptionType;

import com.prolog.prologbackend.Exception.ExceptionType;

public enum NotesExceptionType implements ExceptionType {
    NOTES_NOT_FOUND(404,"존재하지 않는 프로젝트입니다.");

    private int errorCode;
    private String errorMessage;

    NotesExceptionType(int errorCode, String errorMessage){
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
