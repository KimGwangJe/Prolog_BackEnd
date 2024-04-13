package com.prolog.prologbackend.Project.DTO.Response;

import com.prolog.prologbackend.Project.DTO.Request.RequestStep;
import com.prolog.prologbackend.TeamMember.DTO.Response.ListTeamMemberDto;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;


@Getter
@Setter
@Schema(title = "ProjectResponse : 프로젝트 반환 DTO")
public class ResponseProjectDetailDTO {

    @Schema(description = "프로젝트를 구분하는 번호입니다.", nullable = false, example = "1")
    private Long projectId;

    @NotBlank
    @Schema(description = "프로젝트 이름입니다.", nullable = false, example = "prolog")
    private String projectName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 시작 날짜입니다.", nullable = false, example = "2024-02-11")
    private Date startedDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 종료 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date endedDate;

    @Schema(description = "프로젝트에 대한 설명입니다.", nullable = true, example = "자유형식")
    private String description;

    @Schema(description = "프로젝트에 사용되는 스택입니다.", nullable = true, example = "리스트로 된 ResponseStack")
    private List<ResponseStack> stack;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 생성 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date createdDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 수정 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date modifiedDate;

    @Schema(description = "프로젝트 단계 리스트입니다.", nullable = false, example = "리스트로 된 projectStep")
    private List<ResponseStep> step;

    @Schema(description = "팀멤버 리스트입니다.", nullable = false, example = "리스트로 된 TeamMember")
    private List<ListTeamMemberDto> teamMembers;
}