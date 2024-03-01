package com.prolog.prologbackend.TeamMember.Domain;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.TeamMember.Exception.TeamMemberExceptionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Part {
    Backend, Frontend, Designer, Devops, Leader;

    public static List<Part> of(String value){
        List<Part> parts = new ArrayList<>();
        List<String> valueList = Arrays.stream(value.split(",")).toList();

        for(String str : valueList) {
            boolean fail = true;
            for (Part part : Part.values()) {
                if (part.toString().equals(str)) {
                    parts.add(part);
                    fail = false;
                    break;
                }
            }
            if(fail)
                throw new BusinessLogicException(TeamMemberExceptionType.BAD_REQUEST);
        }

        return parts;
    }
}
