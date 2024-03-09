package com.prolog.prologbackend.Project.Controller;

import com.prolog.prologbackend.Exception.BusinessLogicException;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Project.DTO.Request.RequestProjectDetailDTO;
import com.prolog.prologbackend.Project.DTO.Response.ProjectListResponseDTO;
import com.prolog.prologbackend.Project.DTO.Response.ResponseProjectDetailDTO;
import com.prolog.prologbackend.Project.ExceptionType.ProjectExceptionType;
import com.prolog.prologbackend.Project.Service.ProjectService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

@Tag(name = "Project Info API", description = "프로젝트 관련 API문서입니다.")
@RestController
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "특정 프로젝트의 상세 정보를 가져옵니다.")
    @GetMapping("/project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success" ,
                    content = @Content(schema = @Schema(implementation = ResponseProjectDetailDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<ResponseProjectDetailDTO> getProject(
            @Parameter(name = "프로젝트 ID", description = "프로젝트 구분 번호입니다.", example = "1", required = true)
            @RequestParam(name = "projectId", required = false) Long projectId
    ){
        if(Objects.isNull(projectId)) throw new BusinessLogicException(ProjectExceptionType.INVALID_INPUT_VALUE);
        ResponseProjectDetailDTO projectDetailDTO = projectService.getProjectInfo(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(projectDetailDTO);
    }


    @Operation(summary = "특정 프로젝트의 정보를 수정합니다.")
    @PutMapping("/api/project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<Void> projectUpdate(
            @AuthenticationPrincipal Member member,
            @Parameter(name = "프로젝트 정보", description = "수정된 프로젝트의 정보가 들어있습니다.", required = true)
            @Valid @RequestBody RequestProjectDetailDTO requestProjectDetailDTO,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors() || requestProjectDetailDTO.getProjectId() == null) {
            throw new BusinessLogicException(ProjectExceptionType.INVALID_INPUT_VALUE);
        }

        projectService.projectUpdate(requestProjectDetailDTO,member.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(summary = "사용자가 포함된 프로젝트 리스트를 반환합니다.")
    @GetMapping("/project/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success" ,
                    content = @Content(schema = @Schema(implementation = ProjectListResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "No Content" ,
                    content = @Content(schema = @Schema(implementation = ProjectListResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request"),
    })
    public ResponseEntity<ProjectListResponseDTO> getProjectList(
            @Parameter(name = "memberId", description = "멤버를 구분하는 ID.", required = true)
            @RequestParam(name = "memberId", required = false) Long memberId
    ){
        ProjectListResponseDTO projectList = projectService.getProjectList(memberId);

        if (projectList == null || projectList.getProjectList().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(projectList);
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(projectList);
        }
    }


    @Operation(summary = "프로젝트를 생성하고 생성된 프로젝트의 ProjectID를 반환합니다.")
    @PostMapping("/api/project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
    })
    public ResponseEntity<Long> createProject(
            @Parameter(name = "프로젝트 데이터", description = "프로젝트의 세부 데이터입니다.", required = true)
            @Valid @RequestBody RequestProjectDetailDTO projectDetailDTO,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors() || projectDetailDTO.getStartedDate() == null || projectDetailDTO.getEndedDate() == null) {
            throw new BusinessLogicException(ProjectExceptionType.INVALID_INPUT_VALUE);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectDetailDTO));
    }


    @Operation(summary = "프로젝트를 삭제하고 HttpStatus를 반환합니다.")
    @PutMapping("/api/project/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<Void> deleteProject(
            @AuthenticationPrincipal Member member,
            @Parameter(name = "ProjectId", description = "프로젝트를 구분하는 ID.", required = true)
            @RequestParam(name = "projectId", required = false) Long projectId
    ){
        if(Objects.isNull(projectId) || Objects.isNull(member.getId())){
            throw new BusinessLogicException(ProjectExceptionType.INVALID_INPUT_VALUE);
        }
        projectService.deleteProject(projectId,member.getId());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}