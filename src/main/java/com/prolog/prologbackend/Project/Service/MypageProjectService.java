package com.prolog.prologbackend.Project.Service;

import com.prolog.prologbackend.Project.DTO.ProjectListResponseDTO;

public interface MypageProjectService {
    ProjectListResponseDTO getProjectList(Long userId);
}
