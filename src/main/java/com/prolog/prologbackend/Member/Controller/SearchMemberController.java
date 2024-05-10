package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Exception.ErrorResponse;
import com.prolog.prologbackend.Member.DTO.Request.PasswordUpdateDto;
import com.prolog.prologbackend.Member.Service.Facade.SearchMemberFacadeService;
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
    private final SearchMemberFacadeService searchMemberFacadeService;

    @Operation(summary = "이메일 찾기 메서드", description = "사용자 정보를 이용하여 올바른 사용자에 한해 이메일을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 이메일 찾기 완료"),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class)))
    })
    @GetMapping("/email")
    ResponseEntity searchEmail(
            @Parameter(description = "회원 정보 확인을 위한 닉네임", example = "kimLeeChoi21", required = true)
            @RequestParam @NotBlank String nickname,
            @Parameter(description = "회원 정보 확인을 위한 핸드폰 번호", example = "010-1234-5678", required = true)
            @RequestParam @NotBlank String phone){
        return ResponseEntity.status(HttpStatus.OK).body(searchMemberFacadeService.findEmail(nickname, phone));
    }

    @Operation(summary = "인증 번호 발급 메서드", description = "비밀 번호 재설정을 위한 본인 인증 번호를 이메일로 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created : 인증 번호 발급 후 전송 완료"),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=Void.class)))
    })
    @PostMapping("/password/certification")
    ResponseEntity<Void> issueCertificationNumber(
            @Parameter(description = "인증 번호를 발급할 이메일 주소", example = "kimLeeChoi@mail.com", required = true)
            @RequestParam @Email String email){
        searchMemberFacadeService.issueCertificationNumber(email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "인증 번호 확인 메서드", description = "비밀 번호 재설정을 위해 전송된 인증 번호를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 인증 성공"),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class)))
    })
    @GetMapping("/password/certification")
    ResponseEntity<Void> checkCertificationNumber(
            @Parameter(description = "인증 번호를 발급한 이메일 주소", example = "kimLeeChoi@mail.com", required = true)
            @RequestParam @Email String email,
            @Parameter(description = "발급받은 인증 번호", example = "4865", required = true)
            @RequestParam int code){
        searchMemberFacadeService.checkCertificationNumber(email, code);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "인증 여부 확인 메서드", description = "비밀번호 재설정을 위한 본인 인증 여부를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 인증 여부 확인됨"),
            @ApiResponse(responseCode = "400", description = "Bad Request : 닉네임이 일치하지 않음",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class)))
    })
    @GetMapping("/password")
    ResponseEntity<Void> checkCertificationStatus(
            @Parameter(description = "회원 정보 확인을 위한 이메일 주소", example = "kimLeeChoi@mail.com", required = true)
            @RequestParam @Email String email,
            @Parameter(description = "회원 정보 확인을 위한 닉네임", example = "kimLeeChoi21", required = true)
            @RequestParam String nickname){
        searchMemberFacadeService.checkCertificationStatus(nickname, email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "비밀번호 재설정 메서드", description = "새로운 비밀번호를 입력받아 재설정 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok : 비밀번호 변경 성공"),
            @ApiResponse(responseCode = "401", description = "Unauthorized : 이메일 인증 필요",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Not Found : 존재하지 않는 멤버",
                    content = @Content(schema = @Schema(implementation=ErrorResponse.class)))
    })
    @PatchMapping("/password")
    ResponseEntity<Void> updatePassword(@RequestBody PasswordUpdateDto passwordUpdateDto){
        searchMemberFacadeService.updatePassword(passwordUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
