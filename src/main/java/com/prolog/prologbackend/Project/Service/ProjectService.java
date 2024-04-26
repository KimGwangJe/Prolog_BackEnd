package com.prolog.prologbackend.Project.Service;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Project.DTO.Request.RequestProjectDetailDTO;
import com.prolog.prologbackend.Project.DTO.Request.RequestStep;
import com.prolog.prologbackend.Project.DTO.Response.*;
import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.Project.Domain.ProjectStack;
import com.prolog.prologbackend.Project.Domain.ProjectStep;
import com.prolog.prologbackend.Project.ExceptionType.ProjectExceptionType;
import com.prolog.prologbackend.Project.Repository.ProjectRepository;
import com.prolog.prologbackend.Project.Repository.ProjectStackRepository;
import com.prolog.prologbackend.Project.Repository.ProjectStepRepository;
import com.prolog.prologbackend.TeamMember.DTO.Response.ListTeamMemberDto;
import com.prolog.prologbackend.TeamMember.Domain.TeamMember;
import com.prolog.prologbackend.TeamMember.Service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectStepRepository projectStepRepository;
    private final ProjectStackRepository projectStackRepository;
    private final TeamMemberService teamMemberService;

    @Transactional(readOnly = true)
    public ResponseProjectDetailDTO getProjectInfo(Long projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new BusinessLogicException(ProjectExceptionType.PROJECT_NOT_FOUND));

        List<TeamMember> teamMembers = teamMemberService.getListByProject(project);
        List<ListTeamMemberDto> listTeamMemberDtos = new ArrayList<>();

        for(TeamMember t: teamMembers){
            listTeamMemberDtos.add(ListTeamMemberDto.of(t));
        }

        ResponseProjectDetailDTO responseProjectDetailDTO = createResponseProjectDetailDTO(project);
        responseProjectDetailDTO.setTeamMembers(listTeamMemberDtos);

        return responseProjectDetailDTO;
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateProject(RequestProjectDetailDTO projectDetailDTO, Member member) {

        Project project = projectRepository.findById(projectDetailDTO.getProjectId()).orElseThrow(() ->
                new BusinessLogicException(ProjectExceptionType.PROJECT_NOT_FOUND));

        teamMemberService.getEntityByMemberAndProject(member,project);

        project = Project.builder()
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
                projectRepository.save(project);

                // Step 정보 업데이트
                updateSteps(projectDetailDTO.getStep(), project);
            } catch(Exception e){
                throw new BusinessLogicException(ProjectExceptionType.PROJECT_SAVE_ERROR);
            }

    }


    @Transactional(readOnly = true)
    public ProjectListResponseDTO getProjectList(Member member) {
        List<TeamMember> teamMembers = teamMemberService.getListByMember(member);

        List<ResponseProjectDetailDTO> projectList = new ArrayList<>();

        for (TeamMember teamMember : teamMembers) {
            Project project = teamMember.getProject();
            projectList.add(createResponseProjectDetailDTO(project));
        }

        ProjectListResponseDTO projectListResponseDTO = new ProjectListResponseDTO();
        projectListResponseDTO.setProjectList(projectList);

        return projectListResponseDTO;
    }


    @Transactional(rollbackFor = Exception.class)
    public Long createProject(RequestProjectDetailDTO projectDetailDTO) {
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

    public void deleteProject(Long projectId,Member member) {
        Project project = projectRepository.findById(projectId).orElseThrow(() ->
                new BusinessLogicException(ProjectExceptionType.PROJECT_NOT_FOUND));

        teamMemberService.getEntityByMemberAndProject(member,project);

        // 프로젝트의 isDeleted와 modifiedDate 수정
        project.deleteProject();
            // 프로젝트 업데이트 저장
        try{
            projectRepository.save(project);
        } catch (Exception e){
            throw new BusinessLogicException(ProjectExceptionType.PROJECT_SAVE_ERROR);
        }

    }

    public ResponseStackImageListDTO getStackImage() {
        ResponseStackImageListDTO responseStackImageListDTO = new ResponseStackImageListDTO();

        List<ProjectStack> stackImages = projectStackRepository.findAll();

        List<ResponseStackImageDTO> stackImageDTOList = stackImages.stream().map(stackImage -> {
            ResponseStackImageDTO stackImageDTO = new ResponseStackImageDTO();
            stackImageDTO.setStackId(stackImage.getStackId());
            stackImageDTO.setStackName(stackImage.getStackName());
            stackImageDTO.setImage(stackImage.getImage());
            return stackImageDTO;
        }).collect(Collectors.toList());

        responseStackImageListDTO.setStackImageList(stackImageDTOList);

        return responseStackImageListDTO;
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
                    responseStack.setStackImageLink(stack.getImage());
                    return responseStack;
                })
                .collect(Collectors.toList());
    }

    // 팀멤버 생성 시 프로젝트를 조회하기 위한 메서드
    public Project getProject(Long projectId){
        return projectRepository.findById(projectId).orElseThrow(
                () -> new BusinessLogicException(ProjectExceptionType.PROJECT_NOT_FOUND)
        );
    }
}
