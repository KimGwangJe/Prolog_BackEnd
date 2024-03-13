package com.prolog.prologbackend.Member.DTO.Response;

import com.prolog.prologbackend.Member.Domain.Member;
import lombok.Getter;

@Getter
public class SimpleMemberDto {
    private String nickName;
    private String profileImage;
    private String email;
    private String phone;
    private boolean isBasic;

    public static SimpleMemberDto of(Member member){
        SimpleMemberDto memberDto = new SimpleMemberDto();

        memberDto.nickName = member.getNickname();
        memberDto.profileImage = member.getProfileImage();
        memberDto.email = member.getEmail();
        memberDto.phone = member.getPhone();
        memberDto.isBasic = member.getStatus().isBasicMember();

        return memberDto;
    }
}
