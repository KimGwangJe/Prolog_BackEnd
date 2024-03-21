package com.prolog.prologbackend.TeamMember.Controller;

import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.TeamMember.DTO.Request.CreateTeamMemberDto;
import com.prolog.prologbackend.TeamMember.Service.TeamMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name="팀멤버 관련 API", description = "팀멤버와 관련된 API 문서입니다.")
@RestController
@RequiredArgsConstructor
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @Operation(summary = "팀멤버 등록 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created : 팀멤버 추가 성공"),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 엔티티",
                    content = @Content(schema = @Schema(implementation=Void.class))),
            @ApiResponse(responseCode = "409", description = "Conflict : 이미 존재하는 팀멤버",
                    content = @Content(schema = @Schema(implementation=Void.class)))
    })
    @PostMapping("/teamMembers")
    public ResponseEntity createTeamMember(@Valid @RequestBody CreateTeamMemberDto createTeamMemberDto){
        teamMemberService.createTeamMember(createTeamMemberDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "팀멤버 삭제 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "No Content : 팀멤버 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "Forbidden : 권한 없는 사용자",
                    content = @Content(schema = @Schema(implementation=Void.class))),
            @ApiResponse(responseCode = "404", description = "Not Found : 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation=Void.class)))
    })
    @Schema(description = "Path Variable. 삭제할 팀멤버 id", example = "2")
    @DeleteMapping("/api/teamMembers/{team-id}")
    public ResponseEntity removeTeamMember(@AuthenticationPrincipal Member member,
                                           @PathVariable("team-id") Long teamId){
        teamMemberService.removeTeamMember(member, teamId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
