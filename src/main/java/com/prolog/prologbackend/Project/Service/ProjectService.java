package com.prolog.prologbackend.Project.Service;

import com.prolog.prologbackend.Project.DTO.Request.RequestProjectDetailDTO;
import com.prolog.prologbackend.Project.DTO.Response.ProjectListResponseDTO;
import com.prolog.prologbackend.Project.DTO.Response.ResponseProjectDetailDTO;
import com.prolog.prologbackend.Project.DTO.Response.ResponseStackImageListDTO;

public interface ProjectService {
    ResponseProjectDetailDTO getProjectInfo(Long projectId);

    boolean projectUpdate(RequestProjectDetailDTO projectDetailDTO, Long memberId);

    ProjectListResponseDTO getProjectList(Long memberId);

    Long createProject(RequestProjectDetailDTO projectDetailDTO);

    void deleteProject(Long projectId,Long memberId);

    ResponseStackImageListDTO getStackImage();
}