package com.prolog.prologbackend.Project.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ProjectListResponseDTO {
    @Schema(description = "프로젝트들을 담은 리스트입니다.", nullable = true, example = "{projectname:asd, projectnum:1, ~~~}")
    private ArrayList<ProjectDetailRequestDTO> projectList;
}
