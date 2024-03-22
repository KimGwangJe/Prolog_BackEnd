package com.prolog.prologbackend.Member.Controller;

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

    @PostMapping("/password")
    ResponseEntity issueCertificationNumber(@RequestParam @Email String email){
        searchMemberService.issueCertificationNumber(email);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
