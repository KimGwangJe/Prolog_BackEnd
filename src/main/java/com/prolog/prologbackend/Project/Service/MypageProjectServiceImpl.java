package com.prolog.prologbackend.Project.Service;

import com.prolog.prologbackend.Project.DTO.ProjectListResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Author : Kim
 * date : 2024-02-11
 * description : 우선은 getUserInfo에서 members entity에 필드를 넣고 난 이후 DTO 내부에서 Entity를 DTO로 변경합시다
 */

@Service
@RequiredArgsConstructor
public class MypageProjectServiceImpl implements MypageProjectService{
    @Override
    public ProjectListResponseDTO getProjectList(Long userId) {
        ProjectListResponseDTO projectList = new ProjectListResponseDTO();
        return projectList;
    }

}
