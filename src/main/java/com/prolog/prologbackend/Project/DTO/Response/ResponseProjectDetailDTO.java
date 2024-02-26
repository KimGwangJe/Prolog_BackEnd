package com.prolog.prologbackend.Project.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * Author : Kim
 * Date : 2024-02-17
 * Description : DB에 있는 생성일과 수정일은 사용자가 직접 수정이 불가능합니다.
 * 02-17 projectStack 클래스 생성
 */
@Getter
@Setter
public class ResponseProjectDetailDTO {

    @Schema(description = "프로젝트를 구분하는 번호입니다.", nullable = false, example = "1")
    private Long projectId;

    @NotBlank
    @Schema(description = "프로젝트 이름입니다.", nullable = false, example = "prolog")
    private String projectName;

    // 이 부분은 팀원 부분이라 나중에 영은님이 구현해주면 가져오는걸로
//    @NotBlank
//    @Schema(description = "프로젝트에서의 사용자의 역할", nullable = true, example = "Master")
//    private ProjectRole projectRole;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 시작 날짜입니다.", nullable = false, example = "2024-02-11")
    private Date startedDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 종료 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date endedDate;

    @Schema(description = "프로젝트에 대한 설명입니다.", nullable = true, example = "자유형식")
    private String description;

    @Schema(description = "프로젝트에 사용되는 스택입니다.", nullable = true, example = "items: [{stackId: 1,stackName: React, stackImage:www.~~~}, ~~], 받을때는 1/2/3/4??? 어떻게 받지")
    private List<ResponseStack> stack;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 생성 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date createdDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 수정 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date modifiedDate;

    @Schema(description = "프로젝트 단계 리스트입니다.", nullable = false, example = "projectStep:[{},{}]")
    private List<ResponseStep> step;
}