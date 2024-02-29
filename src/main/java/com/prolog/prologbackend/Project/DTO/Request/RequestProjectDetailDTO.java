package com.prolog.prologbackend.Project.DTO.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * Author : Kim
 * Date : 2024-02-19
 * Description : 프로젝트 생성, 수정시에 사용
 */

@Getter
@Setter
public class RequestProjectDetailDTO {
    @Schema(description = "프로젝트를 구분하는 번호입니다.", nullable = true, example = "1")
    private Long projectId;

    @NotBlank
    @Schema(description = "프로젝트 이름입니다.", nullable = false, example = "prolog")
    private String projectName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 시작 날짜입니다.", nullable = false, example = "2024-02-11")
    private Date startedDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "프로젝트 종료 날짜입니다.", nullable = false, example = "2024-03-11")
    private Date endedDate;

    @Schema(description = "프로젝트에 대한 설명입니다.", nullable = true, example = "자유형식")
    private String description;

    @Schema(description = "프로젝트에 사용되는 스택입니다.", nullable = true)
    private List<Long> stack;

    @NotEmpty //List 타입에는 NotEmpty
    @Schema(description = "프로젝트 단계 리스트입니다.", nullable = false, example = "projectStep:[{},{}]")
    private List<RequestStep> step;
}