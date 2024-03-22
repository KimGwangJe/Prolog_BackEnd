package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Member.DTO.Request.MemberJoinDto;
import com.prolog.prologbackend.Member.Service.AnyMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name="멤버 관련 API (인가 불필요)", description = "모든 사용자가 접근할 수 있는 요청과 관련된 멤버 API 문서입니다.")
@RestController
@RequiredArgsConstructor
public class AnyMemberController {
    private final AnyMemberService anyMemberService;

    @Operation(summary = "회원 가입 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created : 일반 회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "Conflict : 이미 존재하는 회원",
                    content = @Content(schema = @Schema(implementation=Void.class)))
    })
    @PostMapping("/signup")
    ResponseEntity joinMember(@Valid @RequestBody MemberJoinDto joinDto){
        anyMemberService.joinMember(joinDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "카카오 소셜 회원가입 및 로그인 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created : 소셜 로그인 성공")
    })
    @Parameter(name = "인증 코드", description = "카카오 서버 인증에 필요한 코드", example = "kakaoCodeKakaoCode", required = true)
    @PostMapping("/oauth/kakao/login")
    ResponseEntity socialLoginMember(@RequestParam String code){
        Map tokens = anyMemberService.loginToKaKao(code);
        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }

    @Operation(summary = "회원 가입 시 이메일 검증 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 사용 가능한 이메일"),
            @ApiResponse(responseCode = "409", description = "Conflict : 이미 사용중인 이메일",
                    content = @Content(schema = @Schema(implementation=Void.class)))
    })
    @Parameter(name = "회원 이메일", description = "사용중인지 확인하고 싶은 이메일 주소", example = "kimLeeChoi@mail.com", required = true)
    @GetMapping("/email")
    ResponseEntity validateEmail(@RequestParam @Email String email){
        if (anyMemberService.validateEmail(email))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        else
            return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/nickname")
    ResponseEntity validateNickname(@RequestParam @NotBlank @Size(max=20, message="닉네임은 20자 미만으로 작성해야 합니다.") String nickname) {
        if(anyMemberService.validateNickname(nickname))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        else
            return ResponseEntity.status(HttpStatus.OK).build();
    }
}
