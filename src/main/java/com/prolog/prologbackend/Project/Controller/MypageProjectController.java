package com.prolog.prologbackend.Project.Controller;

import com.prolog.prologbackend.Project.DTO.ProjectListResponseDTO;
import com.prolog.prologbackend.Project.Service.MypageProjectService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "MyPage Project Api", description = "MyPage 페이지에서 프로젝트 관련 API들입니다.")
@RestController
@RequestMapping(value = "/mypage")
@RequiredArgsConstructor
public class MypageProjectController {
    private final MypageProjectService mypageProjectService;


    @Operation(summary = "마이페이지에서 사용자의 프로젝트 리스트를 반환합니다.")
    @GetMapping("/project-list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success" , content = @Content(schema = @Schema(implementation = ProjectListResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Null"),
    })
    public ResponseEntity<ProjectListResponseDTO> getProjectList(
            @Parameter(name = "email", description = "유저의 이메일입니다.", example = "abcd0000@naver.com", required = true)
            @RequestBody Long userId
    ){
        ProjectListResponseDTO projectList = mypageProjectService.getProjectList(userId);
        return ResponseEntity.status(HttpStatus.OK).body(projectList);
    }
}
