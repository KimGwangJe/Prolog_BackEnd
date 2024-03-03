package com.prolog.prologbackend.Notes.Controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.prolog.prologbackend.Notes.DTO.NotesType;
import com.prolog.prologbackend.Notes.DTO.Request.RequestNotesDTO;
import com.prolog.prologbackend.Notes.Service.NotesService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(NotesController.class) //controller 전용
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureMockMvc //MockMvc 사용 위함
class NotesControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;
    @MockBean
    NotesService notesService;

    @Test
    @DisplayName("일지 정보 리스트 Get")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void 일지_정보_리스트() throws Exception {
        Long invalidMemberId = null;
        mvc.perform(get("/notes/list")
                        .with(csrf())
                        .param("teamMemberId", invalidMemberId != null ? invalidMemberId.toString() : ""))
                .andExpect(status().isBadRequest());

        Long validMemberId = 1L;
        mvc.perform(get("/notes/list")
                        .with(csrf())
                        .param("teamMemberId", validMemberId != null ? validMemberId.toString() : ""))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일지 정보 Get")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void 일지_정보_GET() throws Exception {
        Long invalidNotesId = null;
        mvc.perform(get("/notes/info")
                        .with(csrf())
                        .param("notesId", invalidNotesId != null ? invalidNotesId.toString() : ""))
                .andExpect(status().isBadRequest());

        Long validNotesId = 1L;
        mvc.perform(get("/notes/info")
                        .with(csrf())
                        .param("notesId", validNotesId != null ? validNotesId.toString() : ""))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일지 생성")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void 일지_생성() throws Exception {
        RequestNotesDTO requestNotesDTO = new RequestNotesDTO();
        requestNotesDTO.setMemberId(1L);
        requestNotesDTO.setDate(new Date());
        requestNotesDTO.setSummary("asd");
        requestNotesDTO.setContent("aa");
        requestNotesDTO.setType(NotesType.valueOf("Blog"));

        mvc.perform(post("/api/notes/insert")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestNotesDTO)))
                .andExpect(status().isBadRequest());

        requestNotesDTO.setTitle("a");
        mvc.perform(post("/api/notes/insert")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestNotesDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("일지 수정")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void 일지_수정() throws Exception {
        RequestNotesDTO requestNotesDTO = new RequestNotesDTO();
        requestNotesDTO.setNotesId(1L);
        requestNotesDTO.setMemberId(1L);
        requestNotesDTO.setDate(new Date());
        requestNotesDTO.setSummary("asd");
        requestNotesDTO.setContent("aa");
        requestNotesDTO.setType(NotesType.valueOf("Blog"));

        mvc.perform(put("/api/notes/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestNotesDTO)))
                .andExpect(status().isBadRequest());

        //null 해결
        requestNotesDTO.setTitle("a");

        /**
         * 성공
         */
        mvc.perform(put("/api/notes/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(requestNotesDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("일지 삭제")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void 일지_삭제() throws Exception{
        Long invalidNotesId = null;

        mvc.perform(delete("/api/notes/delete")
                        .with(csrf())
                        .param("notesId", invalidNotesId != null ? invalidNotesId.toString() : ""))
                .andExpect(status().isBadRequest());

        Long validNotesId = 1L;
        Long validTeamMemberId = 1L;
        mvc.perform(delete("/api/notes/delete")
                        .with(csrf())
                        .param("notesId", validNotesId != null ? validNotesId.toString() : "")
                        .param("teamMemberId", validTeamMemberId != null ? validTeamMemberId.toString() : ""))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("이미지 저장")
    @WithMockUser(username = "테스트_최고관리자", roles = {"SUPER"})
    void 이미지_저장() throws Exception {

        // Mock 이미지 파일을 생성합니다.
        MockMultipartFile file = new MockMultipartFile(
                "file",      // 파일 파라미터 이름
                "test.jpg",  // 파일 이름
                "image/jpeg",  // 콘텐츠 타입
                "test data".getBytes() // 파일 데이터
        );

        // 이미지 저장 요청을 수행합니다.
        mvc.perform(MockMvcRequestBuilders.multipart("/api/notes/image")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isCreated()); // 응답 상태가 Created(201)인지 확인합니다.
    }

}