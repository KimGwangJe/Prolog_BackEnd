package com.prolog.prologbackend.Project.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.prologbackend.Project.DTO.Request.RequestProjectDetailDTO;
import com.prolog.prologbackend.Project.DTO.Request.RequestStep;
import com.prolog.prologbackend.Project.Service.ProjectService;
import com.prolog.prologbackend.Security.Jwt.JwtProvider;
import com.prolog.prologbackend.Security.Jwt.JwtType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class) //controller 전용
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc //MockMvc 사용 위함
class ProjectControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ProjectService projectService;

    @MockBean
    JwtProvider jwtProvider;


    @Test
    @DisplayName("프로젝트 정보 Get")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void 프로젝트_정보_Get() throws Exception {
        /**
         * 실패
         */
//         given
        Long invalidProjectId = null; // 유효하지 않은 프로젝트 ID (null)

        // when & then
        mvc.perform(get("/project/info")
                        .with(csrf())
                        .param("projectId", invalidProjectId != null ? invalidProjectId.toString() : ""))
                .andExpect(status().isBadRequest());

        /**
         * 성공
         */

        // given
        Long projectId = 1L;

        // when & then
        mvc.perform(get("/project/info")
                        .with(csrf())
                        .param("projectId", projectId.toString()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("프로젝트 정보 업데이트")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void 프로젝트_정보_업데이트() throws Exception {

        RequestProjectDetailDTO requestProjectDetailDTO = requestProjectDetailDTO();

        /**
         * 실패 - null 포함
         */
        mvc.perform(put("/api/project/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestProjectDetailDTO)))
                .andExpect(status().isBadRequest());

        //null 해결
        requestProjectDetailDTO.setProjectId(1L);

        /**
         * 성공
         */
        mvc.perform(put("/api/project/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestProjectDetailDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("프로젝트 리스트 반환")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void 프로젝트_리스트_반환() throws Exception {

        // given
        String validEmail = "valid@test.com";
        String validToken = jwtProvider.createToken(JwtType.ACCESS_TOKEN, validEmail); // 유효한 토큰
        // when & then
        mvc.perform(get("/project/list")
                        .with(csrf())
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.projectList").doesNotExist()); // 프로젝트 리스트가 없음을 확인합니다.
    }




    @Test
    @DisplayName("프로젝트 생성")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void 프로젝트_생성() throws Exception {
        RequestProjectDetailDTO requestProjectDetailDTO = requestProjectDetailDTO();

        /**
         * 실패 - null 포함
         */
        requestProjectDetailDTO.setProjectName("");
        mvc.perform(post("/api/project/insert")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestProjectDetailDTO)))
                .andExpect(status().isBadRequest());

        //null 해결
        requestProjectDetailDTO.setProjectName("테스트");
        /**
         * 성공
         */
        mvc.perform(post("/api/project/insert")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestProjectDetailDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("프로젝트 삭제")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void 프로젝트_삭제() throws Exception{

        /**
         * 실패
         */
        Long InvalidProjectId = null;
        mvc.perform(put("/api/project/delete")
                        .with(csrf())
                        .param("projectId",InvalidProjectId != null ? InvalidProjectId.toString() : ""))
                .andExpect(status().isBadRequest());

        /**
         * 성공
         */
        Long projectId = 1L;

        mvc.perform(put("/api/project/delete")
                        .with(csrf())
                        .param("projectId",projectId.toString()))
                .andExpect(status().isOk());
    }

    public RequestProjectDetailDTO requestProjectDetailDTO() throws Exception{
        String start = "2024-02-22";
        String end = "2024-02-22";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = formatter.parse(start);
        Date endDate = formatter.parse(end);

        List<RequestStep> step = new ArrayList<>();
        RequestStep requestStep = new RequestStep();
        requestStep.setProjectId(1L);
        requestStep.setStepName("테스트 단계");
        requestStep.setEndedDate(startDate);
        requestStep.setStartedDate(endDate);
        step.add(requestStep);

        List<Long> stack = new ArrayList<>();
        stack.add(1L);

        RequestProjectDetailDTO requestProjectDetailDTO = new RequestProjectDetailDTO();
        requestProjectDetailDTO.setProjectName("테스트");
        requestProjectDetailDTO.setDescription("테스트 내용");
        requestProjectDetailDTO.setStep(step);
        requestProjectDetailDTO.setStack(stack);
        requestProjectDetailDTO.setStartedDate(startDate);
        requestProjectDetailDTO.setEndedDate(endDate);

        return requestProjectDetailDTO;
    }
}