package com.prolog.prologbackend.Project.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Author : Kim
 * Date : 2024-02-17
 * Description : 프로젝트의 단계 정보입니다.
 * 프론트에서 받을때는 stepId 없음
 */

@Getter
@Setter
public class ResponseStep {

    @Schema(description = "프로젝트 단계 ID", nullable = false, example = "1")
    private Long stepId;

    @NotBlank
    @Schema(description = "프로젝트 단계 이름", nullable = false, example = "자료조사")
    private String stepName;

    @NotBlank
    @Schema(description = "프로젝트 단계의 시작 날짜", nullable = false, example = "2024-02-17")
    private Date startedDate;

    @NotBlank
    @Schema(description = "프로젝트 단계의 종료 날짜", nullable = false, example = "2024-03-17")
    private Date endedDate;
}