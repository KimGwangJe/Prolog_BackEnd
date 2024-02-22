package com.prolog.prologbackend.Member.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MemberJoinDto {
    /**
     * 비밀번호 확인은 프론트에서 수행
     * service에서 중복 여부 검사 : email, phone
     */
    @Email
    private String email;
    @NotBlank
    @Size(min=8, max=20, message="비밀번호는 최소 8자리, 최대 20자리로 작성해야 합니다.")
    private String password;
    @NotBlank
    @Size(max=20, message="닉네임은 20자 미만으로 작성해야 합니다.")
    private String nickname;
    @NotBlank
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "핸드폰 번호는 11자리 숫자와 '-'로 구성되어야 합니다.")
    private String phone;

}
