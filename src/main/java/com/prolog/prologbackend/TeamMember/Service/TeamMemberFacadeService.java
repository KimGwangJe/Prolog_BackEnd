package com.prolog.prologbackend.TeamMember.Service;

import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Service.MemberService;
import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.Project.Service.ProjectService;
import com.prolog.prologbackend.TeamMember.DTO.Request.CreateTeamMemberDto;
import com.prolog.prologbackend.TeamMember.Domain.Part;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMemberFacadeService {
    private final TeamMemberService teamMemberService;
    private final MemberService memberService;
    private final ProjectService projectService;

    /**
     * 필요한 정보를 받아 새 팀멤버 등록
     *
     * @param teamMemberDto : 팀멤버 생성에 필요한 정보가 담긴 DTO
     */
    @Transactional
    public void createTeamMember(CreateTeamMemberDto teamMemberDto){
        Project project = projectService.getProject(teamMemberDto.getProjectId());
        Member member = memberService.getMember(teamMemberDto.getMemberId());
        teamMemberService.getEntityConflictByMemberAndProject(member,project);

        String part = teamMemberDto.getParts().stream().map(Part::toString).collect(Collectors.joining(","));

        TeamMember teamMember = TeamMember.builder()
                .part(part).member(member).project(project)
                .build();

        teamMemberService.createTeamMember(teamMember);
    }

    /**
     * 팀멤버의 id를 받아 해당 팀멤버의 정보 삭제
     *
     * @param member : 요청보낸 사용자의 정보
     * @param teamId : 삭제를 원하는 팀멤버의 id
     */
    @Transactional
    public void deleteTeamMember(Member member, Long teamId){
        TeamMember teamMember = teamMemberService.getEntityById(teamId);

        if(teamMember.getMember().getId() != member.getId()) {
            teamMemberService.getEntityByMemberAndProject(member, teamMember.getProject());
        }

        teamMemberService.deleteTeamMember(teamMember);
    }
}
