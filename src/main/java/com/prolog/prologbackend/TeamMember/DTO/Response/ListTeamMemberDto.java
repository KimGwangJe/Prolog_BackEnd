package com.prolog.prologbackend.TeamMember.DTO.Response;

import com.prolog.prologbackend.TeamMember.Domain.Part;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import lombok.Getter;

/**
 * 프로젝트 상세 정보 조회, 팀멤버 일지 목록 조회 요청에서 사용
 */
@Getter
public class ListTeamMemberDto {
    private Long id;
    private String nickName;
    private Part part;
    private String profileImage;

    public static ListTeamMemberDto of(TeamMember teamMember){
        ListTeamMemberDto listTeamMemberDto = new ListTeamMemberDto();

        listTeamMemberDto.id = teamMember.getId();
        listTeamMemberDto.nickName = teamMember.getMember().getNickname();
        listTeamMemberDto.part = teamMember.getPart();
        listTeamMemberDto.profileImage = teamMember.getMember().getProfileImage();

        return listTeamMemberDto;
    }
}
