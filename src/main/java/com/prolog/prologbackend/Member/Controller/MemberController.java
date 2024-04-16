package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Exception.ErrorResponse;
import com.prolog.prologbackend.Member.DTO.Request.MemberUpdateDto;
import com.prolog.prologbackend.Member.DTO.Response.SimpleMemberDto;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Service.MemberService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name="멤버 관련 API (인가 필요)", description = "권한이 있는 사용자만 접근할 수 있는 요청과 관련된 멤버 API 문서입니다.")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "사용자 정보 조회 메서드", description = "로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 사용자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation=SimpleMemberDto.class)))
    })
    @GetMapping("/information")
    ResponseEntity getMember(@AuthenticationPrincipal Member member){
        return ResponseEntity.status(HttpStatus.OK).body(SimpleMemberDto.of(member));
    }

    @Operation(summary = "회원 정보 수정 메서드", description = "사용자는 자신의 정보 중 일부 또는 전부를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 정보 수정 성공"),
            @ApiResponse(responseCode = "409", description = "Conflict : 변경하려는 이메일이 이미 사용중임",
                    content = @Content(schema = @Schema(implementation= ErrorResponse.class)))
    })
    @PatchMapping("/information")
    ResponseEntity<Void> updateMember(@AuthenticationPrincipal Member member,
                                @RequestBody @Valid MemberUpdateDto dto){
        memberService.updateMember(member.getEmail(), dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "회원 탈퇴 메서드", description = "서비스 이용 중지를 위한 회원 탈퇴를 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 회원 탈퇴 성공")
    })
    @DeleteMapping
    ResponseEntity<Void> withdrawMember(@AuthenticationPrincipal Member member){
        memberService.deleteMember(member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "프로필 이미지 수정 메서드", description = "프로필 이미지의 수정 또는 초기화를 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 이미지 수정 성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request : S3 이미지 등록 실패",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found : 일치하는 회원이 없음",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflict : 이미 기본이미지인 경우",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class)))
    })
    @PatchMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Void> updateProfileImage(
            @AuthenticationPrincipal Member member,
            @Parameter(description = "이미지 수정을 위해 새로 적용할 multipart/form-data 형식의 이미지 파일")
            @RequestPart(value = "image") MultipartFile image){
        if(image.isEmpty())
            memberService.resetProfileImage(member.getEmail());
        else
            memberService.updateProfileImage(member.getEmail(), image);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
