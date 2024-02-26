package com.prolog.prologbackend.Notes.Controller;

import com.prolog.prologbackend.Notes.DTO.NotesListResponseDTO;
import com.prolog.prologbackend.Notes.Service.NotesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author : Kim
 * Date : 2024-02-16
 * Description : 일지 관련 API입니
 * 팀멤버 ID를 가져와서 프로젝트ID와 함께 일지 조회
*/
@RestController
@RequiredArgsConstructor
@Tag(name = "Project Notes API", description = "특정 프로젝트에서 사용자가 작성한 일지 정보 반환")
public class NotesController {
    private final NotesService notesService;

    @Operation(summary = "프로젝트에서 사용자가 작성한 일지를 반환합니다.")
    @GetMapping("/notes-list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success" , content = @Content(schema = @Schema(implementation = NotesListResponseDTO.class)))
    })
    public ResponseEntity<NotesListResponseDTO> getProjectList(
            @Parameter(name = "팀멤버ID", description = "팀멤버 구분 번호입니다.", example = "1", required = true)
            @RequestParam Long memberId,
            @Parameter(name = "프로젝트ID", description = "프로젝트 구분 번호입니다.", example = "1", required = true)
            @RequestParam Long projectId

    ){
        NotesListResponseDTO notesList = notesService.getProjectNotes(memberId,projectId);
        return ResponseEntity.status(HttpStatus.OK).body(notesList);
    }
}
