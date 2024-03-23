package com.prolog.prologbackend.Member.DTO.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordUpdateDto {
    @Email
    private String email;
    @Size(min=8, max=12, message="비밀번호는 최소 8자리, 최대 12자리로 작성해야 합니다.")
    private String password;
}
