package com.prolog.prologbackend.Project.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Author : Kim
 * Date : 2024-02-11
 * Description : targetMail, Nickname, Subject, ProjectNum, Link가 우선적으로 필요합니다.
 * 화면을 구성해서 전달하는 것이 좋을것 같으며
 * 리다이렉션에 targetId와 projectId를 넣어서 Save API 요청
 */

@Getter
@Setter
public class InvitationEmailDTO {

    @Email
    @NotBlank
    @Schema(description = "초대 메일을 받는 사용자의 이메일", nullable = false, example = "k12@gmail.com")
    private String targetMail;

    @Min(1)
    @Schema(description = "초대 메일을 받을 사용자의 회원ID", nullable = false, example = "1")
    private Long targetId;

    @NotBlank
    @Schema(description = "초대를 받은 사람의 닉네임입니다.", nullable = false, example = "김광")
    private String nickname;

    @NotBlank
    @Schema(description = "Prolog [프로젝트 이름] 프로젝트에 초대되었습니다.", nullable = false, example = "todolist")
    private String projectName;

    @Min(1)
    @Schema(description = "초대된 프로젝트 번호", nullable = false, example = "1")
    private String projectId;
}
