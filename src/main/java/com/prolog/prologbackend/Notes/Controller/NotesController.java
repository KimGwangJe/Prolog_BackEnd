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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;


@Tag(name = "일지 관련 API", description = "일지 관련 API입니다")
@RestController
@RequiredArgsConstructor
public class NotesController {
    private final NotesService notesService;

    @Operation(summary = "프로젝트에서 사용자가 작성한 일지 리스트를 반환합니다.")
    @GetMapping("/notes/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "204", description = "No Content"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = Void.class)))

    })
    public ResponseEntity<ResponseNotesListDTO> getNotesList(
            @Parameter(name = "teamMemberId", description = "팀멤버 구분 번호입니다.", example = "1", required = true)
            @RequestParam(name = "teamMemberId", required = false) Long teamMemberId
    ) {
        if(Objects.isNull(teamMemberId)){
            throw new BusinessLogicException(NotesExceptionType.INVALID_INPUT_VALUE);
        }
        ResponseNotesListDTO notesList = notesService.getNotesList(teamMemberId);
        if(notesList.getNotesList().isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(notesList);
    }

    @Operation(summary = "프로젝트에서 사용자가 작성한 일지를 반환합니다.")
    @GetMapping("/notes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<ResponseNotesDTO> getNotes(
            @Parameter(name = "notesId", description = "일지 구분 번호입니다.", example = "1", required = true)
            @RequestParam(name = "notesId", required = false) Long notesId
    ){
        if(Objects.isNull(notesId)) throw new BusinessLogicException(NotesExceptionType.INVALID_INPUT_VALUE);
        return ResponseEntity.status(HttpStatus.OK).body(notesService.getNotes(notesId));
    }

    @Operation(summary = "일지를 저장하고 저장된 일지의 id를 반환합니다.")
    @PostMapping("/api/notes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Long> createNotes(
            @Parameter(name = "requestNotesDTO", description = "일지의 내용이 들어갑니다.", required = true)
            @Valid @RequestBody RequestNotesDTO requestNotesDTO,
            BindingResult bindingResult
    ) {
        if(bindingResult.hasErrors() || requestNotesDTO.getDate() == null){
            throw new BusinessLogicException(NotesExceptionType.INVALID_INPUT_VALUE);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(notesService.createNotes(requestNotesDTO));
    }

    @Operation(summary = "수정된 일지 정보를 받아 저장합니다.")
    @PutMapping("/api/notes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = Void.class)))
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
    @DeleteMapping("/api/notes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<Void> deleteNotes(
            @Parameter(name = "notesId", description = "일지 구분 번호입니다.", example = "1", required = true)
            @RequestParam(name = "notesId", required = false) Long notesId,
            @Parameter(name = "teamMemberId", description = "팀 멤버의 구분 번호입니다.", example = "1", required = true)
            @RequestParam(name = "teamMemberId", required = false) Long teamMemberId
    ){
        if(Objects.isNull(notesId) || Objects.isNull(teamMemberId)) {
            throw new BusinessLogicException(NotesExceptionType.INVALID_INPUT_VALUE);
        }
        notesService.deleteNotes(notesId,teamMemberId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "일지에 사용되는 이미지를 S3에 저장하고 이미지의 url을 전달해줍니다.")
    @PostMapping(value = "/api/notes/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = Void.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(schema = @Schema(implementation = Void.class)))
    })
    public ResponseEntity<URL> saveNotesImage(
            @Parameter(description = "multipart/form-data 형식의 이미지 리스트를 input으로 받습니다. 이때 key 값은 file 입니다.")
            @RequestPart(value = "file") MultipartFile file
    ) throws IOException {
        if(file.isEmpty()) throw new BusinessLogicException(ImageExceptionType.NOTES_NOT_FOUND);
        URL url = notesService.saveImage(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }
}
