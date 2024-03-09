package com.prolog.prologbackend.Project.Controller;

import com.prolog.prologbackend.Project.DTO.Request.InvitationEmailDTO;
import com.prolog.prologbackend.Project.Service.InvitationEmailService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@Tag(name = "Send Email API", description = "사용자에게 초대 이메일 전송.")
public class InvitationEmailController {
    private final InvitationEmailService emailService;

    @PostMapping("/api/invitation/email")
    public ResponseEntity<Void> emailSend(
            @Parameter(name = "emailDTO", description = "초대 이메일 전송을 위한 데이터를 받습니다.", required = true)
            @Valid @RequestBody InvitationEmailDTO emailDTO){
        try{
            emailService.sendMail(emailDTO);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
