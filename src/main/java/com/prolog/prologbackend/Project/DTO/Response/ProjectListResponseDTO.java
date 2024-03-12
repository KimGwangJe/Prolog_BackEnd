package com.prolog.prologbackend.Project.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class ProjectListResponseDTO {
    @Schema(description = "프로젝트들을 담은 리스트입니다.", nullable = true,
            example = "200 -> projectList:[{projectname:asd, projectnum:1, ~~~},{~~~}], 204 -> 빈 리스트 반환")
    private List<ResponseProjectDetailDTO> projectList;
}