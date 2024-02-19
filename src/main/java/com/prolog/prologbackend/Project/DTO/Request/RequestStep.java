package com.prolog.prologbackend.Project.DTO.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RequestStep {

    @NotBlank
    @Schema(description = "프로젝트 단계 이름", nullable = false, example = "자료조사")
    private String stepName;

    @NotBlank
    @Schema(description = "프로젝트 단계의 시작 날짜", nullable = false, example = "2024-02-17")
    private Date startedDate;

    @NotBlank
    @Schema(description = "프로젝트 단계의 종료 날짜", nullable = false, example = "2024-03-17")
    private Date endedDate;

    @Schema(description = "프로젝트 ID", nullable = false, example = "1")
    private Long projectId;
}