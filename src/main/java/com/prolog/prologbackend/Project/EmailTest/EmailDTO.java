package com.prolog.prologbackend.Project.EmailTest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * Author : Kim
 * Date : 2024-02-11
 * Description : targetMail, Nickname, Subject, ProjectNum, Link가 우선적으로 필요합니다.
 * 화면을 구성해서 전달하는 것이 좋을것 같으며
 * 링크를 누르면 리다이렉션 되어서 팀에 가입 하는 API를 서버로 날리도록 하면 될듯 합니다.
 *
 * 초대 메일을 경우에서는 html을 만들어서 그 안에 버튼을 두고 링크를 누르면 해당 url로 리다이렉션? 되도록 하는게 나을까요?
 * 초대 URL을 어떻게 구성할지가 고민이네요
 *
 * 이메일 전송 관련 테스트입니다 추후 패키지 변경 예정
*/

@Getter
@Setter
public class EmailDTO {
    @Email
    @NotBlank
    @Schema(description = "초대 메일을 보낼 이메일", nullable = false, example = "k12@gmail.com")
    private String targetMail;
    @NotBlank
    @Schema(description = "초대를 받은 사람의 닉네임입니다.", nullable = false, example = "김광")
    private String nickname;
    @NotBlank
    @Schema(description = "메일 제목 ) 이 부분은 저희가 정해서 날리는게 나을듯 합니다", nullable = false, example = "[prolog 프로젝트] 프로젝트에 참여해주세요!")
    private String subject;
    @NotBlank
    @Schema(description = "초대된 프로젝트 번호", nullable = false, example = "1")
    private String projectNum;
    @NotBlank
    @Schema(description = "초대된 프로젝트의 리다이렉션? 링크", nullable = false, example = "이 부분은 조금 고민입니다.")
    private String link;
}

