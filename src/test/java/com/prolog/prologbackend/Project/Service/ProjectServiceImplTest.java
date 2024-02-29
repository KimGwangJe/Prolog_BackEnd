package com.prolog.prologbackend.Project.Service;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Project.DTO.Request.RequestProjectDetailDTO;
import com.prolog.prologbackend.Project.DTO.Request.RequestStep;
import com.prolog.prologbackend.Project.DTO.Response.ProjectListResponseDTO;
import com.prolog.prologbackend.Project.DTO.Response.ResponseProjectDetailDTO;
import com.prolog.prologbackend.Project.Domain.Project;
import com.prolog.prologbackend.Project.Repository.ProjectRepository;
import com.prolog.prologbackend.Project.Repository.ProjectStackRepository;
import com.prolog.prologbackend.Project.Repository.ProjectStepRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectStepRepository projectStepRepository;
    @Mock
    private ProjectStackRepository projectStackRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    public Project mockProject() throws Exception{
        String start = "2024-02-19";
        String end = "2024-03-19";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = formatter.parse(start);
        Date endDate = formatter.parse(end);
        String create = "2024-03-19 13:52:07";
        SimpleDateFormat createFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date createDate = createFormatter.parse(create);

        Long projectId = 1L;
        Project mockProject = Project.builder()
                .projectId(projectId)
                .projectName("pro")
                .startDate(startDate)
                .endedDate(endDate)
                .description("aaa")
                .stack("1")
                .createdDate(null)
                .modifiedDate(createDate)
                .isDeleted(true)
                .build();

        return mockProject;
    }

    public RequestProjectDetailDTO requestProjectDetailDTO(){
        Long projectId = 1L;
        //프로젝트 스텝 요청 DTO
        RequestStep requestStep = new RequestStep();
        requestStep.setStepName("개발");
        requestStep.setProjectId(1L);
        requestStep.setStartedDate(new Date());
        requestStep.setEndedDate(new Date());
        List<RequestStep> step = new ArrayList<>();
        step.add(requestStep);

        // 프로젝트 업데이트 요청 DTO
        RequestProjectDetailDTO requestProjectDetailDTO = new RequestProjectDetailDTO();
        requestProjectDetailDTO.setProjectId(projectId);
        requestProjectDetailDTO.setProjectName("pro");
        requestProjectDetailDTO.setStartedDate(new Date());
        requestProjectDetailDTO.setEndedDate(new Date());
        requestProjectDetailDTO.setDescription("new description");
        requestProjectDetailDTO.setStack(Collections.singletonList(1L));
        requestProjectDetailDTO.setStep(step);
        return requestProjectDetailDTO;
    }
    @Test
    @DisplayName("Get프로젝트")
    void Get프로젝트() throws Exception{
        Long projectId = 1L;
        Project mockProject = mockProject();

        // projectRepository가 findById 메서드를 호출할 때 mockProject를 반환하도록 설정
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(mockProject));

        // when
        ResponseProjectDetailDTO responseProjectDetailDTO = projectService.getProjectInfo(projectId);

        // then
        assertNotNull(responseProjectDetailDTO); // 응답이 null이 아닌지 확인
        assertEquals(mockProject.getProjectId(), responseProjectDetailDTO.getProjectId());
        assertEquals(mockProject.getProjectName(), responseProjectDetailDTO.getProjectName());
        assertEquals(mockProject.getStartDate(), responseProjectDetailDTO.getStartedDate());
        assertEquals(mockProject.getEndedDate(), responseProjectDetailDTO.getEndedDate());
        assertEquals(mockProject.getDescription(), responseProjectDetailDTO.getDescription());
        assertEquals(mockProject.getCreatedDate(), responseProjectDetailDTO.getCreatedDate());

        // projectRepository가 findById 메서드를 호출할 때 Optional.empty()를 반환하도록 설정
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(BusinessLogicException.class, () -> {
            projectService.getProjectInfo(projectId);
        }, "프로젝트를 찾을 수 없는 경우 BusinessLogicException이 발생해야 합니다.");
    }

    @Test
    @DisplayName("프로젝트 업데이트")
    void 프로젝트_업데이트() throws Exception{
        // given
        Project mockProject = mockProject();

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(mockProject));

        // 프로젝트 업데이트 요청 DTO
        RequestProjectDetailDTO requestProjectDetailDTO = requestProjectDetailDTO();

        // when
        boolean isUpdated = projectService.projectUpdate(requestProjectDetailDTO);
        // then
        assertTrue(isUpdated); // 업데이트가 성공적으로 이루어졌는지 확인

        // 프로젝트 업데이트 전과 후의 description이 다른지 확인
        assertNotEquals(mockProject.getDescription(), requestProjectDetailDTO.getDescription());
    }

    @Test
    @DisplayName("Get_프로젝트_리스트")
    void Get프로젝트_리스트() throws Exception{
        Project mockProject = mockProject();

        // projectRepository가 findById 메서드를 호출할 때 mockProject를 반환하도록 설정
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(mockProject));

        // when
        ProjectListResponseDTO projectListResponseDTO = projectService.getProjectList(1L);

        // then
        assertNotNull(projectListResponseDTO); // 응답이 null이 아닌지 확인
        assertEquals(mockProject.getProjectId(), projectListResponseDTO.getProjectList().get(0).getProjectId());
        assertEquals(mockProject.getProjectName(), projectListResponseDTO.getProjectList().get(0).getProjectName());
        assertEquals(mockProject.getStartDate(), projectListResponseDTO.getProjectList().get(0).getStartedDate());
        assertEquals(mockProject.getEndedDate(), projectListResponseDTO.getProjectList().get(0).getEndedDate());
        assertEquals(mockProject.getDescription(), projectListResponseDTO.getProjectList().get(0).getDescription());
        assertEquals(mockProject.getCreatedDate(), projectListResponseDTO.getProjectList().get(0).getCreatedDate());

        // projectRepository가 findById 메서드를 호출할 때 Optional.empty()를 반환하도록 설정
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThrows(BusinessLogicException.class, () -> {
            projectService.getProjectList(1L);
        }, "유저를 찾을 수 없는 경우 BusinessLogicException이 발생해야 합니다.");
    }

    @Test
    @DisplayName("프로젝트 생성")
    void 프로젝트_생성() throws Exception {

        Project mockProject = mockProject();

        // projectRepository.save() 메서드의 반환값 설정
        when(projectRepository.save(any(Project.class))).thenReturn(mockProject);

        // 프로젝트 업데이트 요청 DTO 생성
        RequestProjectDetailDTO projectDetailDTO = requestProjectDetailDTO();

        // 프로젝트 생성 메서드 호출
        Long projectId = projectService.createProject(projectDetailDTO);

        // 프로젝트 ID가 null이 아닌지 확인
        assertNotNull(projectId);
    }

    @Test
    @DisplayName("프로젝트 삭제")
    void 프로젝트_삭제() throws Exception{
        // given
        Long projectId = 1L;
        Project mockProject = mockProject();

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(mockProject));

        // when
        projectService.deleteProject(projectId);

        ArgumentCaptor<Project> projectCaptor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(projectCaptor.capture()); // save 메서드에 전달된 Project 객체를 캡처

        Project updatedProject = projectCaptor.getValue();
        assertEquals(true, updatedProject.getIsDeleted()); // isDeleted가 true로 설정되었는지 확인
    }

}