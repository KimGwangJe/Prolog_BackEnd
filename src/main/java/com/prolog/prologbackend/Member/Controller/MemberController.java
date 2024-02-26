package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Member.DTO.Response.SimpleMemberDto;
import com.prolog.prologbackend.Member.Domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    @GetMapping
    ResponseEntity getMember(@AuthenticationPrincipal Member member){
        return ResponseEntity.status(HttpStatus.OK).body(SimpleMemberDto.of(member));
    }
}
