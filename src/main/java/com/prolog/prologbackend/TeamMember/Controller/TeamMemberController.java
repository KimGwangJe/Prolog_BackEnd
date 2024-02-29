package com.prolog.prologbackend.TeamMember.Controller;

import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.TeamMember.Service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teamMembers")
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @PostMapping
    public ResponseEntity createTeamMember(@AuthenticationPrincipal Member member,
                                           @RequestParam String part,
                                           @RequestParam Long projectId){
        teamMemberService.createTeamMember(part, member, projectId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
