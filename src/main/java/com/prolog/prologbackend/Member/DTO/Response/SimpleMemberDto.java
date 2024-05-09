package com.prolog.prologbackend.Member.DTO.Response;

import com.prolog.prologbackend.Member.Domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(title = "MemberResponse : 초대할 팀원의 등록을 위한 정보 조회 DTO")
@Getter
public class SimpleMemberDto {
    @Schema(description = "회원 id", example = "2")
    private Long id;
    @Schema(description = "회원 닉네임", example = "개발새발")
    private String nickName;
    @Schema(description = "회원 프로필 이미지 링크", example = "profileImageS3Link")
    private String profileImage;
    @Schema(description = "회원 email", example = "kimLeeChoi@prologmail.com")
    private String email;

    public static SimpleMemberDto of(Member member){
        SimpleMemberDto memberDto = new SimpleMemberDto();

        memberDto.id = member.getId();
        memberDto.nickName = member.getNickname();
        memberDto.profileImage = member.getProfileImage();
        memberDto.email = member.getEmail();

        return memberDto;
    }
}
