package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Member.DTO.Request.MemberUpdateDto;
import com.prolog.prologbackend.Member.DTO.Response.SimpleMemberDto;
import com.prolog.prologbackend.Member.Domain.Member;
import com.prolog.prologbackend.Member.Service.MemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    ResponseEntity getMember(@AuthenticationPrincipal Member member){
        return ResponseEntity.status(HttpStatus.OK).body(SimpleMemberDto.of(member));
    }

    @GetMapping("/email")
    ResponseEntity validateEmail(@AuthenticationPrincipal Member member,
                                 @RequestParam @Email String email){
        if (memberService.validateEmail(member, email))
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        else
            return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping
    ResponseEntity updateMember(@AuthenticationPrincipal Member member,
                                @RequestBody @Valid MemberUpdateDto dto){
        memberService.updateMember(member.getEmail(), dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    ResponseEntity withdrawMember(@AuthenticationPrincipal Member member){
        memberService.removeMember(member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
