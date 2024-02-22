package com.prolog.prologbackend.Member.Controller;

import com.prolog.prologbackend.Member.DTO.Request.MemberJoinDto;
import com.prolog.prologbackend.Member.Service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping
    ResponseEntity joinMember(@Valid @RequestBody MemberJoinDto joinDto){
        memberService.joinMember(joinDto);
        return ResponseEntity.status(CREATED).build();
    }
}
