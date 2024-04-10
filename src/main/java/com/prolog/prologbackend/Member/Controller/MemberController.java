package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Member.DTO.Request.MemberUpdateDto;
import com.prolog.prologbackend.Member.DTO.Response.SimpleMemberDto;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @Operation(summary = "로그인한 사용자 정보 조회 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 사용자 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation=SimpleMemberDto.class)))
    })
    @GetMapping("/information")
    ResponseEntity getMember(@AuthenticationPrincipal Member member){
        return ResponseEntity.status(HttpStatus.OK).body(SimpleMemberDto.of(member));
    }

    @Operation(summary = "회원 정보 수정 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 정보 수정 성공"),
            @ApiResponse(responseCode = "409", description = "Conflict : 변경하려는 이메일이 이미 사용중임",
                    content = @Content(schema = @Schema(implementation=Void.class)))
    })
    @PatchMapping("/information")
    ResponseEntity updateMember(@AuthenticationPrincipal Member member,
                                @RequestBody @Valid MemberUpdateDto dto){
        memberService.updateMember(member.getEmail(), dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "회원 탈퇴 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 회원 탈퇴 성공")
    })
    @DeleteMapping
    ResponseEntity withdrawMember(@AuthenticationPrincipal Member member){
        memberService.deleteMember(member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/image")
    ResponseEntity updateProfileImage(@AuthenticationPrincipal Member member,
                                            @RequestPart(value = "image") MultipartFile image){
        if(image.isEmpty())
            memberService.resetProfileImage(member.getEmail());
        else
            memberService.updateProfileImage(member.getEmail(), image);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
