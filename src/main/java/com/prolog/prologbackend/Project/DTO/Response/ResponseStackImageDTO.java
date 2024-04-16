package com.prolog.prologbackend.Project.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseStackImageDTO{

    @Schema(description = "스택 이미지의 ID", nullable = false, example = "1")
    private Long stackId;

    @Schema(description = "스택 이미지의 이름", nullable = false, example = "React or 리액트")
    private String stackName;

    @Schema(description = "스택 이미지의 링크", nullable = false, example = "www.~~~")
    private String image;
}
