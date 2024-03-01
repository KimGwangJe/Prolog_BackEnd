package com.prolog.prologbackend.TeamMember.Controller;

import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.TeamMember.DTO.Request.CreateTeamMemberDto;
import com.prolog.prologbackend.TeamMember.Service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teamMembers")
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @PostMapping
    public ResponseEntity createTeamMember(@AuthenticationPrincipal Member member,
                                           @RequestBody CreateTeamMemberDto createTeamMemberDto){
        teamMemberService.createTeamMember(createTeamMemberDto.getParts(), member, createTeamMemberDto.getProjectId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{team-id}")
    public ResponseEntity removeTeamMember(@AuthenticationPrincipal Member member,
                                           @PathVariable("team-id") Long teamId){
        teamMemberService.removeTeamMember(member, teamId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
