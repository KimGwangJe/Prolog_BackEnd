package com.prolog.prologbackend.Project.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ResponseStack {

    @Schema(description = "스택 ID", nullable = false, example = "1")
    private Long stackID;

    @NotBlank
    @Schema(description = "스택 이름", nullable = false, example = "React")
    private String stackName;

    @NotBlank
    @Schema(description = "스택 이미지 링크", nullable = false, example = "www.~~~ 이미지 저장 url")
    private String stackImageLink;
}