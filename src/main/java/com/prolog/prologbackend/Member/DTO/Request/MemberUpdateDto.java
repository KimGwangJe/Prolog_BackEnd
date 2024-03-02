package com.prolog.prologbackend.Member.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberUpdateDto {
    @Email
    private String email;
    @NotBlank
    @Size(min=8, max=20, message="비밀번호는 최소 8자리, 최대 12자리로 작성해야 합니다.")
    private String password;
    @NotBlank
    @Size(max=20, message="닉네임은 20자 미만으로 작성해야 합니다.")
    private String nickname;
}
