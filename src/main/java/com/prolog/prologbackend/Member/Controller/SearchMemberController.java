package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Member.DTO.Request.PasswordUpdateDto;
import com.prolog.prologbackend.Member.Service.SearchMemberService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchMemberController {
    private final SearchMemberService searchMemberService;

    @GetMapping("/email")
    ResponseEntity searchEmail(@RequestParam @NotBlank String nickname, @RequestParam @NotBlank String phone){
        String email = searchMemberService.findEmail(nickname, phone);
        return ResponseEntity.status(HttpStatus.OK).body(email);
    }

    @PostMapping("/password/certification")
    ResponseEntity issueCertificationNumber(@RequestParam @Email String email){
        searchMemberService.issueCertificationNumber(email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/password/certification")
    ResponseEntity checkCertificationNumber(@RequestParam @Email String email, @RequestParam String code){
        searchMemberService.checkCertificationNumber(email, code);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/password")
    ResponseEntity checkCertificationStatus(@RequestParam @Email String email, @RequestParam String nickname){
        searchMemberService.checkCertificationStatus(nickname, email);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/password")
    ResponseEntity updatePassword(@RequestBody PasswordUpdateDto passwordUpdateDto){
        searchMemberService.updatePassword(passwordUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
