package com.prolog.prologbackend.Project.Service;

import com.prolog.prologbackend.Project.DTO.Request.RequestProjectDetailDTO;
import com.prolog.prologbackend.Project.DTO.Response.ProjectListResponseDTO;
import com.prolog.prologbackend.Project.DTO.Response.ResponseProjectDetailDTO;

public interface ProjectService {
    ResponseProjectDetailDTO getProjectInfo(Long projectId);

    boolean projectUpdate(RequestProjectDetailDTO projectDetailDTO, String email);

    ProjectListResponseDTO getProjectList(String userEmail);

    Long createProject(RequestProjectDetailDTO projectDetailDTO);

    void deleteProject(Long projectId,String email);
}