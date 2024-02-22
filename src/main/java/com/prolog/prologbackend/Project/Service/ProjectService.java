package com.prolog.prologbackend.Project.Service;

import com.prolog.prologbackend.Project.DTO.Request.RequestProjectDetailDTO;
import com.prolog.prologbackend.Project.DTO.Response.ProjectListResponseDTO;
import com.prolog.prologbackend.Project.DTO.Response.ResponseProjectDetailDTO;

/**
 * 2024-02-17 Project List 파일과 합침
 */
public interface ProjectService {
    ResponseProjectDetailDTO getProjectInfo(Long projectId);

    boolean projectUpdate(RequestProjectDetailDTO projectDetailDTO);

    ProjectListResponseDTO getProjectList(Long userId);

    Long createProject(RequestProjectDetailDTO projectDetailDTO);

    void deleteProject(Long projectId);
}