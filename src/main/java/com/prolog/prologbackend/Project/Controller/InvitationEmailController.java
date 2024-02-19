package com.prolog.prologbackend.Project.Controller;

import com.prolog.prologbackend.Project.DTO.Request.InvitationEmailDTO;
import com.prolog.prologbackend.Project.Service.InvitationEmailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author : Kim
 * Date : 2024-02-16
 * Description : 프로젝트 초대 이메일 전 CONTROLLER입니다.
 * InvitationEmailDTO에 userId 추가
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Send Email API", description = "사용자에게 초대 이메일 전송.")
public class InvitationEmailController {
    private final InvitationEmailService emailService;

    @PostMapping("/invitation/email")
    public boolean emailSend(@RequestBody InvitationEmailDTO emailDTO){
        boolean success = true;
        try{
            success = emailService.sendMailReject(emailDTO);
        } catch(Exception e){
            e.printStackTrace();
            success = false;
        }
        return success;
    }
}
