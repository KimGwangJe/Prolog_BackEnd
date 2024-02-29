package com.prolog.prologbackend.TeamMember.Domain;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.TeamMember.Exception.TeamMemberExceptionType;

public enum Part {
    Backend, Frontend, Designer, Devops, Leader;

    public static Part of(String value){
        for(Part part : Part.values()){
            if(value.equals(part.toString())) return part;
        }
        throw new BusinessLogicException(TeamMemberExceptionType.BAD_REQUEST);
    }
}
