package com.prolog.prologbackend.Notes.ExceptionType;

import com.prolog.prologbackend.Exception.ExceptionType;

public enum ImageExceptionType implements ExceptionType {
    NOTES_NOT_FOUND(400,"이미지가 null 입니다.");


    private int errorCode;
    private String errorMessage;

    ImageExceptionType(int errorCode, String errorMessage){
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