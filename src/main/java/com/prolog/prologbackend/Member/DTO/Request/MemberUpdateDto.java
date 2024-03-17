package com.prolog.prologbackend.Member.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Schema(title = "MemberRequest : 회원 정보 수정 DTO")
@Getter
public class MemberUpdateDto {
    @Schema(description = "회원 email. 변경을 원하는 경우 새로운 email이 담김", example = "kimLeeChoi@mail.com")
    @Email
    private String email;
    @Schema(description = "회원 password. 변경을 원하는 경우 새로운 password가 담김", example = "password1234")
    @NotBlank
    @Size(min=8, max=20, message="비밀번호는 최소 8자리, 최대 12자리로 작성해야 합니다.")
    private String password;
    @Schema(description = "회원 nickname. 변경을 원하는 경우 새로운 nickname이 담김", example = "개발새발")
    @NotBlank
    @Size(max=20, message="닉네임은 20자 미만으로 작성해야 합니다.")
    private String nickname;
}
