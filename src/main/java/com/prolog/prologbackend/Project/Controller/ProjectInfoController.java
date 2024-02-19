package com.prolog.prologbackend.Project.Controller;

import com.prolog.prologbackend.Project.DTO.Request.RequestProjectDetailDTO;
import com.prolog.prologbackend.Project.DTO.Response.ProjectListResponseDTO;
import com.prolog.prologbackend.Project.DTO.Response.ResponseProjectDetailDTO;
import com.prolog.prologbackend.Project.Service.ProjectInfoService;
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
import org.springframework.web.bind.annotation.*;

/**
 * Author : Kim
 * Date : 2024-02-17
 * Description : 특정 프로젝트에 대한 정보를 가져옵니다
 * info - 프로젝트 ID로 특정 프로젝트의 정보들과 팀멤버에서 프로젝트 ID로 팀원들을 조회해옵니다.
 * info - 프로젝트 ID로 특정 프로젝트의 단계들을 조회해옵니다.
 * info - 가져온 project의 스택으로 프로젝트 스택 테이블에서 검색해서 또 줘야됨
 * update - info 가져와서 보여주고 변경된 info 받아와서 저장
 * list - 회원ID로 팀멤버 테이블에서 회원ID가 같은 row의 프로젝트 id(여러개)를 찾아서 프로젝트 테이블에서 프로젝트 id로 검색
 */

@Tag(name = "Project Info API", description = "프로젝트 관련 API문서입니다.")

@RestController
@RequiredArgsConstructor
public class ProjectInfoController {
    private final ProjectInfoService projectInfoService;

    @Operation(summary = "특정 프로젝트의 상세 정보를 가져옵니다.")
    @GetMapping("/project-info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success" , content = @Content(schema = @Schema(implementation = ResponseProjectDetailDTO.class)))
    })
    public ResponseEntity<ResponseProjectDetailDTO> getProject(
            @Parameter(name = "프로젝트 ID", description = "프로젝트 구분 번호입니다.", example = "1", required = true)
            @RequestParam Long projectId
    ){
        ResponseProjectDetailDTO projectDetailDTO = projectInfoService.getProjectInfo(projectId);
        return ResponseEntity.status(HttpStatus.OK).body(projectDetailDTO);
    }


    @Operation(summary = "특정 프로젝트의 정보를 수정합니다.")
    @PutMapping("/project-update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<Void> projectUpdate(
            @Parameter(name = "프로젝트 ID", description = "프로젝트 구분 번호입니다.", example = "1", required = true)
            @RequestBody RequestProjectDetailDTO requestProjectDetailDTO
            ){
        boolean success = projectInfoService.projectUpdate(requestProjectDetailDTO);
        if(success) {
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "사용자가 포함된 프로젝트 리스트를 반환합니다.")
    @GetMapping("/project-list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success" , content = @Content(schema = @Schema(implementation = ProjectListResponseDTO.class)))
    })
    public ResponseEntity<ProjectListResponseDTO> getProjectList(
            @Parameter(name = "유저ID", description = "유저 구분 번호입니다.", example = "1", required = true)
            @RequestParam Long userId
    ){
        ProjectListResponseDTO projectList = projectInfoService.getProjectList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(projectList);
    }
}
