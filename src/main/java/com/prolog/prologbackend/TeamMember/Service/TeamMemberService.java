package com.prolog.prologbackend.TeamMember.Service;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.TeamMember.Domain.Part;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import com.prolog.prologbackend.TeamMember.Exception.TeamMemberExceptionType;
import com.prolog.prologbackend.TeamMember.Repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;


    //팀멤버 생성
    public void createTeamMember(TeamMember teamMember) {
        teamMemberRepository.save(teamMember);
    }

    //팀멤버 삭제 : Entity
    public void deleteTeamMember(TeamMember teamMember){
        teamMemberRepository.delete(teamMember);
    }

    //팀멤버 삭제 : Id List
    public void deleteTeamMemberByIds(List<Long> teamMembersIds){
        teamMemberRepository.deleteAllByIdInBatch(teamMembersIds);
    }

    //팀멤버 조회 : Id
    public TeamMember getEntityById(Long teamMemberId){
        return teamMemberRepository.findById(teamMemberId).orElseThrow(() -> {
            throw new BusinessLogicException(TeamMemberExceptionType.NOT_FOUND);
        });
    }

    //팀멤버 List 반환 : Entity (Project)
    public List<TeamMember> getListByProject(Project project){
        return teamMemberRepository.findAllByProject(project).orElseThrow(
                () -> { throw new BusinessLogicException(TeamMemberExceptionType.NOT_FOUND); }
        );
    }

    //팀멤버 List 반환 : Entity (Member)
    public List<TeamMember> getListByMember(Member member){
        return teamMemberRepository.findAllByMember(member);
    }

    //존재하는 팀멤버인지 확인 & 프로젝트의 리더인지 확인 : Entity (Member, Project)
    public void getEntityByMemberAndProject(Member member, Project project){
        String part = teamMemberRepository.findByMemberAndProject(member, project)
                .orElseThrow(() -> {
                    throw new BusinessLogicException(TeamMemberExceptionType.FORBIDDEN);
                }).getPart();

        if (!Arrays.stream(part.split(",")).toList().contains(Part.Leader.toString()))
            throw new BusinessLogicException(TeamMemberExceptionType.FORBIDDEN);
    }

    //이미 존재하는지 확인 : Entity (Member, Project)
    public void getEntityConflictByMemberAndProject(Member member, Project project){
        teamMemberRepository.findByMemberAndProject(member,project)
                .ifPresent( t -> { throw new BusinessLogicException(TeamMemberExceptionType.CONFLICT); });
    }
}
