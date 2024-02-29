package com.prolog.prologbackend.TeamMember.Service;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import com.prolog.prologbackend.TeamMember.Exception.TeamMemberExceptionType;
import com.prolog.prologbackend.TeamMember.Repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;

    /*
    1) 프로젝트 상세 정보 조회
     * : 프로젝트 id와 일치하는 모든 팀멤버의 엔티티를 반환
     * : 원하는 프로젝트의 팀 멤버 목록이 필요 - 프로젝트 기준 조회
     * -> 프로젝트 생성 시 팀장을 기준으로 팀멤버가 생성
     * -> 프로젝트가 있는데 팀멤버 정보가 하나도 없다는 건 에러이므로 잘못된 요청.
     */
    public List<TeamMember> getListByProject(Project project){
        //
        return teamMemberRepository.findAllByProject(project).orElseThrow(
                () -> { throw new BusinessLogicException(TeamMemberExceptionType.NOT_FOUND); }
        );
    }

    /*
    2) 마이페이지 프로젝트 목록 조회
     * : 회원 id와 일치하는 모든 팀멤버의 엔티티를 반환
     * : 해당 회원의 팀멤버 목록을 조회 - 회원 기준 조회
     * -> 프로젝트가 하나도 없는 회원이 있을 수 있음 (신규 회원의 경우)
     * -> 그래서 따로 에러를 던지지 않음
     */
    public List<TeamMember> getListByMember(Member member){
        return teamMemberRepository.findAllByMember(member);
    }

    /*
    3) 특정 회원의 일지 목록 조회
     * : 특정 회원의 특정 프로젝트와 일치하는 팀멤버 엔티티를 반환
     * : 원하는 회원이 진행하는 특정 프로젝트의 일지 목록을 팀멤버를 이용하여 조회 - 회원 및 프로젝트 기준 조회
     * -> 특정 팀멤버의 일지를 조회하기 위해 사용되는 메서드
     * -> 회원과 프로젝트의 정보로 팀멤버를 조회, 존재하지 않는 경우 잘못된 요청.
     */
    public TeamMember getEntityByMemberAndProject(Member member, Project project){
        return teamMemberRepository.findByMemberAndProject(member, project).orElseThrow(
                () -> { throw new BusinessLogicException(TeamMemberExceptionType.NOT_FOUND); }
        );
    }
}
