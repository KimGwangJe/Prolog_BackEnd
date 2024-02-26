package com.prolog.prologbackend.Notes.DTO;


import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Author : Kim
 * Date : 2024-02-16
 * Description : 일지 타입 2가지중 하나를 받습니다.
 * 타입의 이름은 Blog와 ??? 입니다.
*/
public enum NotesType {
    Type1,
    Type2;

    @JsonCreator
    public static NotesType fromTestEnum(String val){
        for(NotesType notesType : NotesType.values()){
            if(notesType.name().equals(val)){
                return notesType;
            }
        }
        throw new IllegalArgumentException("Invalid NotesType: " + val);
    }
}
