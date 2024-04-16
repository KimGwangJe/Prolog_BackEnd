package com.prolog.prologbackend.TeamMember.Service;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.TeamMember.Domain.Part;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import com.prolog.prologbackend.TeamMember.Exception.TeamMemberExceptionType;
import com.prolog.prologbackend.TeamMember.Repository.TeamMemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    /**
     * 필요한 정보를 받아 새 팀멤버 등록
     *
     * @param project : 소속 프로젝트 정보
     * @param member : 등록할 회원 정보
     * @param parts : 프로젝트 내 역할 목록
     */
    @Transactional
    public void createTeamMember(Project project, Member member, List<Part> parts) {
        teamMemberRepository.findByMemberAndProject(member,project)
                .ifPresent( t -> { throw new BusinessLogicException(TeamMemberExceptionType.CONFLICT); });

        String part = parts.stream().map(Part::toString).collect(Collectors.joining(","));

        TeamMember teamMember = TeamMember.builder()
                .part(part).member(member).project(project)
                .build();

        teamMemberRepository.save(teamMember);
    }

    /**
     * 팀멤버의 id를 받아 해당 팀멤버의 정보 삭제
     *
     * @param member : 요청보낸 사용자의 정보
     * @param teamId : 삭제를 원하는 팀멤버의 id
     */
    @Transactional
    public void removeTeamMember(Member member, Long teamId){
        TeamMember teamMember = getEntityById(teamId);

        if(teamMember.getMember().getId() != member.getId()) {
            getEntityByMemberAndProject(member, teamMember.getProject());
        }

        teamMemberRepository.delete(teamMember);
    }

    /**
     * 팀멤버 id 목록에 해당하는 엔티티 삭제
     * : 회원 탈퇴 시 호출
     *
     * @param teamMembersIds : 삭제할 팀멤버의 id 목록
     */
    @Transactional
    public void removeTeamMember(List<Long> teamMembersIds){
        teamMemberRepository.deleteAllByIdInBatch(teamMembersIds);
    }

    /**
     * 프로젝트 정보와 일치하는 모든 팀멤버의 엔티티를 반환
     * : 프로젝트 상세 정보 조회 시 호출
     *
     * @param project : 프로젝트 기준으로 팀멤버 조회
     * @return : 조회된 팀멤버 엔티티의 목록 반환
     */
    public List<TeamMember> getListByProject(Project project){
        return teamMemberRepository.findAllByProject(project).orElseThrow(
                () -> { throw new BusinessLogicException(TeamMemberExceptionType.NOT_FOUND); }
        );
    }

    /**
     * 회원 정보와 일치하는 모든 팀멤버의 엔티티를 반환
     * : 마이페이지 프로젝트 목록 조회 시 호출
     *
     * @param member : 회원 기준으로 팀멤버 조회
     * @return : 조회된 팀멤버 엔티티의 목록 반환
     */
    public List<TeamMember> getListByMember(Member member){
        return teamMemberRepository.findAllByMember(member);
    }

    /**
     * 특정 회원의 특정 프로젝트와 일치하는 팀멤버 엔티티 조회
     * : 특정 회원의 일지 목록 조회 시 호출
     *
     * @param member : 회원 기준으로 팀멤버 조회
     * @param project : 프로젝트 기준으로 팀멤버 조회
     */
    public void getEntityByMemberAndProject(Member member, Project project){
        String part = teamMemberRepository.findByMemberAndProject(member, project)
                .orElseThrow(() -> {
                    throw new BusinessLogicException(TeamMemberExceptionType.FORBIDDEN);
                }).getPart();

        if (!Arrays.stream(part.split(",")).toList().contains(Part.Leader.toString()))
            throw new BusinessLogicException(TeamMemberExceptionType.FORBIDDEN);
    }

    /**
     * 팀멤버 ID로 엔티티 조회
     *
     * @param teamMemberId : 조회할 팀멤버 id
     * @return : 조회된 팀멤버 엔티티 반환
     */
    public TeamMember getEntityById(Long teamMemberId){
        return teamMemberRepository.findById(teamMemberId).orElseThrow(() -> {
            throw new BusinessLogicException(TeamMemberExceptionType.NOT_FOUND);
        });
    }
}
