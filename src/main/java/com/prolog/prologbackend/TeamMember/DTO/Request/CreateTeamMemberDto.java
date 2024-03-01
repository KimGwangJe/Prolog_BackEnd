package com.prolog.prologbackend.TeamMember.DTO.Request;

import com.prolog.prologbackend.TeamMember.Domain.Part;
import lombok.Getter;

import java.util.List;

@Getter
public class CreateTeamMemberDto {
    private List<Part> parts;
    private Long projectId;
}
