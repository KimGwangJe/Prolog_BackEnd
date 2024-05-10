package com.prolog.prologbackend.Member.DTO.Response;

import com.prolog.prologbackend.Member.Domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(title = "MemberResponse : 로그인한 사용자 정보 조회 DTO")
@Getter
public class BasicMemberDto {
    @Schema(description = "회원 닉네임", example = "개발새발")
    private String nickName;
    @Schema(description = "회원 프로필 이미지 링크", example = "profileImageS3Link")
    private String profileImage;
    @Schema(description = "회원 email", example = "kimLeeChoi@prologmail.com")
    private String email;
    @Schema(description = "회원 phone number", example = "010-1234-5678")
    private String phone;
    @Schema(description = "일반 회원 여부", example = "true")
    private boolean isBasic;

    public static BasicMemberDto of(Member member){
        BasicMemberDto memberDto = new BasicMemberDto();

        memberDto.nickName = member.getNickname();
        memberDto.profileImage = member.getProfileImage();
        memberDto.email = member.getEmail();
        memberDto.phone = member.getPhone();
        memberDto.isBasic = member.getStatus().isBasicMember();

        return memberDto;
    }
}
