package com.prolog.prologbackend.Member.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(title = "MemberRequest : 비밀번호 재발급 DTO")
@Getter
public class PasswordUpdateDto {
    @Schema(description = "정보 확인에 사용될 email", example = "kimLeeChoi@prologmail.com")
    @Email
    private String email;
    @Schema(description = "회원의 새로운 password", example = "password1234")
    @Size(min=8, max=12, message="비밀번호는 최소 8자리, 최대 12자리로 작성해야 합니다.")
    private String password;
}
