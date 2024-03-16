package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Member.DTO.Request.MemberJoinDto;
import com.prolog.prologbackend.Member.Service.AnyMemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AnyMemberController {
    private final AnyMemberService anyMemberService;

    @PostMapping("/signup")
    ResponseEntity joinMember(@Valid @RequestBody MemberJoinDto joinDto){
        anyMemberService.joinMember(joinDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/oauth/kakao/login")
    ResponseEntity socialLoginMember(@RequestParam String code){
        Map tokens = anyMemberService.loginToKaKao(code);
        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }

    @GetMapping("/email")
    ResponseEntity validateEmail(@RequestParam @Email String email){
        if (anyMemberService.validateEmail(email))
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        else
            return ResponseEntity.status(HttpStatus.OK).build();
    }
}
