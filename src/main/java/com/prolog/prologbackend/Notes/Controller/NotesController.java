package com.prolog.prologbackend.Notes.Controller;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Notes.DTO.Request.RequestNotesDTO;
import com.prolog.prologbackend.Notes.DTO.Response.ResponseNotesDTO;
import com.prolog.prologbackend.Notes.DTO.Response.ResponseNotesListDTO;
import com.prolog.prologbackend.Notes.ExceptionType.ImageExceptionType;
import com.prolog.prologbackend.Notes.ExceptionType.NotesExceptionType;
import com.prolog.prologbackend.Notes.Service.NotesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;


@Tag(name = "Project Notes API", description = "일지 관련 API입니다")
@RestController
@RequiredArgsConstructor
public class NotesController {
    private final NotesService notesService;

    @Operation(summary = "프로젝트에서 사용자가 작성한 일지 리스트를 반환합니다.")
    @GetMapping("/notes/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success" ,
                    content = @Content(schema = @Schema(implementation = ResponseNotesListDTO.class)))
    })
    public ResponseEntity<ResponseNotesListDTO> getNotesList(
            @Parameter(name = "팀멤버ID", description = "팀멤버 구분 번호입니다.", example = "1", required = true)
            @RequestParam(name = "teamMemberId", required = false) Long teamMemberId
    ) {
        if(Objects.isNull(teamMemberId)){
            throw new BusinessLogicException(NotesExceptionType.INVALID_INPUT_VALUE);
        }
        ResponseNotesListDTO notesList = notesService.getNotesList(teamMemberId);
        return ResponseEntity.status(HttpStatus.OK).body(notesList);
    }

    @Operation(summary = "프로젝트에서 사용자가 작성한 일지를 반환합니다.")
    @GetMapping("/notes/info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success" ,
                    content = @Content(schema = @Schema(implementation = ResponseNotesListDTO.class)))
    })
    public ResponseEntity<ResponseNotesDTO> getNotes(
            @Parameter(name = "일지 ID", description = "일지 구분 번호입니다.", example = "1", required = true)
            @RequestParam(name = "notesId", required = false) Long notesId
    ){
        if(Objects.isNull(notesId)) throw new BusinessLogicException(NotesExceptionType.INVALID_INPUT_VALUE);
        return ResponseEntity.status(HttpStatus.OK).body(notesService.getNotes(notesId));
    }

    @Operation(summary = "일지를 저장하고 저장된 일지의 id를 반환합니다.")
    @PostMapping("/api/notes/insert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<Void> createNotes(
            @Parameter(name = "requestNotesDTO", description = "일지의 내용이 들어갑니다.", required = true)
            @Valid @RequestBody RequestNotesDTO requestNotesDTO,
            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors() || requestNotesDTO.getDate() == null){
            throw new BusinessLogicException(NotesExceptionType.INVALID_INPUT_VALUE);
        }
        notesService.createNotes(requestNotesDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "수정된 일지 정보를 받아 저장합니다.")
    @PutMapping("/notes/update") // + /api
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<Void> updateNotes(
            @Parameter(name = "requestNotesDTO", description = "일지의 수정된 내용이 들어갑니다.", required = true)
            @Valid @RequestBody RequestNotesDTO requestNotesDTO,
            BindingResult bindingResult
    ){
        if(bindingResult.hasErrors() || requestNotesDTO.getDate() == null){
            throw new BusinessLogicException(NotesExceptionType.INVALID_INPUT_VALUE);
        }
        notesService.updateNotes(requestNotesDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "해당 일지를 삭제하고 status 코드를 전송합니다.")
    @DeleteMapping("/api/notes/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<Void> deleteNotes(
            @Parameter(name = "일지 ID", description = "일지 구분 번호입니다.", example = "1", required = true)
            @RequestParam(name = "notesId", required = false) Long notesId,
            @Parameter(name = "팀 멤버 ID", description = "팀 멤버의 구분 번호입니다.", example = "1", required = true)
            @RequestParam(name = "teamMemberId", required = false) Long teamMemberId
    ){
        if(Objects.isNull(notesId) || Objects.isNull(teamMemberId)) {
            throw new BusinessLogicException(NotesExceptionType.INVALID_INPUT_VALUE);
        }
        notesService.deleteNotes(notesId,teamMemberId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "일지에 사용되는 이미지를 S3에 저장하고 이미지의 url을 전달해줍니다.")
    @PostMapping("/api/notes/image")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<URL> saveNotesImage(
            @Parameter(name = "file", description = "이미지를 받습니다.", required = true)
            @RequestPart(value = "file") MultipartFile file
    ) throws IOException {
        if(file.isEmpty()) throw new BusinessLogicException(ImageExceptionType.NOTES_NOT_FOUND);
        URL url = notesService.saveImage(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }
}
