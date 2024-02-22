package com.prolog.prologbackend.Project.Service;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Project.DTO.Request.RequestProjectDetailDTO;
import com.prolog.prologbackend.Project.DTO.Request.RequestStep;
import com.prolog.prologbackend.Project.DTO.Response.ProjectListResponseDTO;
import com.prolog.prologbackend.Project.DTO.Response.ResponseProjectDetailDTO;
import com.prolog.prologbackend.Project.DTO.Response.ResponseStack;
import com.prolog.prologbackend.Project.DTO.Response.ResponseStep;
import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.Project.Domain.ProjectStack;
import com.prolog.prologbackend.Project.Domain.ProjectStep;
import com.prolog.prologbackend.Project.ExceptionType.ProjectExceptionType;
import com.prolog.prologbackend.Project.Repository.ProjectRepository;
import com.prolog.prologbackend.Project.Repository.ProjectStackRepository;
import com.prolog.prologbackend.Project.Repository.ProjectStepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Author : Kim
 * Date : 2024-02-17
 * Description :
 * info - 프로젝트 ID로 특정 프로젝트의 정보들과 팀멤버에서 프로젝트 ID로 팀원들을 조회해옵니다.
 * info - 프로젝트 ID로 특정 프로젝트의 단계들을 조회해옵니다.
 * info - 가져온 project의 스택으로 프로젝트 스택 테이블에서 검색해서 또 줘야됨
 * update - info 가져와서 보여주고 변경된 info 받아와서 저장
 * list - 회원ID로 팀멤버 테이블에서 회원ID가 같은 row의 프로젝트 id(여러개)를 찾아서 프로젝트 테이블에서 프로젝트 id로 검색
 */

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectStepRepository projectStepRepository;
    private final ProjectStackRepository projectStackRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseProjectDetailDTO getProjectInfo(Long projectId) {
        /**
         * 팀 멤버 조회 하는것도 추가 필요
         */
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new BusinessLogicException(ProjectExceptionType.PROJECT_NOT_FOUND));

        ResponseProjectDetailDTO responseProjectDetailDTO = createResponseProjectDetailDTO(project);

        return responseProjectDetailDTO;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean projectUpdate(RequestProjectDetailDTO projectDetailDTO) {

        Project project = projectRepository.findById(projectDetailDTO.getProjectId()).orElseThrow(() ->
                new BusinessLogicException(ProjectExceptionType.PROJECT_NOT_FOUND));

        // 빌더 패턴을 사용하여 새로운 프로젝트 엔티티 생성 및 필드 값 설정
        Project updatedProject = Project.builder()
                .projectId(project.getProjectId()) // 기존 프로젝트의 아이디 설정
                .projectName(projectDetailDTO.getProjectName())
                .startDate(projectDetailDTO.getStartedDate())
                .endedDate(projectDetailDTO.getEndedDate())
                .description(projectDetailDTO.getDescription())
                .stack(listToString(projectDetailDTO.getStack()))
                .modifiedDate(new Date()) // 수정 날짜를 현재 날짜로 업데이트
                .build();

        try{
            // 프로젝트 업데이트 저장
            projectRepository.save(updatedProject);

            // Step 정보 업데이트
            updateSteps(projectDetailDTO.getStep(), project);

            return true; // 성공적으로 업데이트됨
        } catch(Exception e){
            throw new BusinessLogicException(ProjectExceptionType.PROJECT_SAVE_ERROR);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public ProjectListResponseDTO getProjectList(Long userId) {
        /**
         * 팀멤버 repository 가 push 되면 userId로 teamMember 테이블에서 projectId 가져오게 변경
         */
        List<Long> projectIdList = new ArrayList<>();
        projectIdList.add(1L);

        List<ResponseProjectDetailDTO> projectList = new ArrayList<>();

        for (Long projectId : projectIdList) {
            // 여기에서는 굳이인가? 사용자가 projectID로 요청하는게 아니라 DB에서 조회해서 주는거라 음...
            Project project = projectRepository.findById(projectId).orElseThrow(() ->
                    new BusinessLogicException(ProjectExceptionType.PROJECT_NOT_FOUND));

            ResponseProjectDetailDTO responseProjectDetailDTO = createResponseProjectDetailDTO(project);
            projectList.add(responseProjectDetailDTO);

        }

        ProjectListResponseDTO projectListResponseDTO = new ProjectListResponseDTO();
        projectListResponseDTO.setProjectList(projectList);

        return projectListResponseDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProject(RequestProjectDetailDTO projectDetailDTO) {
        //프로젝트를 우선 저장하고 projectID 가져와서 그걸 이용해서 step 저장할때 projectID를 넣어줘야됨
        Project project = Project.builder()
                .projectName(projectDetailDTO.getProjectName())
                .startDate(projectDetailDTO.getStartedDate())
                .endedDate(projectDetailDTO.getEndedDate())
                .description(projectDetailDTO.getDescription())
                .stack(listToString(projectDetailDTO.getStack()))
                .createdDate(new Date())
                .modifiedDate(new Date())
                .isDeleted(false)
                .build();
        try{
            project = projectRepository.save(project);

            // 단계 저장
            updateSteps(projectDetailDTO.getStep(),project);

            return project.getProjectId();
        } catch(Exception e){
            throw new BusinessLogicException(ProjectExceptionType.PROJECT_SAVE_ERROR);
        }
    }

    @Override
    public void deleteProject(Long projectId) {
        //프로젝트 정보
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new BusinessLogicException(ProjectExceptionType.PROJECT_NOT_FOUND));


        // 빌더 패턴을 사용하여 새로운 프로젝트 엔티티 생성 및 필드 값 설정
        Project updatedProject = Project.builder()
                .projectId(project.getProjectId())
                .projectName(project.getProjectName())
                .startDate(project.getStartDate())
                .endedDate(project.getEndedDate())
                .description(project.getDescription())
                .stack(project.getStack())
                .modifiedDate(new Date()) // 수정 날짜를 현재 날짜로 업데이트
                .isDeleted(true) // 삭제되었음을 표시
                .build();

        // 프로젝트 업데이트 저장
        projectRepository.save(updatedProject);
    }


    /**
     * 공통 로직
     */

    // 프로젝트 조회 하여 ResponseProjectDetailDTO 생성
    private ResponseProjectDetailDTO createResponseProjectDetailDTO(Project project) {
        ResponseProjectDetailDTO responseProjectDetailDTO = mapProjectToResponseProjectDetailDTO(project);

        List<ProjectStep> projectSteps = projectStepRepository.findByProjectProjectId(project.getProjectId());
        List<ResponseStep> responseSteps = mapProjectStepsToResponseSteps(projectSteps);
        responseProjectDetailDTO.setStep(responseSteps);

        List<Long> stackIds = stringToList(project.getStack());
        List<ResponseStack> responseStacks = mapStackIdsToResponseStacks(stackIds);
        responseProjectDetailDTO.setStack(responseStacks);

        return responseProjectDetailDTO;
    }

    // stack [1,2,3,4] -> "1/2/3/4"로 변경
    public static String listToString(List<Long> list) {
        StringBuilder builder = new StringBuilder();
        for (Long item : list) {
            builder.append(item).append("/");
        }
        builder.deleteCharAt(builder.length() - 1); // 마지막 "/"를 삭제
        return builder.toString();
    }

    // "1/2/3/4" -> stack [1,2,3,4]로 변경
    private List<Long> stringToList(String stackString) {
        return Arrays.stream(stackString.split("/"))
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    // 프로젝트 step 변경 메서드
    // 메서드명이 별로인거같은데,,, 생성과 업데이트에 사용되어서
    private void updateSteps(List<RequestStep> requestSteps, Project project) {
        // 기존 단계 삭제
        // 업데이트를 원했는데 프로젝트 단계의 갯수가 줄어든다면 update가 가능?
        projectStepRepository.deleteByProject(project);

        // 새로운 단계 추가
        List<ProjectStep> projectSteps = new ArrayList<>();

        for (RequestStep requestStep : requestSteps) {
            ProjectStep projectStep = ProjectStep.builder()
                    .stepName(requestStep.getStepName())
                    .startedDate(requestStep.getStartedDate())
                    .endedDate(requestStep.getEndedDate())
                    .project(project)
                    .build();
            projectSteps.add(projectStep);
        }

        // 새로운 단계들 저장
        projectStepRepository.saveAll(projectSteps);
    }

    // 프로젝트 정보를 ResponseProjectDetailDTO로 변환하는 메서드
    private ResponseProjectDetailDTO mapProjectToResponseProjectDetailDTO(Project project) {
        ResponseProjectDetailDTO responseProjectDetailDTO = new ResponseProjectDetailDTO();
        responseProjectDetailDTO.setProjectId(project.getProjectId());
        responseProjectDetailDTO.setProjectName(project.getProjectName());
        responseProjectDetailDTO.setStartedDate(project.getStartDate());
        responseProjectDetailDTO.setEndedDate(project.getEndedDate());
        responseProjectDetailDTO.setDescription(project.getDescription());
        responseProjectDetailDTO.setCreatedDate(project.getCreatedDate());
        responseProjectDetailDTO.setModifiedDate(project.getModifiedDate());
        return responseProjectDetailDTO;
    }

    // 프로젝트 스텝을 ResponseStep 리스트로 변환하는 메서드
    private List<ResponseStep> mapProjectStepsToResponseSteps(List<ProjectStep> projectSteps) {
        return projectSteps.stream()
                .map(step -> {
                    ResponseStep responseStep = new ResponseStep();
                    responseStep.setStepId(step.getStepId());
                    responseStep.setStepName(step.getStepName());
                    responseStep.setStartedDate(step.getStartedDate());
                    responseStep.setEndedDate(step.getEndedDate());
                    return responseStep;
                })
                .collect(Collectors.toList());
    }

    // 프로젝트 스택 ID 리스트를 ResponseStack 리스트로 변환하는 메서드
    private List<ResponseStack> mapStackIdsToResponseStacks(List<Long> stackIds) {
        List<ProjectStack> projectStacks = projectStackRepository.findAllById(stackIds);
        return projectStacks.stream()
                .map(stack -> {
                    ResponseStack responseStack = new ResponseStack();
                    responseStack.setStackID(stack.getStackId());
                    responseStack.setStackName(stack.getStackName());
                    responseStack.setImage(stack.getImage());
                    return responseStack;
                })
                .collect(Collectors.toList());
    }

}