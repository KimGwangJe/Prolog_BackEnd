package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Member.DTO.Request.MemberJoinDto;
import com.prolog.prologbackend.Member.Service.MemberJoinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AnyMemberController {
    private final MemberJoinService memberJoinService;

    @PostMapping("/signup")
    ResponseEntity joinMember(@Valid @RequestBody MemberJoinDto joinDto){
        memberJoinService.joinMember(joinDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/oauth/kakao/login")
    ResponseEntity socialLoginMember(@RequestParam String code){
        Map tokens = memberJoinService.loginToKaKao(code);
        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }
}
