package com.prolog.prologbackend.Member.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(title = "MemberRequest : 일반 회원 가입 DTO")
@Getter
public class MemberJoinDto {
    @Schema(description = "사용할 email", example = "kimLeeChoi@prologmail.com")
    @Email
    private String email;
    @Schema(description = "사용할 password", example = "password1234")
    @NotBlank
    @Size(min=8, max=12, message="비밀번호는 최소 8자리, 최대 12자리로 작성해야 합니다.")
    private String password;
    @Schema(description = "사용할 nickname", example = "개발새발")
    @NotBlank
    @Size(max=20, message="닉네임은 20자 미만으로 작성해야 합니다.")
    private String nickname;
    @Schema(description = "사용할 phone number", example = "010-1234-5678")
    @NotBlank
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "핸드폰 번호는 11자리 숫자와 '-'로 구성되어야 합니다.")
    private String phone;

}
