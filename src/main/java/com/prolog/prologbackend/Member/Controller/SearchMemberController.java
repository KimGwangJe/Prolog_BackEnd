package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Member.DTO.Request.PasswordUpdateDto;
import com.prolog.prologbackend.Member.Service.SearchMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="멤버 정보 찾기 관련 API (인가 불필요)", description = "로그인을 위한 이메일 및 비밀번호 찾기 관련 API 문서입니다.")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchMemberController {
    private final SearchMemberService searchMemberService;

    @Operation(summary = "이메일 찾기 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 이메일 찾기 완료"),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=Void.class)))
    })
    @Parameter(name = "회원 닉네임", description = "회원 정보 확인을 위한 닉네임", example = "kimLeeChoi21", required = true)
    @Parameter(name = "회원 핸드폰 번호", description = "회원 정보 확인을 위한 핸드폰 번호", example = "010-1234-5678", required = true)
    @GetMapping("/email")
    ResponseEntity searchEmail(@RequestParam @NotBlank String nickname, @RequestParam @NotBlank String phone){
        String email = searchMemberService.findEmail(nickname, phone);
        return ResponseEntity.status(HttpStatus.OK).body(email);
    }

    @Operation(summary = "인증 번호 발급 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created : 인증 번호 발급 후 전송 완료"),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=Void.class)))
    })
    @Parameter(name = "회원 이메일", description = "인증 번호를 발급할 이메일 주소", example = "kimLeeChoi@mail.com", required = true)
    @PostMapping("/password/certification")
    ResponseEntity issueCertificationNumber(@RequestParam @Email String email){
        searchMemberService.issueCertificationNumber(email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "인증 번호 확인 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 인증 성공"),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=Void.class)))
    })
    @Parameter(name = "회원 이메일", description = "인증 번호를 발급한 이메일 주소", example = "kimLeeChoi@mail.com", required = true)
    @Parameter(name = "인증 번호", description = "발급받은 인증 번호", example = "4865", required = true)
    @GetMapping("/password/certification")
    ResponseEntity checkCertificationNumber(@RequestParam @Email String email, @RequestParam int code){
        searchMemberService.checkCertificationNumber(email, code);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "인증 여부 확인 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 인증 여부 확인됨"),
            @ApiResponse(responseCode = "400", description = "Bad Request : 닉네임이 일치하지 않음",
                    content = @Content(schema = @Schema(implementation=Void.class))),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=Void.class)))
    })
    @Parameter(name = "회원 이메일", description = "회원 정보 확인을 위한 이메일 주소", example = "kimLeeChoi@mail.com", required = true)
    @Parameter(name = "회원 닉네임", description = "회원 정보 확인을 위한 닉네임", example = "kimLeeChoi21", required = true)
    @GetMapping("/password")
    ResponseEntity checkCertificationStatus(@RequestParam @Email String email, @RequestParam String nickname){
        searchMemberService.checkCertificationStatus(nickname, email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "비밀번호 재설정 메서드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 비밀번호 변경 성공"),
            @ApiResponse(responseCode = "401", description = "Unauthorized : 이메일 인증 필요",
                    content = @Content(schema = @Schema(implementation=Void.class))),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=Void.class)))
    })
    @PatchMapping("/password")
    ResponseEntity updatePassword(@RequestBody PasswordUpdateDto passwordUpdateDto){
        searchMemberService.updatePassword(passwordUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
