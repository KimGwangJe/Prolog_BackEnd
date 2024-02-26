package com.prolog.prologbackend.Project.DTO.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Author : Kim
 * Date : 2024-02-17
 * Description : 프로젝트 리스트를 담아서 반환합니다.
 * ProjectDetailDTO에 포함됩니다.
 */

@Getter
@Setter
public class ProjectListResponseDTO {
    @Schema(description = "프로젝트들을 담은 리스트입니다.", nullable = true, example = "projectList:[{projectname:asd, projectnum:1, ~~~},{~~~}]")
    private List<ResponseProjectDetailDTO> projectList;
}