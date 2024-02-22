package com.prolog.prologbackend.Project.Controller;

import com.prolog.prologbackend.Exception.BusinessLogicException;
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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Objects;

/**
 * Author : Kim
 * Date : 2024-02-20
 * Description : 특정 프로젝트에 대한 정보를 가져옵니다
 * Get - 프로젝트 ID로 특정 프로젝트의 정보들과 팀멤버에서 프로젝트 ID로 팀원들을 조회해옵니다.
 * Get - 프로젝트 ID로 특정 프로젝트의 단계들을 조회해옵니다.
 * Get - 가져온 project의 스택으로 프로젝트 스택 테이블에서 검색해서 또 줘야됨
 * update - info 가져와서 보여주고 변경된 info 받아와서 저장
 * GetList - 회원ID로 팀멤버 테이블에서 회원ID가 같은 row의 프로젝트 id(여러개)를 찾아서 프로젝트 테이블에서 프로젝트 id로 검색
 * Delete - 프로젝트 ID 받아서 그 프로젝트의 is_deleted를 true? 스택과 단계는 건들 필요 없음
 * Create - 프로젝트ID, 생성일, 수정일, 삭제여부를 제외한 모든 데이터를 받아와야됨 스택[1,2,3,4], 단계 포함
 * Create - 프로젝트 ID를 반환하여 생성된 프로젝트로 바로 이동?
 *
 * 프로젝트 정보 줄때 is_deleted 도 전달해줘야됨 아니면 service에서 그냥 isdeleted가 false인것만 전달하든가
 */

@Tag(name = "Project Info API", description = "프로젝트 관련 API문서입니다.")

@Tag(name = "Project Info API", description = "프로젝트 관련 API문서입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/project")
public class ProjectController {
    private final ProjectService projectService;

    @Operation(summary = "특정 프로젝트의 상세 정보를 가져옵니다.")
    @GetMapping("/info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success" , content = @Content(schema = @Schema(implementation = ResponseProjectDetailDTO.class)))
    })
    public ResponseEntity<ResponseProjectDetailDTO> getProject(
            @Parameter(name = "프로젝트 ID", description = "프로젝트 구분 번호입니다.", example = "1", required = true)
            //@PathVariable가 낫나?
            @RequestParam(name = "projectId", required = false) Long projectId
    ){
        if(Objects.isNull(projectId)) throw new BusinessLogicException(ProjectExceptionType.INVALID_INPUT_VALUE);
        ResponseProjectDetailDTO projectDetailDTO = projectService.getProjectInfo(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(projectDetailDTO);
    }


    @Operation(summary = "특정 프로젝트의 정보를 수정합니다.")
    @PutMapping("/update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<Void> projectUpdate(
            @Parameter(name = "프로젝트 ID", description = "프로젝트 구분 번호입니다.", example = "1", required = true)
            @Valid @RequestBody RequestProjectDetailDTO requestProjectDetailDTO,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors() || requestProjectDetailDTO.getProjectId() == null) {
            throw new BusinessLogicException(ProjectExceptionType.INVALID_INPUT_VALUE);
        }
        projectService.projectUpdate(requestProjectDetailDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    @Operation(summary = "사용자가 포함된 프로젝트 리스트를 반환합니다.")
    @GetMapping("/list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success" , content = @Content(schema = @Schema(implementation = ProjectListResponseDTO.class))),
            @ApiResponse(responseCode = "204", description = "No Content" , content = @Content(schema = @Schema(implementation = ProjectListResponseDTO.class)))
    })
    public ResponseEntity<ProjectListResponseDTO> getProjectList(
            @Parameter(name = "유저ID", description = "유저 구분 번호입니다.", example = "1", required = true)
            @RequestParam(name = "userId", required = false) Long userId
    ){
        if(Objects.isNull(userId)) throw new BusinessLogicException(ProjectExceptionType.INVALID_INPUT_VALUE);
        ProjectListResponseDTO projectList = projectService.getProjectList(userId);

        if (Objects.isNull(projectList) || projectList.getProjectList().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(projectList);
        } else{
            return ResponseEntity.status(HttpStatus.OK).body(projectList);
        }
    }


    @Operation(summary = "프로젝트를 생성하고 생성된 프로젝트의 ProjectID를 반환합니다.")
    @PostMapping("/insert")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
    })
    public ResponseEntity<Long> createProject(
            @Parameter(name = "프로젝트 데이터", description = "프로젝트의 세부 데이터를 받아 저장하고 ProjectID를 반환합니다.", required = true)
            @Valid @RequestBody RequestProjectDetailDTO projectDetailDTO,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors() || projectDetailDTO.getStartedDate() == null || projectDetailDTO.getEndedDate() == null) {
            throw new BusinessLogicException(ProjectExceptionType.INVALID_INPUT_VALUE);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectDetailDTO));
    }


    @Operation(summary = "프로젝트를 삭제하고 HttpStatus를 반환합니다.")
    @PutMapping("/delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    public ResponseEntity<Void> deleteProject(
            @Parameter(name = "Project information", description = "Information of the project to be created.", required = true)
            @RequestParam(name = "projectId", required = false) Long projectId
    ){
        if(Objects.isNull(projectId)) throw new BusinessLogicException(ProjectExceptionType.INVALID_INPUT_VALUE);
        projectService.deleteProject(projectId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
