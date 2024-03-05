package com.prolog.prologbackend.Notes.ExceptionType;

import com.prolog.prologbackend.Exception.ExceptionType;

public enum NotesExceptionType implements ExceptionType {
    NOTES_NOT_FOUND(404,"존재하지 않는 일지입니다."),
    INVALID_INPUT_VALUE(400,"데이터에 null이 포함되어 있습니다."),
    NOTES_DELETE_ERROR(400,"일지 삭제에 실패 하였습니다."),
    NOTES_TYPE_ERROR(400,"일지 타입이 올바르지 않습니다."),
    NOTES_SAVE_ERROR(400,"일지 저장에 실패 하였습니다.");


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
