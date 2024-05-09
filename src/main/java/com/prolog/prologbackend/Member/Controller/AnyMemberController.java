package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Exception.ErrorResponse;
import com.prolog.prologbackend.Member.DTO.Request.MemberJoinDto;
import com.prolog.prologbackend.Member.Service.Facade.AnyMemberFacadeService;
import com.prolog.prologbackend.Security.Jwt.JwtType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="멤버 관련 API (인가 불필요)", description = "모든 사용자가 접근할 수 있는 요청과 관련된 멤버 API 문서입니다.")
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class AnyMemberController {
    private final AnyMemberFacadeService anyMemberFacadeService;

    @Operation(summary = "팀원 조회", description = "초대를 원하는 팀원의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 팀원 조회 성공"),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class)))
    })
    @GetMapping("/email")
    ResponseEntity findMemberByEmail(
            @Parameter(description = "초대하려는 팀원의 이메일 주소", example = "kimLeeChoi@mail.com", required = true)
            @RequestParam @Email String email){
        return ResponseEntity.status(HttpStatus.OK).body(anyMemberFacadeService.getMemberByEmail(email));
    }

    @Operation(summary = "일반 회원 가입 메서드", description = "일반 회원 가입을 통해 회원을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created : 일반 회원가입 성공"),
            @ApiResponse(responseCode = "409", description = "Conflict : 이미 존재하는 회원",
                    content = @Content(schema = @Schema(implementation= ErrorResponse.class)))
    })
    @PostMapping("/signup")
    ResponseEntity<Void> joinMember(@Valid @RequestBody MemberJoinDto joinDto){
        anyMemberFacadeService.joinMember(joinDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "카카오 소셜 회원가입 및 로그인", description = "카카오 소셜 로그인을 통해 회원 등록 및 로그인 합니다.")
    @ApiResponse(responseCode = "201", description = "Created : 소셜 로그인 성공")
    @PostMapping("/login/oauth/kakao")
    ResponseEntity<Void> socialLoginMember(
            @Parameter(description = "카카오 서버 인증에 필요한 코드", example = "kakaoCodeKakaoCode", required = true)
            @RequestParam String code, HttpServletResponse response){
        String[] tokens = anyMemberFacadeService.loginToKaKao(code);
        response.addHeader(JwtType.ACCESS_TOKEN.getTokenType(),tokens[0]);
        response.addHeader(JwtType.REFRESH_TOKEN.getTokenType(),tokens[1]);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "이메일 사용 가능 여부 조회", description = "사용 가능한 이메일인지 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 사용 가능한 이메일"),
            @ApiResponse(responseCode = "409", description = "Conflict : 이미 사용중인 이메일",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class)))
    })
    @GetMapping("/validation/email")
    ResponseEntity<Void> validateEmail(
            @Parameter(description = "사용중인지 확인하고 싶은 이메일 주소", example = "kimLeeChoi@mail.com", required = true)
            @RequestParam @Email String email){
        if (anyMemberFacadeService.validateEmail(email))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        else
            return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "닉네임 사용 가능 여부 조회", description = "사용 가능한 닉네임인지 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 사용 가능한 닉네임"),
            @ApiResponse(responseCode = "409", description = "Conflict : 이미 사용중인 닉네임",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class)))
    })
    @GetMapping("/validation/nickname")
    ResponseEntity<Void> validateNickname(
            @Parameter(description = "사용중인지 확인하고 싶은 닉네임", example = "kimLeeChoi21", required = true)
            @RequestParam @NotBlank @Size(max=20, message="닉네임은 20자 미만으로 작성해야 합니다.") String nickname) {
        if(anyMemberFacadeService.validateNickname(nickname))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        else
            return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "이메일 검증", description = "서비스 이용을 위한 회원 가입한 이메일의 검증을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 이메일 인증 성공"),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflict : 이미 인증된 이메일",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class)))
    })
    @PatchMapping("/verification/{token}")
    ResponseEntity<Void> verificationEmail(
            @Schema(description = "이메일 인증을 위해 발급받은 토큰", example = "verificationTokenVerificationEmailToken")
            @PathVariable String token){
        anyMemberFacadeService.verificationEmail(token);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
