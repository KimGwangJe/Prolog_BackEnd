package com.prolog.prologbackend.Project.DTO.Request;

import com.prolog.prologbackend.TeamMember.Domain.Part;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


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
    @Schema(description = "초대를 받은 사람의 닉네임입니다.", nullable = false, example = "김광제")
    private String nickname;

    @NotBlank
    @Schema(description = "초대 된 프로젝트의 이름", nullable = false, example = "todolist")
    private String projectName;

    @Min(1)
    @Schema(description = "초대된 프로젝트 번호", nullable = false, example = "1")
    private Long projectId;

    @NotEmpty
    @Schema(description = "초대된 팀멤버의 역할 구분", nullable = false, example = "[Leader,Backend]")
    private List<Part> partList;
}
